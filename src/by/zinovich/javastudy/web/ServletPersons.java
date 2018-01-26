package by.zinovich.javastudy.web;

import by.zinovich.javastudy.api.dao.PersonsDAO;
import by.zinovich.javastudy.api.domain.Person;
import by.zinovich.javastudy.exceptions.DaoException;
import by.zinovich.javastudy.impl.dao.DaoFactoryImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ServletPersons extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.println("<br> <B> List of Persons </B>");
        pw.println("<table border=5>");
        pw.println("<td><b>Имя<b></td>");
        pw.println("<td><b>Фамилия<b></td>");
        pw.println("<td><b>login<b></td>");
        pw.println("<td><b>password<b></td>");
        pw.println("<td><b>user_id<b></td>");
        try {
            PersonsDAO personsDAO = new DaoFactoryImpl().getPersonsDAO();
            for (Person p : personsDAO.getAllPersons()) {
                pw.println("<tr>");
                pw.println("<td>" + p.getPersonNameFirst() + "</td>");
                pw.println("<td>" + p.getPersonNameSecond() + "</td>");
                pw.println("<td>" + p.getPersonLogin() + "</td>");
                pw.println("<td>" + p.getPersonPassword() + "</td>");
                pw.println("<td>" + p.getPersonId() + "</td>");
                pw.println("</tr>");
            }
        } catch (DaoException e) {
            e.printStackTrace();
        }
        pw.println("</table>");
    }
}
