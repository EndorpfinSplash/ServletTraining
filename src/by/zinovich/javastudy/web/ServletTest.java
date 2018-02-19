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
import java.util.Properties;

public class ServletTest extends HttpServlet {

    Properties logProps = new Properties();
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    InputStream input = classLoader.getResourceAsStream("Logging.properties");

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doRequest(request, response);
    }

    protected void doRequest(HttpServletRequest req, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();

        try {

            pw.println("<form action=\"test\" method=\"GET\">" +
                    "<p>Введите user_id пользователя, которого необходимо удалить: <input type=\"text\" name=\"user_id_for_deleting\">" +
                    "<input type=\"submit\" value=\"Удалить\" /> </p>" +
                    "</form>");

            pw.println("<form action=\"test\" method=\"GET\">" +
                    "<p>Введите user_id пользователя, которого необходимо редактировать: <br> " +
                    "User_id: <input type=\"text\" name=\"user_id_for_edit\"> <br>" +
                    "Firt_name: <input type=\"text\" name=\"Firt_name_edit\"> <br>" +
                    "Second_name: <input type=\"text\" name=\"Second_name_edit\"> <br>" +
                    "Login: <input type=\"text\" name=\"login_for_edit\"> <br>" +
                    "<input type=\"submit\" value=\"Редактировать\" /><br> </p>" +
                    "</form>");

            String userIdForDelete = req.getParameter("user_id_for_deleting");
            PersonsDAO personsDAO = new DaoFactoryImpl().getPersonsDAO();
            if (userIdForDelete != null && !"".equals(userIdForDelete)) {
                int countRecForDelPersonId = personsDAO.countPersonsRecords(Integer.parseInt(userIdForDelete));

                if (countRecForDelPersonId == 1) {
                    personsDAO.deletePerson(Integer.parseInt(userIdForDelete));
                    pw.println("<p> Пользователь с user_id = " + userIdForDelete + " удален </p>");
                } else {
                    pw.println("<p> Пользователя с user_id = " + userIdForDelete + " нет в таблице </p>");
                }

            }
            if ("".equals(userIdForDelete)) {
                pw.println("<p>" + "Чтобы удалить пользователя, необходимо ввести его user_id" + "</p>");

            }

            pw.println("<br> <B> List of Persons </B>");
            pw.println("<table border=5>");
            pw.println("<td><b>Имя<b></td>");
            pw.println("<td><b>Фамилия<b></td>");
            pw.println("<td><b>login<b></td>");
            pw.println("<td><b>password<b></td>");
            pw.println("<td><b>user_id<b></td>");

            for (Person p : personsDAO.getAllPersons()) {
                pw.println("<tr>");
                pw.println("<td>" + p.getPersonNameFirst() + "</td>");
                pw.println("<td>" + p.getPersonNameSecond() + "</td>");
                pw.println("<td>" + p.getPersonLogin() + "</td>");
                pw.println("<td>" + p.getPersonPassword() + "</td>");
                pw.println("<td>" + p.getPersonId() + "</td>");
                pw.println("</tr>");
            }
            pw.println("</table>");

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
}
