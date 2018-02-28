package by.zinovich.javastudy.web;

import by.zinovich.javastudy.api.dao.PersonsDAO;
import by.zinovich.javastudy.api.domain.Person;
import by.zinovich.javastudy.exceptions.DaoException;
import by.zinovich.javastudy.exceptions.MyServletException;
import by.zinovich.javastudy.impl.dao.DaoFactoryImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Properties;

public class ServletTest extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doRequest(request, response);
    }

    protected void doRequest(HttpServletRequest req, HttpServletResponse response) throws IOException {

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();

        /// MODEL ///
        try (PersonsDAO personsDAO = new DaoFactoryImpl().getPersonsDAO()) {
            String action = req.getParameter("action");
            String personIdStr = req.getParameter("person_id");
            String personFirstName = req.getParameter("Firt_name") == null ? "" : req.getParameter("Firt_name");
            String personSecondName = req.getParameter("Second_name") == null ? "" : req.getParameter("Second_name");
            String personLogin = req.getParameter("login") == null ? "" : req.getParameter("login");
            String personPassword = req.getParameter("password") == null ? "" : req.getParameter("password");

            // Delete button push handler
            if (action != null && action.equals("удалить")) {
                deletePerson(personIdStr, personsDAO);
                pw.println(wrapIntoHtmlParagraph("Пользователь с person_id = " + personIdStr + " удален."));
            }

            // Add new user button push handler
            if (action != null && action.equals("Add_new_Person")) {
                addPerson(personFirstName, personSecondName, personLogin, personsDAO);
                pw.println(wrapIntoHtmlParagraph("Новый пользователь добавлен."));
            }

            // Go to add new person button push handler
            if (action != null && action.equals("Go_to_add_Person_form")) {
                /// Add Person
                pw.println(createHtmlForAddPerson("", "", ""));
                return;
            }

            if (action != null && action.equals("редактировать")) {
                Integer usrIdForEdit = Integer.parseInt(personIdStr);
                Person personForEdit = personsDAO.getPersonByPersonId(usrIdForEdit);
                pw.println(createHtmlForEditPerson(personForEdit));
                return;
            }

            if (action != null && action.equals("Save_Edited_data")) {
                Person personForEdit = new Person(Integer.parseInt(personIdStr), personFirstName, personSecondName, personLogin, personPassword);
                personsDAO.updatePerson(personForEdit);
                pw.println(wrapIntoHtmlParagraph("Данные изменены."));
            }

            // Add new user button html
            pw.println(createHtmlForAddPersonButton());

            /// Show all Persons
            pw.println(createHtmlTableOfPersonsList(personsDAO.getAllPersons()));
        } catch (MyServletException es) {
            pw.println(wrapIntoHtmlParagraph( es.getMessage()));
            pw.println(createHtmlForMainMenuButton());
        } catch (DaoException ed) {
            pw.println(wrapIntoHtmlParagraph( ed.getMessage()));
            pw.println(createHtmlForMainMenuButton());
        } catch (Exception e) {
            pw.println(wrapIntoHtmlParagraph("Произошла ошибка. Дополнительные сведения в log-файле."));
            logError(e);
            pw.println(createHtmlForMainMenuButton());
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    private String wrapIntoHtmlParagraph(String message) {
        return "<p>" + message + "</p>";
    }

    private void deletePerson(String userStringId, PersonsDAO personsDAO) throws MyServletException {
        try {
            Integer userIdForDelete = Integer.parseInt(userStringId);
            personsDAO.deletePerson(userIdForDelete);
        } catch (DaoException e) {
            String exceptionMsg = "При попытке обращения к данным о пользователях произошел сбой.";
            try {
                logError(e);
            } catch (IOException e1) {
                // добавляем сбой логирования, если он был
                exceptionMsg += "\n При попытке записать сведения об этой ошибке произошел сбой.";
            }
            // сбой при обращении к данным
            throw new MyServletException(exceptionMsg);
        }
    }

    private void addPerson(String userFirstNameForAdd, String userSecondNameForAdd, String userLoginForAdd, PersonsDAO personsDAO) throws MyServletException {
        if ((userFirstNameForAdd != null && !"".equals(userFirstNameForAdd)) &&
                (userSecondNameForAdd != null && !"".equals(userSecondNameForAdd)) &&
                (userLoginForAdd != null && !"".equals(userLoginForAdd))
                ) {
            try {
                personsDAO.addPerson(new Person(userFirstNameForAdd, userSecondNameForAdd, userLoginForAdd));
            } catch (DaoException e) {
                String exceptionMsg = "При попытке добавления пользователя произошел сбой.";
                try {
                    logError(e);
                } catch (IOException e1) {
                    exceptionMsg += "При попытке записать сведения об ошибке произошел сбой.";
                }
                throw new MyServletException(exceptionMsg);
            }
        } else {
            throw new MyServletException("Все поля должны быть заполнены!");
        }
    }

    private void logError(Exception e) throws IOException {
        Properties logProps = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("Logging.properties");
        FileWriter logWriter = null;
        try {
            logProps.load(input);

            File log = new File(logProps.getProperty("LogFile"));
            logWriter = new FileWriter(log, true);
            for (StackTraceElement element : e.getStackTrace()) {
                logWriter.write(element.toString() + '\n');
            }
            logWriter.flush();
        } finally {
            if (logWriter != null) {
                logWriter.close();
            }
            if (input != null) {
                input.close();
            }
        }
    }

    /// VIEW ///
    private StringBuffer createHtmlTableOfPersonsList(List<Person> list) {
        StringBuffer personsHtmlTable = new StringBuffer("<br> <B> Список пользователей </B>" +
                "<table border=5>" +
                "<td><b>Имя<b></td> <td><b>Фамилия<b></td> <td><b>login<b></td> <td><b>password<b></td> <td><b>person_id<b></td> <td><b>Удалить<b></td> <td><b>Редактировать<b></td>" +
                "<tr>");

        for (Person p : list) {
            personsHtmlTable.append(
                    "<td>" + p.getPersonNameFirst() + "</td>" +
                            "<td>" + p.getPersonNameSecond() + "</td>" +
                            "<td>" + p.getPersonLogin() + "</td>" +
                            "<td>" + p.getPersonPassword() + "</td>" +
                            "<td>" + p.getPersonId() + "</td>" +
                            "<td><form action=\"test\" method=\"GET\"> <input type=\"hidden\" name=\"person_id\" value =\"" +
                            +p.getPersonId() +
                            "\"> <input type=\"submit\" name=\"action\" value =\"удалить\" /> </form></td>" +
                            "<td><form action=\"test\" method=\"GET\"> <input type=\"hidden\" name=\"person_id\" value =\"" +
                            +p.getPersonId() +
                            "\"> <input type=\"submit\" name=\"action\" value =\"редактировать\" /> </form></td>" +
                            "</tr>");
        }
        personsHtmlTable.append("</table>");
        return personsHtmlTable;
    }

    private String createHtmlForAddPerson(String firstName, String secondName, String login) {
        return "<form action=\"test\" method=\"GET\">" +
                "<p>Чтобы добавить пользователя укажите все необходимые данные: <br> " +
                "Firt_name:___<input type=\"text\" name=\"Firt_name\" value=\"" + firstName + "\"> <br>" +
                "Second_name:_<input type=\"text\" name=\"Second_name\" value=\"" + secondName + "\"> <br>" +
                "Login:_______<input type=\"text\" name=\"login\" value=\"" + login + "\"> <br>" +
                "<input type=\"hidden\" name=\"action\" value =\"Add_new_Person\"/>" +
                "<input type=\"submit\" value=\"Добавить\" /><br> </p>" +
                "</form>" + createHtmlForMainMenuButton();
    }

    private String createHtmlForAddPersonButton() {
        return "<form action=\"test\" method=\"GET\">" +
                "<input type=\"hidden\" name=\"action\" value =\"Go_to_add_Person_form\"/>" +
                "<input type=\"submit\" value=\"Добавить нового пользователя\" /><br> </p>" +
                "</form>";
    }

    private String createHtmlForMainMenuButton() {
        return "<form action=\"test\" method=\"GET\">" +
                "<input type=\"hidden\" name=\"action\" value =\"Go_to_Persons_List\"/>" +
                "<input type=\"submit\" value = \"Вернуться к списку пользователей\" a href =\"test\" /><br> </p>" +
                "</form>";
    }

    private String createHtmlForEditPerson(Person person) {
        return "<form action=\"test\" method=\"GET\">" +
                "person_id: <input type=\"hidden\" name=\"person_id\" value=\"" + person.getPersonId() + "\"> <br>" +
                "Firt_name: <input type=\"text\" name=\"Firt_name\" value=\"" + person.getPersonNameFirst() + "\"> <br>" +
                "Second_name: <input type=\"text\" name=\"Second_name\" value=\"" + person.getPersonNameSecond() + "\"> <br>" +
                "Login: <input type=\"text\" name=\"login\" value=\"" + person.getPersonLogin() + "\"> <br>" +
                "Password: <input type=\"text\" name=\"password\" value=\"" + person.getPersonPassword() + "\"> <br>" +
                "<input type=\"hidden\" name=\"action\" value =\"Save_Edited_data\"/>" +
                "<input type=\"submit\" value=\"Сохранить изменения\" /><br> </p>" +
                "</form>" + createHtmlForMainMenuButton();
    }
}
