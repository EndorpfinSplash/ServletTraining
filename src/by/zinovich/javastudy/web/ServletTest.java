package by.zinovich.javastudy.web;

import by.zinovich.javastudy.api.dao.PersonsDAO;
import by.zinovich.javastudy.api.domain.Person;
import by.zinovich.javastudy.exceptions.DaoException;
import by.zinovich.javastudy.exceptions.MyServletException;
import by.zinovich.javastudy.impl.dao.DaoFactoryImpl;
import com.sun.javafx.iio.ios.IosDescriptor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.ParseException;
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

            // Delete button push handler
            String usrIdForDelByButtonStr = req.getParameter("usr_id_for_del_by_btn");
            if (usrIdForDelByButtonStr != null) {
                try {
                    deletePerson(usrIdForDelByButtonStr, personsDAO);
                    pw.println("<p> Пользователь с user_id = " + usrIdForDelByButtonStr + " удален.</p>");
                } catch (MyServletException e) {
                    pw.println("<p>" + e.getMessage() + "</p>");
                }
            }

            // Add new user button push handler
            String addNewUserButtonPushed = req.getParameter("Add_person_button_push");
            if (addNewUserButtonPushed != null) {
                String userFirstNameForAdd = req.getParameter("Firt_name_add") == null ? "" : req.getParameter("Firt_name_add");
                String userSecondNameForAdd = req.getParameter("Second_name_add") == null ? "" : req.getParameter("Second_name_add");
                String userLoginForAdd = req.getParameter("login_for_add") == null ? "" : req.getParameter("login_for_add");
                try {
                    addPerson(userFirstNameForAdd, userSecondNameForAdd, userLoginForAdd, personsDAO);
                    pw.println("<p> Новый пользователь добавлен.</p>");
                } catch (MyServletException e) {
                    pw.println(createHtmlForAddPerson(userFirstNameForAdd, userSecondNameForAdd, userLoginForAdd));
                    pw.println("<p> " + e.getMessage() + "</p>");
                    return;
                }
            }

            // Go to add new person button push handler
            String addNewUserMenuButton = req.getParameter("Add_new_user_menu");
            if (addNewUserMenuButton != null) {
                /// Add Person
                pw.println(createHtmlForAddPerson("", "", ""));
                return;
            }
            // Add new user button html
            pw.println(createHtmlForAddPersonButton());

            /// Show all Persons
            pw.println(createHtmlTableOfPersonsList(personsDAO.getAllPersons()));

        } catch (Exception e) {
            pw.println("<p> Произошла ошибка. Дополнительные сведения в log-файле.</p>");
            logError(e);
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
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
                "<td><b>Имя<b></td> <td><b>Фамилия<b></td> <td><b>login<b></td> <td><b>password<b></td> <td><b>user_id<b></td> <td><b>Операция<b></td>" +
                "<tr>");

        for (Person p : list) {
            personsHtmlTable.append(
                    "<td>" + p.getPersonNameFirst() + "</td>" +
                            "<td>" + p.getPersonNameSecond() + "</td>" +
                            "<td>" + p.getPersonLogin() + "</td>" +
                            "<td>" + p.getPersonPassword() + "</td>" +
                            "<td>" + p.getPersonId() + "</td>" +
                            "<td><form action=\"test\" method=\"GET\"> <input type=\"hidden\" name=\"usr_id_for_del_by_btn\" value =\"" +
                            +p.getPersonId() +
                            "\"> <input type=\"submit\" name=\"submit\" value =\"удалить\" /> </form></td>" +

                            "</tr>");
        }
        personsHtmlTable.append("</table>");
        return personsHtmlTable;
    }

    private String createHtmlForAddPerson(String firstName, String secondName, String login) {
        return "<form action=\"test\" method=\"GET\">" +
                "<p>Чтобы добавить пользователя укажите все необходимые данные: <br> " +
                "Firt_name:___<input type=\"text\" name=\"Firt_name_add\" value=\"" + firstName + "\"> <br>" +
                "Second_name:_<input type=\"text\" name=\"Second_name_add\" value=\"" + secondName + "\"> <br>" +
                "Login:_______<input type=\"text\" name=\"login_for_add\" value=\"" + login + "\"> <br>" +
                "<input type=\"submit\" name=\"Add_person_button_push\" value=\"Добавить\" /><br> </p>" +
                "</form>" + createHtmlForMainMenuButton();
    }

    private String createHtmlForAddPersonButton() {
        return "<form action=\"test\" method=\"GET\">" +
                "<input type=\"submit\" name=\"Add_new_user_menu\" value=\"Добавить нового пользователя\" /><br> </p>" +
                "</form>";
    }

    private String createHtmlForMainMenuButton() {
        return "<form action=\"test\" method=\"GET\">" +
                "<input type=\"submit\" name=\"Go_toMainList\" value = \"Вернуться к списку пользователей\" a href =\"test\" /><br> </p>" +
                "</form>";
    }

    private String createHtmlForEditPerson() {
        return "<form action=\"test\" method=\"GET\">" +
                "<p>Введите user_id пользователя, которого необходимо редактировать: <br> " +
                "User_id: <input type=\"text\" name=\"user_id_for_edit\"> <br>" +
                "Firt_name: <input type=\"text\" name=\"Firt_name_edit\"> <br>" +
                "Second_name: <input type=\"text\" name=\"Second_name_edit\"> <br>" +
                "Login: <input type=\"text\" name=\"login_for_edit\"> <br>" +
                "<input type=\"submit\" value=\"Редактировать\" /><br> </p>" +
                "</form>";
    }
}
