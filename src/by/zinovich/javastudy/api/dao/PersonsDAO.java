package by.zinovich.javastudy.api.dao;

import by.zinovich.javastudy.api.domain.Person;
import by.zinovich.javastudy.exceptions.DaoException;

import java.util.List;

public interface PersonsDAO extends AutoCloseable {
    List<Person> getAllPersons() throws DaoException;

    void addPerson(Person person) throws DaoException;

    void updatePerson(Person person) throws  DaoException;

    void deletePerson(Integer Person_id) throws DaoException;

    void close() throws DaoException;

    int countPersonsRecords (Integer Person_id) throws DaoException;
}
//////////////////////////////////////////////