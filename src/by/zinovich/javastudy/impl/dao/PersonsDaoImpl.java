package by.zinovich.javastudy.impl.dao;

import by.zinovich.javastudy.api.dao.PersonsDAO;
import by.zinovich.javastudy.api.domain.Person;
import by.zinovich.javastudy.exceptions.DaoException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PersonsDaoImpl implements PersonsDAO {

    private static final String SELECT_ALL_PERSONS =
            "SELECT Person_id, Person_name_first, Person_name_second, Login, password FROM Persons";

    private static final String SELECT_PERSON_BY_ID =
            "SELECT Person_id, Person_name_first, Person_name_second, Login, password FROM Persons where person_id =?";
    private static final String INSERT_PERSON =
            "INSERT into Persons ( Person_name_first, Person_name_second, Login) VALUES (?,?,?)";
    private static final String UPDATE_PERSON =
            "UPDATE Persons SET Person_name_first=?, Person_name_second=?, Login=?, password = ? where person_id=?";
    private static final String DELETE_PERSON =
            "DELETE FROM Persons WHERE Person_id=?";
    private static final String COUNT_PERSONS_RECORDS =
            "SELECT COUNT(*) FROM Persons WHERE Person_id=?";

    private Connection connection;
    private PreparedStatement selectAllPersonsStmt;
    private PreparedStatement selectAllGroupsForPersonStmt;
    private PreparedStatement insertPersonStmt;
    private PreparedStatement deletePersonStmt;
    private PreparedStatement updatePersonStmt;
    private PreparedStatement countPersonasRecStmt;
    private PreparedStatement selectPersonByIdStmt;

    List<PreparedStatement> preparedStatementList = new ArrayList<>(Arrays.asList(
            selectAllPersonsStmt, selectAllGroupsForPersonStmt, insertPersonStmt, deletePersonStmt, updatePersonStmt, countPersonasRecStmt, selectPersonByIdStmt)
    );

    public PersonsDaoImpl(Connection connection) throws DaoException {
        this.connection = connection;
    }

    @Override
    public List<Person> getAllPersons() throws DaoException {
        List<Person> allPersons = new ArrayList<>();
        ResultSet resultSet = null;

        try {
            if (this.selectAllPersonsStmt == null) {
                this.selectAllPersonsStmt = this.connection.prepareStatement(SELECT_ALL_PERSONS);
            }
            resultSet = this.selectAllPersonsStmt.executeQuery();
            while (resultSet.next()) {
                Person person = new Person();
                person.setPersonId(resultSet.getInt("Person_id"));
                person.setPersonNameFirst(resultSet.getString("Person_name_first"));
                person.setPersonNameSecond(resultSet.getString("Person_name_second"));
                person.setPersonLogin(resultSet.getString("Login"));
                person.setPersonPassword(resultSet.getString("password"));

                allPersons.add(person);
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to create statement to select all group payments", e);
        } finally {
            CloseResultSet.closeResultSet(resultSet, "persons.");
        }
        return allPersons;
    }

    @Override
    public void addPerson(Person person) throws DaoException {
        ResultSet resultSet = null;

        try {
            if (this.insertPersonStmt == null) {
                this.insertPersonStmt = this.connection.prepareStatement(INSERT_PERSON);
            }
            resultSet = this.insertPersonStmt.getGeneratedKeys();
            this.insertPersonStmt.setString(1, person.getPersonNameFirst());
            this.insertPersonStmt.setString(2, person.getPersonNameSecond());
            this.insertPersonStmt.setString(3, person.getPersonLogin());
            this.insertPersonStmt.execute();

            if (!resultSet.next()) {
                throw new DaoException("New person wasn't added.");
            }
        } catch (SQLException e) {
            throw new DaoException("Fail to add new person.", e);
        } finally {
            CloseResultSet.closeResultSet(resultSet, "persons.");
        }
    }

    @Override
    public void updatePerson(Person person) throws DaoException {
        try {
            if (this.updatePersonStmt == null) {
                this.updatePersonStmt = this.connection.prepareStatement(UPDATE_PERSON);
            }
            this.updatePersonStmt.setString(1, person.getPersonNameFirst());
            this.updatePersonStmt.setString(2, person.getPersonNameSecond());
            this.updatePersonStmt.setString(3, person.getPersonLogin());
            this.updatePersonStmt.setString(4, person.getPersonPassword());
            this.updatePersonStmt.setInt(5, person.getPersonId());
            this.updatePersonStmt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Failed to update person data.", e);
        }
    }

    @Override
    public void deletePerson(Integer id) throws DaoException {
        try {
            if (this.deletePersonStmt == null) {
                this.deletePersonStmt = this.connection.prepareStatement(DELETE_PERSON);
            }
            this.deletePersonStmt.setInt(1, id);
            this.deletePersonStmt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Deleting person failed.", e);
        }
    }

    @Override
    public int countPersonsRecords(Integer Person_id) throws DaoException {
        ResultSet rs = null;
        try {
            if (this.countPersonasRecStmt == null) {
                this.countPersonasRecStmt = this.connection.prepareStatement(COUNT_PERSONS_RECORDS);
            }
            this.countPersonasRecStmt.setInt(1,Person_id);
            rs = this.countPersonasRecStmt.executeQuery();
            return rs.getInt(1);

        } catch (SQLException e) {
            throw new DaoException("Failed to count person", e);
        } finally {
            CloseResultSet.closeResultSet(rs, "persons.");
        }
    }

    @Override
    public Person getPersonByPersonId(Integer person_id) throws DaoException {

        ResultSet resultSet = null;
        Person person = new Person();
        try {
            if (this.selectPersonByIdStmt == null) {
                this.selectPersonByIdStmt = this.connection.prepareStatement(SELECT_PERSON_BY_ID);
        }
            this.selectPersonByIdStmt.setInt(1, person_id);
            resultSet = this.selectPersonByIdStmt.executeQuery();

                person.setPersonId(resultSet.getInt("Person_id"));
                person.setPersonNameFirst(resultSet.getString("Person_name_first"));
                person.setPersonNameSecond(resultSet.getString("Person_name_second"));
                person.setPersonLogin(resultSet.getString("Login"));
                person.setPersonPassword(resultSet.getString("password"));

        } catch (SQLException e) {
            throw new DaoException("Failed to create statement to select all group payments", e);
        } finally {
            CloseResultSet.closeResultSet(resultSet, "persons.");
        }
        return person;
    }

    @Override
    public void close() throws DaoException {
        Exception ex = null;

        for (PreparedStatement closeable : preparedStatementList) {
            try {
                if (closeable != null && !closeable.isClosed()) {
                    closeable.close();
                }
            } catch (Exception e) {
                if (ex == null) {
                    ex = e;
                }
                ex.addSuppressed(e);
            }
        }

        try {
            if ((this.connection != null) || (!this.connection.isClosed())) {
                this.connection.close();
            }
        } catch (SQLException e) {
            throw new DaoException("Connection closing failure.", e);
        }

        if (ex != null) {
            throw new DaoException("Failed at attempt to close prepared statements.", ex);
        }
    }
}
//////////////////////////////////////////////