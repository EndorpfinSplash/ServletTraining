package by.zinovich.javastudy.api.dao;

import by.zinovich.javastudy.exceptions.DaoException;

public interface DaoFactory  {
    PaymentsDAO getPaymentsDao() throws DaoException;
    GroupOfPaymentDAO getGroupOfPaymentDao() throws DaoException;
    PersonsDAO getPersonsDAO() throws DaoException;
}
////////////////////////////////////////////////