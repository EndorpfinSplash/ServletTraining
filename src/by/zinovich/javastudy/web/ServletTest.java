package by.zinovich.javastudy.web;

import by.zinovich.javastudy.api.dao.PersonsDAO;
import by.zinovich.javastudy.api.domain.Person;
import by.zinovich.javastudy.exceptions.DaoException;
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
        try {
            PersonsDAO personsDAO = new DaoFactoryImpl().getPersonsDAO();
            // Delete person
            pw.println(createHtmlForDeletePerson());
            String userStringId = req.getParameter("user_id_for_deleting") == null ? "" : req.getParameter("user_id_for_deleting");
            pw.println("<p> " + deletePerson(userStringId, personsDAO) + "</p>");

            /// Add Person
            String userFirstNameForAdd = req.getParameter("Firt_name_add") == null ? "" : req.getParameter("Firt_name_add");
            String userSecondNameForAdd = req.getParameter("Second_name_add") == null ? "" : req.getParameter("Second_name_add");
            String userLoginForAdd = req.getParameter("login_for_add") == null ? "" : req.getParameter("login_for_add");

            pw.println(createHtmlForAddPerson(userFirstNameForAdd, userSecondNameForAdd, userLoginForAdd));
            pw.println("<p> " + addPerson(userFirstNameForAdd,userSecondNameForAdd,userLoginForAdd,personsDAO) + "</p>");

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

    private String deletePerson(String userStringId, PersonsDAO personsDAO) {
        String result;
        try {

            Integer userIdForDelete = Integer.parseInt(userStringId);

            int countRecForDelPersonId = personsDAO.countPersonsRecords(userIdForDelete);

            if (countRecForDelPersonId == 1) {
                personsDAO.deletePerson(userIdForDelete);
                result = ("Пользователь с user_id = " + userIdForDelete + " удален.");
            } else {
                result = "Пользователя с user_id = " + userIdForDelete + " нет в таблице.";
            }

        } catch (NumberFormatException e) {
            result = " Чтобы удалить пользователя, необходимо ввести его номер user_id";
        } catch (DaoException e) {
            result = "При попытке обращения к данным о пользователях произошел сбой. ";
            try {
                logError(e);
            } catch (IOException e1) {
                result += "При попытке записать сведения об ошибке произошел сбой.";
            }
        }
        return result;
    }

    private String addPerson(String userFirstNameForAdd, String userSecondNameForAdd, String userLoginForAdd, PersonsDAO personsDAO){
        String result;
        if ((userFirstNameForAdd != null && !"".equals(userFirstNameForAdd)) &&
                (userSecondNameForAdd != null && !"".equals(userSecondNameForAdd)) &&
                (userLoginForAdd != null && !"".equals(userLoginForAdd))
                ) {

            try {
                personsDAO.addPerson(new Person(userFirstNameForAdd, userSecondNameForAdd, userLoginForAdd));
            } catch (DaoException e) {
                result = "При попытке добавления пользователя произошел сбой.";
                try {
                    logError(e);
                } catch (IOException e1) {
                    result += "При попытке записать сведения об ошибке произошел сбой.";
                    return result;
                }
            }
            result = "Новый пользователь добавлен";

        } else {
            result = "Все поля должны быть заполнены!";
        }
        return result;
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
        StringBuffer personsHtmlTable = new StringBuffer("<br> <B> List of Persons </B>" +
                "<table border=5>" +
                "<td><b>Имя<b></td> <td><b>Фамилия<b></td> <td><b>login<b></td> <td><b>password<b></td> <td><b>user_id<b></td>" +
                "<tr>");

        for (Person p : list) {
            personsHtmlTable.append(
                    "<td>" + p.getPersonNameFirst() + "</td>" +
                            "<td>" + p.getPersonNameSecond() + "</td>" +
                            "<td>" + p.getPersonLogin() + "</td>" +
                            "<td>" + p.getPersonPassword() + "</td>" +
                            "<td>" + p.getPersonId() + "</td></tr>");
        }
        personsHtmlTable.append("</table>");
        return personsHtmlTable;
    }

    private String createHtmlForDeletePerson() {
        return "<form action=\"test\" method=\"GET\">" +
                "<p>Введите user_id пользователя, которого необходимо удалить: " +
                "<input type=\"text\" name=\"user_id_for_deleting\">" +
                "<input type=\"submit\" value=\"Удалить\" /> </p>" +
                "</form>";
    }

    private String createHtmlForAddPerson(String firstName, String secondName, String login) {
        return "<form action=\"test\" method=\"GET\">" +
                "<p>Чтобы добавить пользователя укажите все необходимые данные: <br> " +
                "Firt_name:___<input type=\"text\" name=\"Firt_name_add\" value=\"" + firstName + "\"> <br>" +
                "Second_name:_<input type=\"text\" name=\"Second_name_add\" value=\"" + secondName + "\"> <br>" +
                "Login:_______<input type=\"text\" name=\"login_for_add\" value=\"" + login + "\"> <br>" +
                "<input type=\"submit\" value=\"Добавить\" /><br> </p>" +
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
