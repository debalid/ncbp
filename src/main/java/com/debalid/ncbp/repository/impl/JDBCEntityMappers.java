package com.debalid.ncbp.repository.impl;

import com.debalid.ncbp.entity.Client;
import com.debalid.ncbp.entity.Order;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Contains static method for mapping entities from result sets.
 * Created by debalid on 01.05.2016.
 */
public class JDBCEntityMappers {
    // Note - this method does not establish connection between Order and Client!
    static Order mapToOrder(ResultSet rs, String tablePrefix) throws SQLException {
        Long number = rs.getLong(tablePrefix + "order_number");
        if (rs.wasNull()) return null;

        Order order = new Order();
        order.setNumber(number);
        order.setPriceTotal(rs.getInt(tablePrefix + "price_total"));
        order.setDate(rs.getDate(tablePrefix + "date"));
        return order;
    }

    static Client mapToClient(ResultSet rs, String tablePrefix) throws SQLException {
        Integer clientId = rs.getInt(tablePrefix + "client_id");
        if (rs.wasNull()) return null;

        Client client = new Client();
        client.setId(clientId);
        client.setTitle(rs.getString(tablePrefix + "title"));
        client.setPhone(rs.getString(tablePrefix + "phone"));
        return client;
    }

}
