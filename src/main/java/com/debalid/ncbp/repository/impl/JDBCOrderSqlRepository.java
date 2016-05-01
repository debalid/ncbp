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
                        "SELECT orders.*, clients.* " +
                                "FROM orders " +
                                "LEFT JOIN clients ON orders.client_id = clients.client_id " +
                                "WHERE CHAR(order_number) LIKE ? AND LOWER(title) LIKE ?"
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
                                "FROM orders " +
                                "WHERE order_number = ?"
                );
                PreparedStatement insertStatement = connection.prepareStatement(
                        "INSERT INTO orders(date, priceTotal, client_id) " +
                                "VALUES (?, ?, ?)"
                );
                PreparedStatement updateStatement = connection.prepareStatement(
                        "UPDATE orders " +
                                "SET date = ? " +
                                ", priceTotal = ? " +
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
                //insertStatement.setLong(1, some.getNumber()); TODO: handle
                insertStatement.setDate(1, new java.sql.Date(some.getDate().getTime()));
                insertStatement.setInt(2, some.getPriceTotal());
                insertStatement.setInt(3, some.getClient().getId());
                insertStatement.execute();
            } else {
                updateStatement.setDate(1, new java.sql.Date(some.getDate().getTime()));
                updateStatement.setInt(2, some.getPriceTotal());
                updateStatement.setInt(3, some.getClient().getId());
                updateStatement.setLong(4, some.getNumber());
                updateStatement.execute();
            }
        }
    }

    public Order delete(Order some) {
        throw new UnsupportedOperationException();
    }

    public Optional<Order> find(Long id) throws SQLException {
        if (id == null) return Optional.empty();
        try (
                Connection connection = this.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT orders.*, clients.* " +
                                "FROM orders " +
                                "LEFT JOIN clients ON orders.client_id = clients.client_id " +
                                "WHERE orders.order_number = ?"
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
