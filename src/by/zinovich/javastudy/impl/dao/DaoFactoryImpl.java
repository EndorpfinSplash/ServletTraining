package by.zinovich.javastudy.impl.dao;

import by.zinovich.javastudy.api.dao.GroupOfPaymentDAO;
import by.zinovich.javastudy.api.dao.PaymentsDAO;
import by.zinovich.javastudy.api.dao.PersonsDAO;
import by.zinovich.javastudy.exceptions.DaoException;
import by.zinovich.javastudy.api.dao.DaoFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DaoFactoryImpl implements DaoFactory {

    @Override
    public PaymentsDAO getPaymentsDao() throws DaoException {
        return new PaymentsDaoImpl(getConnection());
    }

    @Override
    public GroupOfPaymentDAO getGroupOfPaymentDao() throws DaoException {
        return new GroupOfPaymentDaoImpl(getConnection());
    }

    @Override
    public PersonsDAO getPersonsDAO() throws DaoException {
        return new PersonsDaoImpl(getConnection());
    }

    public Connection getConnection() throws DaoException{
        Properties connectionProps = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("Db_connection.properties");
        Connection connection;
        try {
            connectionProps.load(input);
            Class.forName(connectionProps.getProperty("class.name"));
            connection = DriverManager.getConnection(connectionProps.getProperty("url"),
                    connectionProps.getProperty("user"),
                    connectionProps.getProperty("password")
            );
        } catch (IOException e) {
            throw new DaoException("Reading properties failed " + e);
        } catch (ClassNotFoundException | SQLException e) {
            throw new DaoException("Error has occurred at the moment connection creation " + e);
        }
        return connection;
    }
}
//////////////////////////////////////////////