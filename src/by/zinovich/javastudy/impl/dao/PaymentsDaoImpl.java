package by.zinovich.javastudy.impl.dao;

import by.zinovich.javastudy.api.dao.PaymentsDAO;
import by.zinovich.javastudy.api.domain.Payment;
import by.zinovich.javastudy.exceptions.DaoException;
import by.zinovich.javastudy.utils.CloseResultSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PaymentsDaoImpl implements PaymentsDAO {

    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private PreparedStatement selectAllStmt;
    private PreparedStatement selectPaymentsForPersonStmt;
    private PreparedStatement insertPaymentStmt;
    private PreparedStatement updatePaymentStmt;
    private PreparedStatement deletePaymentStmt;
    List<PreparedStatement> preparedStatementList = new ArrayList<>();

    private static final String SELECT_ALL = "SELECT payment_Id, Payment_Group_id, Summa, Date_of_payment, Description, Person_id FROM Payments order by Date_of_payment";
    private static final String SELECT_ALL_FOR_PERSON = "SELECT payment_Id, Payment_Group_id, Summa, Date_of_payment, Description FROM Payments WHERE person_id = ? order by Date_of_payment";
    private static final String INSERT_PAYMENT = "INSERT into Payments (Payment_Group_id, Summa, Date_of_payment, Description, Person_id ) VALUES (?,?,CURRENT_TIMESTAMP,?,?)";
    private static final String UPDATE_PAYMENT = "UPDATE Payments SET Payment_Group_id=?, Summa=?, Description=? where payment_Id=?";
    private static final String DELETE_PAYMENT = "DELETE FROM Payments WHERE payment_Id=?";

    private Connection connection;

    public PaymentsDaoImpl(Connection connection) throws DaoException {
        this.connection = connection;
    }

    @Override
    public List<Payment> getAllPaymentList() throws DaoException {
        ResultSet resultSet = null;
        try {
            List<Payment> listPayment = new ArrayList<>();
            if (this.selectAllStmt == null) {
                this.selectAllStmt = this.connection.prepareStatement(SELECT_ALL);
            }

            resultSet = selectAllStmt.executeQuery();
            while (resultSet.next()) {
                Payment payment = new Payment();
                payment.setGroupOfPaymentId(resultSet.getInt("Payment_group_Id"));
                payment.setCost(resultSet.getInt("Summa"));
                payment.setDescription(resultSet.getString("Description"));
                payment.setPaymentId(resultSet.getInt("payment_Id"));
                payment.setDate(df.parse(resultSet.getString("Date_of_payment")));
                payment.setPersonId(resultSet.getString("person_id"));

                listPayment.add(payment);
            }
            return listPayment;
        } catch (SQLException | ParseException e) {
            throw new DaoException("Get Payment for person not added.", e);
        } finally {
            CloseResultSet.closeResultSet(resultSet, "payments.");
        }
    }

    @Override
    public List<Payment> getPaymentListForPerson(String person_id) throws DaoException {
        ResultSet rs = null;
        try {
            List<Payment> listPayment = new ArrayList<>();
            if (this.selectPaymentsForPersonStmt == null) {
                this.selectPaymentsForPersonStmt = this.connection.prepareStatement(SELECT_ALL_FOR_PERSON);
            }
            this.selectPaymentsForPersonStmt.setString(1, person_id);
            rs = selectPaymentsForPersonStmt.executeQuery();

            while (rs.next()) {
                Payment payment = new Payment();
                payment.setGroupOfPaymentId(rs.getInt("Payment_group_Id"));
                payment.setCost(rs.getInt("Summa"));
                payment.setDate(df.parse(rs.getString("Date_of_payment")));
                payment.setDescription(rs.getString("Description"));
                payment.setPaymentId(rs.getInt("payment_Id"));

                listPayment.add(payment);
            }
            return listPayment;
        } catch (SQLException | ParseException e) {
            throw new DaoException("Get Payment for person failed", e);
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new DaoException("Result set closing failure in the method Get Payment for person", e);
            }
        }
    }

    @Override
    public void addPayment(Payment payment) throws DaoException {
        ResultSet resultSet = null;
        try {
            if (this.insertPaymentStmt == null) {
                this.insertPaymentStmt = this.connection.prepareStatement(INSERT_PAYMENT);
            }
            resultSet = insertPaymentStmt.getGeneratedKeys();
            insertPaymentStmt.setInt(1, payment.getGroupOfPaymentId());
            insertPaymentStmt.setInt(2, payment.getCost());
            insertPaymentStmt.setString(3, payment.getDescription());
            insertPaymentStmt.setString(4, payment.getPersonId());
            insertPaymentStmt.executeUpdate();
            if (!resultSet.next()) {
                throw new DaoException("Payment was not added");
            }
        } catch (SQLException e) {
            throw new DaoException("Payment was not added ", e);
        } finally {
            CloseResultSet.closeResultSet(resultSet, "payment.");
        }
    }

    @Override
    public void updatePayment(Payment payment) throws DaoException {

        try {
            if (this.updatePaymentStmt == null) {
                this.updatePaymentStmt = this.connection.prepareStatement(UPDATE_PAYMENT);
            }
            updatePaymentStmt.setInt(1, payment.getGroupOfPaymentId());
            updatePaymentStmt.setInt(2, payment.getCost());
            updatePaymentStmt.setString(3, payment.getDescription());
            updatePaymentStmt.setInt(4, payment.getPaymentId());
            updatePaymentStmt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Update payment failure ", e);
        }
    }

    @Override
    public void deletePayment(Integer id) throws DaoException {
        try {
            if (this.deletePaymentStmt == null) {
                this.deletePaymentStmt = this.connection.prepareStatement(DELETE_PAYMENT);
            }
            deletePaymentStmt.setInt(1, id);
            deletePaymentStmt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Delete payment failure ", e);
        }

    }

    @Override
    public void close() throws DaoException {

        Exception ex = null;
        for (PreparedStatement closeable : preparedStatementList) {
            try {
                if (!closeable.isClosed()) {
                    closeable.close();
                }
            } catch (Exception x) {
                if (ex == null) {
                    ex = x;
                } else {
                    ex.addSuppressed(x);
                }
            }
        }

        try {
            if ((this.connection != null) || (!this.connection.isClosed())) {
                this.connection.close();
            }
        } catch (SQLException e) {
            throw new DaoException("Connection closing failure ", e);
        }

        if (ex != null) {
            throw new DaoException("One of the prepared statement hasn't been closed", ex);
        }
    }
}
///////////////////////////////////////////////////////////////