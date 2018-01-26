package by.zinovich.javastudy.api.dao;

import by.zinovich.javastudy.api.domain.GroupOfPayment;
import by.zinovich.javastudy.exceptions.DaoException;

import java.util.List;

public interface GroupOfPaymentDAO extends AutoCloseable {
    List<GroupOfPayment> getAllGroups() throws DaoException;

    List<GroupOfPayment> getAllGroupsForPerson(String person_id) throws DaoException;

    void addGroupForPerson(GroupOfPayment groupOfPayment) throws DaoException;

    void updateGroup(GroupOfPayment groupOfPayment) throws DaoException;

    void delete(Integer id) throws DaoException;

    void close() throws DaoException;
}
//////////////////////////////////////////////