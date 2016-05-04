package com.debalid.ncbp.repository.impl;

import com.debalid.ncbp.entity.Client;
import com.debalid.ncbp.entity.Order;
import com.debalid.ncbp.repository.OrderSqlRepository;

import javax.naming.NamingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Pure jdbc implementation of orders repository.
 * Created by debalid on 20.04.2016.
 */
public class JDBCOrderSqlRepository extends AbstractJDBCConsumer
        implements OrderSqlRepository {

    public JDBCOrderSqlRepository() throws NamingException {
        super();
    }

    private List<Order> mapJoinToEntities(ResultSet beginPoint, String ordersTablePrefix, String clientsTablePrefix)
            throws SQLException {

        List<Order> result = new LinkedList<Order>();
        while (beginPoint.next()) {
            Order order = JDBCEntityMappers.mapToOrder(beginPoint, ordersTablePrefix);

            Client client = JDBCEntityMappers.mapToClient(beginPoint, clientsTablePrefix);
            order.setClient(client);

            result.add(order);
        }
        return result;
    }

    public List<Order> findAll() throws SQLException {
        return findByNumberAndClient(null, null);
    }

    public List<Order> findByNumber(String numberChunk) throws SQLException {
        return findByNumberAndClient(numberChunk, null);
    }

    public List<Order> findByClientTitle(String clientTitleChunk) throws SQLException {
        return findByNumberAndClient(null, clientTitleChunk);
    }

    public List<Order> findByNumberAndClient(String numberChunk, String clientTitleChunk) throws SQLException {
        try (
                Connection connection = this.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT ncbp.orders.*, ncbp.clients.* " +
                                "FROM ncbp.orders " +
                                "LEFT JOIN ncbp.clients ON ncbp.orders.client_id = ncbp.clients.client_id " +
                                "WHERE CHAR(order_number) LIKE ? AND (LOWER(title) LIKE ? OR title IS NULL)" +
                                "ORDER BY (order_number) ASC"
                )
        ) {
            statement.setString(1, numberChunk != null ? "%" + numberChunk + "%" : "%");
            statement.setString(2, clientTitleChunk != null ? "%" + clientTitleChunk.toLowerCase() + "%" : "%");
            statement.execute();
            return this.mapJoinToEntities(statement.getResultSet(), "", "");
        }
    }

    public void save(Order some) throws SQLException {
        try (
                Connection connection = this.getDataSource().getConnection();
                PreparedStatement selectStatement = connection.prepareStatement(
                        "SELECT count(*)" +
                                "FROM ncbp.orders " +
                                "WHERE order_number = ?"
                );
                PreparedStatement insertStatement = connection.prepareStatement(
                        "INSERT INTO ncbp.orders(order_number, date, price_total, client_id) " +
                                "VALUES (?, ?, ?, ?)"
                );
                PreparedStatement updateStatement = connection.prepareStatement(
                        "UPDATE ncbp.orders " +
                                "SET date = ? " +
                                ", price_total = ? " +
                                ", client_id = ? " +
                                "WHERE order_number = ?"
                );
        ) {
            selectStatement.setLong(1, some.getNumber());
            selectStatement.execute();

            ResultSet rs = selectStatement.getResultSet();
            rs.next();
            int founded = rs.getInt(1);
            if (founded == 0) {
                insertStatement.setLong(1, some.getNumber());
                insertStatement.setDate(2, new java.sql.Date(some.getDate().getTime()));
                insertStatement.setInt(3, some.getPriceTotal());
                if (some.getClient() != null) {
                    insertStatement.setInt(4, some.getClient().getId());
                } else {
                    insertStatement.setNull(4, Types.INTEGER);
                }
                insertStatement.execute();
            } else {
                updateStatement.setDate(1, new java.sql.Date(some.getDate().getTime()));
                updateStatement.setInt(2, some.getPriceTotal());
                if (some.getClient() != null) {
                    updateStatement.setInt(3, some.getClient().getId());
                } else {
                    updateStatement.setNull(3, Types.INTEGER);
                }
                updateStatement.setLong(4, some.getNumber());
                updateStatement.execute();
            }
        }
    }

    @Override
    public Optional<Order> delete(Long id) throws SQLException {
        try (
                Connection connection = this.getDataSource().getConnection();
                PreparedStatement selectStatement = connection.prepareStatement(
                        "SELECT ncbp.orders.*" +
                                "FROM ncbp.orders " +
                                "WHERE order_number = ?"
                );
                PreparedStatement deleteStatement = connection.prepareStatement("" +
                        "DELETE FROM ncbp.ORDERS WHERE order_number=?"
                )
        ) {
            selectStatement.setLong(1, id);
            selectStatement.execute();

            ResultSet rs = selectStatement.getResultSet();
            if(!rs.next()) return Optional.empty();
            Order order = JDBCEntityMappers.mapToOrder(rs, "");

            deleteStatement.setLong(1, order.getNumber());
            deleteStatement.execute();
            return Optional.of(order);
        }
    }

    public Optional<Order> find(Long id) throws SQLException {
        if (id == null) return Optional.empty();
        try (
                Connection connection = this.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT ncbp.orders.*, ncbp.clients.* " +
                                "FROM ncbp.orders " +
                                "LEFT JOIN ncbp.clients ON ncbp.orders.client_id = ncbp.clients.client_id " +
                                "WHERE ncbp.orders.order_number = ?"
                )
        ) {
            statement.setLong(1, id);
            statement.execute();

            List<Order> found = this.mapJoinToEntities(statement.getResultSet(), "", "");
            return found.isEmpty()
                    ? Optional.<Order>empty()
                    : Optional.of(found.get(0));
        }
    }
}
