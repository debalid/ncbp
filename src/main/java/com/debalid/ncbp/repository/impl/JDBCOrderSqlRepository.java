package com.debalid.ncbp.repository.impl;

import com.debalid.ncbp.Configuration;
import com.debalid.ncbp.entity.Client;
import com.debalid.ncbp.entity.Order;
import com.debalid.ncbp.repository.OrderSqlRepository;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Pure jdbc implementation of orders repository.
 * Created by debalid on 20.04.2016.
 */
public class JDBCOrderSqlRepository implements OrderSqlRepository {
    private DataSource dataSource;

    public JDBCOrderSqlRepository() throws NamingException {
        InitialContext context = new InitialContext();
        DataSource dataSource = (DataSource) context.lookup(Configuration.get("datasource.jndi"));
        if (dataSource == null) throw new RuntimeException("Cannot find suitable data source :(");
        this.dataSource = dataSource;
    }

    // Note - this method does not establish connection between Order and Client
    private Order mapToOrder(ResultSet rs, String tablePrefix) throws SQLException {
        Order order = new Order();
        order.setNumber(rs.getLong(tablePrefix + "order_number"));
        order.setPriceTotal(rs.getInt(tablePrefix + "priceTotal"));
        order.setDate(rs.getDate(tablePrefix + "date"));
        return order;
    }

    private Client mapToClient(ResultSet rs, String tablePrefix) throws SQLException {
        Client client = new Client();
        client.setId(rs.getInt(tablePrefix + "client_id"));
        client.setTitle(rs.getString(tablePrefix + "title"));
        client.setPhone(rs.getString(tablePrefix + "phone"));
        return client;
    }

    private List<Order> mapQuery(ResultSet beginPoint, String ordersTablePrefix, String clientsTablePrefix) throws SQLException {
        List<Order> result = new ArrayList<Order>();
        while (beginPoint.next()) {
            Order order = this.mapToOrder(beginPoint, ordersTablePrefix);

            Client client = this.mapToClient(beginPoint, clientsTablePrefix);
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
                Connection connection = this.dataSource.getConnection();
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
            return this.mapQuery(statement.getResultSet(), "", "");
        }
    }

    public void save(Order some) {
        throw new UnsupportedOperationException();
    }

    public Order delete(Order some) {
        throw new UnsupportedOperationException();
    }

    public Optional<Order> find(Long id) {
        throw new UnsupportedOperationException();
    }
}
