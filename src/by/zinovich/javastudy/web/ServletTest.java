package by.zinovich.javastudy.web;

import by.zinovich.javastudy.api.dao.PersonsDAO;
import by.zinovich.javastudy.api.domain.Person;
import by.zinovich.javastudy.exceptions.DaoException;
import by.zinovich.javastudy.impl.dao.DaoFactoryImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Properties;

public class ServletTest extends HttpServlet {
    String action;
    String personIdStr;
    String personFirstName;
    String personSecondName;
    String personLogin;
    String personPassword;

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

            readParamsFromRequest(req);

            // Delete button push handler
            if (action != null && action.equals("удалить")) {
                deletePerson(personIdStr, personsDAO, pw);
                return;
            }

            // Add new user button push handler
            if (action != null && action.equals("Add_new_Person")) {
                addPerson(personFirstName, personSecondName, personLogin, personsDAO, pw);
                return;
            }

            // Go to Form for adding new person button push handler
            if (action != null && action.equals("Go_to_add_Person_form")) {
                createFormForAddPerson("", "", "", pw);
                createButtonGoToPersonsListMenu(pw);
                return;
            }

            // Edit person button push handler
            if (action != null && action.equals("редактировать")) {
                createEditPersonForm(personsDAO, pw);
                return;
            }

            // Save edited person's data button push handler
            if (action != null && action.equals("Save_Edited_data")) {
                saveEditedPersonData(personsDAO, pw);
                return;
            }

            // Go to list of person button push handler
            if (action == null || action.equals("Go_to_Persons_List")) {
                createButtonForAddPerson(pw);
                createTableOfPersonsList(personsDAO, pw);
                return;
            }

        } catch (DaoException ed) {
            pw.println(wrapIntoHtmlParagraph(ed.getMessage()));
            createButtonGoToPersonsListMenu(pw);
        } catch (Exception e) {
            pw.println(wrapIntoHtmlParagraph("Произошла ошибка. Дополнительные сведения в log-файле."));
            logError(e);
            createButtonGoToPersonsListMenu(pw);
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    private void readParamsFromRequest(HttpServletRequest req) {
        this.action = req.getParameter("action");
        this.personIdStr = req.getParameter("person_id");
        this.personFirstName = req.getParameter("Firt_name") == null ? "" : req.getParameter("Firt_name");
        this.personSecondName = req.getParameter("Second_name") == null ? "" : req.getParameter("Second_name");
        this.personLogin = req.getParameter("login") == null ? "" : req.getParameter("login");
        this.personPassword = req.getParameter("password") == null ? "" : req.getParameter("password");
    }

    private String wrapIntoHtmlParagraph(String message) {
        return "<p>" + message + "</p>";
    }

    private void deletePerson(String userStringId, PersonsDAO personsDAO, PrintWriter pw) throws DaoException {
        Integer userIdForDelete = Integer.parseInt(userStringId);
        personsDAO.deletePerson(userIdForDelete);
        pw.println(wrapIntoHtmlParagraph("Пользователь с person_id = " + userStringId + " удален."));
        createButtonForAddPerson(pw);
        createTableOfPersonsList(personsDAO, pw);
    }

    public void saveEditedPersonData(PersonsDAO personsDAO, PrintWriter pw) throws DaoException {
        Person personForEdit = new Person(Integer.parseInt(personIdStr), personFirstName, personSecondName, personLogin, personPassword);
        personsDAO.updatePerson(personForEdit);
        pw.println(wrapIntoHtmlParagraph("Данные изменены."));
        createButtonForAddPerson(pw);
        createTableOfPersonsList(personsDAO, pw);
    }

    public void createEditPersonForm(PersonsDAO personsDAO, PrintWriter pw) throws DaoException {
        Integer usrIdForEdit = Integer.parseInt(personIdStr);
        Person personForEdit = personsDAO.getPersonByPersonId(usrIdForEdit);
        createFormForEditPerson(personForEdit, pw);
        createButtonGoToPersonsListMenu(pw);
    }

    private void addPerson(String userFirstNameForAdd, String userSecondNameForAdd, String userLoginForAdd, PersonsDAO personsDAO, PrintWriter pw) throws DaoException {
        if ((userFirstNameForAdd != null && !"".equals(userFirstNameForAdd)) &&
                (userSecondNameForAdd != null && !"".equals(userSecondNameForAdd)) &&
                (userLoginForAdd != null && !"".equals(userLoginForAdd))
                ) {
            personsDAO.addPerson(new Person(userFirstNameForAdd, userSecondNameForAdd, userLoginForAdd));
            pw.println("<p>Новый пользователь добавлен.</p>");
            createButtonForAddPerson(pw);
            createTableOfPersonsList(personsDAO, pw);
        } else {
            pw.println("<p>Все поля должны быть заполнены!</p>");
            createFormForAddPerson(userFirstNameForAdd, userSecondNameForAdd, userLoginForAdd, pw);
            createButtonGoToPersonsListMenu(pw);
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
    private void createTableOfPersonsList(PersonsDAO personsDAO, PrintWriter pw) throws DaoException {
        List<Person> list = personsDAO.getAllPersons();

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
        pw.print(personsHtmlTable);
    }

    private void createFormForAddPerson(String firstName, String secondName, String login, PrintWriter pw) {
        pw.println("<form action=\"test\" method=\"GET\">" +
                "<p>Чтобы добавить пользователя укажите все необходимые данные: <br> " +
                "Firt_name:___<input type=\"text\" name=\"Firt_name\" value=\"" + firstName + "\"> <br>" +
                "Second_name:_<input type=\"text\" name=\"Second_name\" value=\"" + secondName + "\"> <br>" +
                "Login:_______<input type=\"text\" name=\"login\" value=\"" + login + "\"> <br>" +
                "<input type=\"hidden\" name=\"action\" value =\"Add_new_Person\"/>" +
                "<input type=\"submit\" value=\"Добавить\" /><br> </p>" +
                "</form>");
    }

    private void createButtonForAddPerson(PrintWriter pw) {
        pw.println("<form action=\"test\" method=\"GET\">" +
                "<input type=\"hidden\" name=\"action\" value =\"Go_to_add_Person_form\"/>" +
                "<input type=\"submit\" value=\"Добавить нового пользователя\" /><br> </p>" +
                "</form>");
    }

    private void createButtonGoToPersonsListMenu(PrintWriter pw) {
        pw.println("<form action=\"test\" method=\"GET\">" +
                "<input type=\"hidden\" name=\"action\" value =\"Go_to_Persons_List\"/>" +
                "<input type=\"submit\" value = \"Вернуться к списку пользователей\" a href =\"test\" /><br> </p>" +
                "</form>");
    }

    private void createFormForEditPerson(Person person, PrintWriter pw) {
        pw.println("<form action=\"test\" method=\"GET\">" +
                "person_id: <input type=\"hidden\" name=\"person_id\" value=\"" + person.getPersonId() + "\"> <br>" +
                "Firt_name: <input type=\"text\" name=\"Firt_name\" value=\"" + person.getPersonNameFirst() + "\"> <br>" +
                "Second_name: <input type=\"text\" name=\"Second_name\" value=\"" + person.getPersonNameSecond() + "\"> <br>" +
                "Login: <input type=\"text\" name=\"login\" value=\"" + person.getPersonLogin() + "\"> <br>" +
                "Password: <input type=\"text\" name=\"password\" value=\"" + person.getPersonPassword() + "\"> <br>" +
                "<input type=\"hidden\" name=\"action\" value =\"Save_Edited_data\"/>" +
                "<input type=\"submit\" value=\"Сохранить изменения\" /><br> </p>" +
                "</form>");
    }
}
