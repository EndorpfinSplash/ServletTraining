package by.zinovich.javastudy.api.dao;

import by.zinovich.javastudy.api.domain.Payment;
import by.zinovich.javastudy.exceptions.DaoException;

import java.util.List;

public interface PaymentsDAO extends AutoCloseable {
    List<Payment> getAllPaymentList() throws DaoException;

    List<Payment> getPaymentListForPerson(String person_id) throws  DaoException;

    void addPayment(Payment payment) throws DaoException;

    void updatePayment(Payment payment) throws  DaoException;

    void deletePayment(Integer payment_id) throws DaoException;

    void close() throws DaoException;

}
//////////////////////////////////////////////