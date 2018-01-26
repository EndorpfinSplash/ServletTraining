package by.zinovich.javastudy.impl.dao;

import by.zinovich.javastudy.api.dao.GroupOfPaymentDAO;
import by.zinovich.javastudy.api.domain.GroupOfPayment;
import by.zinovich.javastudy.exceptions.DaoException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupOfPaymentDaoImpl implements GroupOfPaymentDAO {

    private static final String SELECT_ALL_GROUP_OF_PAYMENTS =
            "SELECT id, Group_name, person_id FROM Group_Of_Payments";
    private static final String SELECT_GROUP_OF_PAYMENTS_FOR_PERSON =
            "SELECT id, Group_name, person_id FROM Group_Of_Payments WHERE person_id = ?";
    private static final String INSERT_GROUP_OF_PAYMENT =
            "INSERT into Group_Of_Payments ( Group_name, person_id) VALUES (?,?)";
    private static final String UPDATE_GROUP_OF_PAYMENT =
            "UPDATE Group_Of_Payments SET Group_name=?, person_id=? where id=?";
    private static final String DELETE_GROUP_OF_PAYMENT =
            "DELETE FROM Group_Of_Payments WHERE id=?";

    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Connection connection;
    private PreparedStatement selectAllGroupsStmt;
    private PreparedStatement selectAllGroupsForPersonStmt;
    private PreparedStatement insertGroupOfPaymentStmt;
    private PreparedStatement deleteGroupStmt;
    private PreparedStatement updateGroupOfPaymentStmt;

    List<PreparedStatement> preparedStatementList = new ArrayList<>(Arrays.asList(
            selectAllGroupsStmt, selectAllGroupsForPersonStmt, insertGroupOfPaymentStmt, deleteGroupStmt, updateGroupOfPaymentStmt)
    );

    public GroupOfPaymentDaoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<GroupOfPayment> getAllGroups() throws DaoException {
        List<GroupOfPayment> allGroupOfPayments = new ArrayList<>();
        ResultSet resultSet = null;

        try {
            if (this.selectAllGroupsStmt == null) {
                this.selectAllGroupsStmt = connection.prepareStatement(SELECT_ALL_GROUP_OF_PAYMENTS);
            }
            resultSet = this.selectAllGroupsStmt.executeQuery();

            while (resultSet.next()) {
                GroupOfPayment groupOfPayment = new GroupOfPayment();
                groupOfPayment.setGroupId(resultSet.getInt("id"));
                groupOfPayment.setGroupOfPaymentName(resultSet.getString("Group_name"));
                groupOfPayment.setPersonId(resultSet.getInt("person_id"));

                allGroupOfPayments.add(groupOfPayment);
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to get group payments", e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    throw new DaoException("Failed to close result set for group payments", e);
                }
            }
        }
        return allGroupOfPayments;
    }

    @Override
    public List<GroupOfPayment> getAllGroupsForPerson(String person_id) throws DaoException {
        List<GroupOfPayment> allGroupOfPayments = new ArrayList<>();
        ResultSet resultSet = null;

        try {
            if (this.selectAllGroupsForPersonStmt == null) {
                this.selectAllGroupsForPersonStmt = connection.prepareStatement(SELECT_GROUP_OF_PAYMENTS_FOR_PERSON);
            }

            this.selectAllGroupsForPersonStmt.setString(1, person_id);
            resultSet = selectAllGroupsForPersonStmt.executeQuery();
            while (resultSet.next()) {
                GroupOfPayment groupOfPayment = new GroupOfPayment();
                groupOfPayment.setGroupId(resultSet.getInt("id"));
                groupOfPayment.setPersonId(resultSet.getInt("person_id"));
                groupOfPayment.setGroupOfPaymentName(resultSet.getString("Group_name"));

                allGroupOfPayments.add(groupOfPayment);
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to create statement to select all group payments for person", e);
        } finally {
            closeResultSet(resultSet);
        }
        return allGroupOfPayments;
    }

    @Override
    public void addGroupForPerson(GroupOfPayment groupOfPayment) throws DaoException {
        ResultSet resultSet = null;

        try {
            if (this.insertGroupOfPaymentStmt == null) {
                this.insertGroupOfPaymentStmt = this.connection.prepareStatement(INSERT_GROUP_OF_PAYMENT);
            }

            resultSet = this.insertGroupOfPaymentStmt.getGeneratedKeys();
            insertGroupOfPaymentStmt.setString(1, groupOfPayment.getGroupOfPaymentName());
            insertGroupOfPaymentStmt.setInt(2, groupOfPayment.getPersonId());
            insertGroupOfPaymentStmt.execute();

            if (!resultSet.next()) {
                throw new DaoException("Group of payment wasn't added.");
            }

        } catch (SQLException e) {
            throw new DaoException("Fail to add new group.", e);
        } finally {
            closeResultSet(resultSet);
        }
    }

    @Override
    public void updateGroup(GroupOfPayment groupOfPayment) throws DaoException {
        try {
            if (this.updateGroupOfPaymentStmt == null) {
                this.updateGroupOfPaymentStmt = this.connection.prepareStatement(UPDATE_GROUP_OF_PAYMENT);
            }
            updateGroupOfPaymentStmt.setString(1, groupOfPayment.getGroupOfPaymentName());
            updateGroupOfPaymentStmt.setInt(2, groupOfPayment.getPersonId());
            updateGroupOfPaymentStmt.setInt(3, groupOfPayment.getGroupId());
            updateGroupOfPaymentStmt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Failed to update group.", e);
        }
    }

    @Override
    public void delete(Integer id) throws DaoException {
        try {
            if (this.deleteGroupStmt == null) {
                this.deleteGroupStmt = this.connection.prepareStatement(DELETE_GROUP_OF_PAYMENT);
            }
            deleteGroupStmt.setInt(1, id);
            deleteGroupStmt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Deleting group of payment failed.", e);
        }
    }

    @Override
    public void close() throws DaoException {
        Exception ex = null;

        for (AutoCloseable closeable : preparedStatementList) {
            try {
                if (closeable != null) {
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
        } catch (SQLException se) {
            throw new DaoException("Connection closing failure.", se);
        }

        if (ex != null) {
            throw new DaoException("Failed at attempt to close prepared statements.", ex);
        }
    }

    private void closeResultSet(ResultSet resultSet) throws DaoException {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                throw new DaoException("Failed to close result set for group of payments", e);
            }
        }
    }
}
//////////////////////////////////////////////