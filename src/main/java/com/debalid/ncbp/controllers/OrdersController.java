package com.debalid.ncbp.controllers;

import com.debalid.ncbp.ErrorResult;
import com.debalid.ncbp.ModelViewResult;
import com.debalid.ncbp.entity.Client;
import com.debalid.ncbp.entity.Order;
import com.debalid.ncbp.repository.ClientSqlRepository;
import com.debalid.ncbp.repository.OrderSqlRepository;
import com.debalid.ncbp.repository.impl.JDBCClientSqlRepository;
import com.debalid.ncbp.repository.impl.JDBCOrderSqlRepository;
import com.debalid.ncbp.util.Tuple;

import javax.naming.NamingException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Request-scoped representation of orders controller in MVC
 * Created by debalid on 01.05.2016.
 */
public class OrdersController {
    public static final String CONTROLLER_URI = "orders"; // GET: `/orders`
    public static final String ACTION_GET_ALL_BY_FILTER = "getAllByFilter"; // GET: `/orders/getAllByFilter`
    public static final String ACTION_GET_EDIT = "edit"; // GET: `/order/edit`

    private OrderSqlRepository ordersRepo;
    private ClientSqlRepository clientsRepo;

    // TODO: switch to DI
    public OrdersController() throws NamingException {
        ordersRepo = new JDBCOrderSqlRepository();
        clientsRepo = new JDBCClientSqlRepository();
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
            List<Order> orders = ordersRepo.findByNumberAndClient(numberChunk, clientTitleChunk); // nulls are acceptable
            return ModelViewResult.of("orders", orders, "/results.jsp");

        } catch (SQLException e) {
            return ErrorResult.of("SQL error :(", e.getMessage());
        }
    }

    // GET: /orders/edit/?number=1234
    public ModelViewResult getEdit(Map<String, String[]> params) {
        String[] numberParams = params.get("number");

        Long number = null;
        if (numberParams != null && numberParams.length > 0) number = Long.parseLong(numberParams[0]);

        try {
            Optional<Order> order = ordersRepo.find(number);

            if (!order.isPresent())
                return ErrorResult.of("Cannot find order :(", "Cannot find order with number " + number, 404);

            List<Client> availableClients = clientsRepo.findAll();

            return ModelViewResult.of(
                    "orderAndAvailableClients",
                    new Tuple<>(order.get(), availableClients),
                    "/edit.jsp"
            );

        } catch (SQLException e) {
            return ErrorResult.of("SQL error :(", e.getMessage());
        }
    }
}
