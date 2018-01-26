package by.zinovich.javastudy.web;

import by.zinovich.javastudy.api.dao.PaymentsDAO;
import by.zinovich.javastudy.api.domain.Payment;
import by.zinovich.javastudy.api.domain.Person;
import by.zinovich.javastudy.exceptions.DaoException;
import by.zinovich.javastudy.impl.dao.DaoFactoryImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

public class ServletPayments extends HttpServlet {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.println("<br> <B> List of Persons </B>");
        pw.println("<table border=5>");
        pw.println("<td><b>Payment Id<b></td>");
        pw.println("<td><b>Description<b></td>");
        pw.println("<td><b>Price value<b></td>");
        pw.println("<td><b>Group id<b></td>");
        pw.println("<td><b>date<b></td>");
        pw.println("<td><b>person id<b></td>");
        try {
            PaymentsDAO paymentsDAO = new DaoFactoryImpl().getPaymentsDao();
            for (Payment p : paymentsDAO.getAllPaymentList()) {
                pw.println("<tr>");
                pw.println("<td>" + p.getPaymentId() + "</td>");
                pw.println("<td>" + p.getDescription() + "</td>");
                pw.println("<td>" + p.getCost() + "</td>");
                pw.println("<td>" + p.getGroupOfPaymentId() + "</td>");
                pw.println("<td>" + simpleDateFormat.format(p.getDate()) + "</td>");
                pw.println("<td>" + p.getPersonId() + "</td>");
                pw.println("</tr>");
            }
        } catch (DaoException e) {
            e.printStackTrace();
        }
        pw.println("</table>");
    }
}
