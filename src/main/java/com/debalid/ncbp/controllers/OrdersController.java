package com.debalid.ncbp.controllers;

import com.debalid.ncbp.ErrorResult;
import com.debalid.ncbp.ModelViewResult;
import com.debalid.ncbp.entity.Order;
import com.debalid.ncbp.repository.OrderSqlRepository;
import com.debalid.ncbp.repository.impl.JDBCOrderSqlRepository;

import javax.naming.NamingException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Request-scoped representation of orders controller in MVC
 * Created by debalid on 01.05.2016.
 */
public class OrdersController {
    public static final String URI = "orders";
    public static final String GET_ALL_BY_FILTER = "getAllByFilter";

    private OrderSqlRepository repo;

    public OrdersController() {
        try {
            repo = new JDBCOrderSqlRepository();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    // GET: / or /orders/
    public ModelViewResult index(Map<String, String[]> params) {
        return getAllByFilter(params);
    }

    // GET: /orders/getAllByFilter
    public ModelViewResult getAllByFilter(Map<String, String[]> params) {
        if (repo == null) {
            return ErrorResult.of("Cannot establish JNDI data source :(", null);
        }
        try {
            String[] numberParams = params.get("number");
            String[] clientTitleParams = params.get("clientTitle");

            String numberChunk = null;
            if (numberParams != null && numberParams.length > 0) numberChunk = numberParams[0];

            String clientTitleChunk = null;
            if (clientTitleParams != null && clientTitleParams.length > 0) clientTitleChunk = clientTitleParams[0];

            List<Order> orders = repo.findByNumberAndClient(numberChunk, clientTitleChunk); // nulls are acceptable
            return ModelViewResult.of("orders", orders, "results.jsp");

        } catch (SQLException e) {
            return ErrorResult.of("SQL error :(", e.getMessage());
        }
    }
}
