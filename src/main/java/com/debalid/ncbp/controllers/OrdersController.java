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
import java.util.Optional;

/**
 * Request-scoped representation of orders controller in MVC
 * Created by debalid on 01.05.2016.
 */
public class OrdersController {
    public static final String CONTROLLER_URI = "orders";
    public static final String ACTION_GET_ALL_BY_FILTER = "getAllByFilter";
    public static final String ACTION_EDIT = "edit";

    private OrderSqlRepository repo;

    public OrdersController() throws NamingException {
        repo = new JDBCOrderSqlRepository();
    }

    // GET: / or /orders/
    public ModelViewResult index(Map<String, String[]> params) {
        return getAllByFilter(params);
    }

    // GET: /orders/getAllByFilter?number=1234&clientTitle=tma
    public ModelViewResult getAllByFilter(Map<String, String[]> params) {
        String[] numberParams = params.get("number");
        String[] clientTitleParams = params.get("clientTitle");

        String numberChunk = null;
        if (numberParams != null && numberParams.length > 0) numberChunk = numberParams[0];

        String clientTitleChunk = null;
        if (clientTitleParams != null && clientTitleParams.length > 0) clientTitleChunk = clientTitleParams[0];

        try {
            List<Order> orders = repo.findByNumberAndClient(numberChunk, clientTitleChunk); // nulls are acceptable
            return ModelViewResult.of("orders", orders, "results.jsp");

        } catch (SQLException e) {
            return ErrorResult.of("SQL error :(", e.getMessage());
        }
    }

    // GET: /orders/edit/?number=1234
    public ModelViewResult edit(Map<String, String[]> params) {
        String[] numberParams = params.get("number");

        Long number = null;
        if (numberParams != null && numberParams.length > 0) number = Long.parseLong(numberParams[0]);

        try {
            Optional<Order> order = repo.find(number);

            if (!order.isPresent())
                return ErrorResult.of("Cannot find order :(", "Cannot find order with number " + number, 404);

            return ModelViewResult.of("order", order.get(), "edit.jsp");

        } catch (SQLException e) {
            return ErrorResult.of("SQL error :(", e.getMessage());
        }
    }
}
