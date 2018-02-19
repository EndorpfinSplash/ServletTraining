package by.zinovich.javastudy.web;

import by.zinovich.javastudy.api.dao.PersonsDAO;
import by.zinovich.javastudy.api.domain.Person;
import by.zinovich.javastudy.impl.dao.DaoFactoryImpl;

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
        Properties logProps = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("Logging.properties");

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        String systemInfo = "";

        try {
            pw.println(createHtmlForDeletePerson());
            //  pw.println(createHtmlForEditPerson());

            Integer userIdForDelete = null;

            try {
                String userStringId = req.getParameter("user_id_for_deleting");
                if (userStringId != null && !"".equals(userStringId)) {
                    userIdForDelete = Integer.parseInt(userStringId);
                }
            } catch (NumberFormatException e) {
                systemInfo = " Чтобы удалить пользователя, необходимо ввести его номер user_id";
            }

            PersonsDAO personsDAO = new DaoFactoryImpl().getPersonsDAO();
            if (userIdForDelete != null ) {
                int countRecForDelPersonId = personsDAO.countPersonsRecords(userIdForDelete);

                if (countRecForDelPersonId == 1) {
                    personsDAO.deletePerson(userIdForDelete);
                    systemInfo = (" Пользователь с user_id = " + userIdForDelete + " удален.");
                } else {
                    systemInfo = "Пользователя с user_id = " + userIdForDelete + " нет в таблице.";
                }
            }

            if ("".equals(userIdForDelete)) {
                systemInfo = "Чтобы удалить пользователя, необходимо ввести его user_id </p>";
            }
            pw.println("<p> " + systemInfo + "</p>");
            pw.println(createHtmlTableOfPersonsList(personsDAO.getAllPersons()));

        } catch (Exception e) {
            pw.println("<p> Произошла ошибка. Дополнительные сведения в log-файле.</p>");

            logProps.load(input);
            File log = new File(logProps.getProperty("LogFile"));
            try (FileWriter logWriter = new FileWriter(log, true)) {
                for (StackTraceElement element : e.getStackTrace()) {
                    logWriter.write(element.toString() + '\n');
                }
                logWriter.flush();
            } finally {
                if (pw != null) {
                    pw.close();
                }
            }
        }
    }

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
                "<p>Введите user_id пользователя, которого необходимо удалить: <input type=\"text\" name=\"user_id_for_deleting\">" +
                "<input type=\"submit\" value=\"Удалить\" /> </p>" +
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
