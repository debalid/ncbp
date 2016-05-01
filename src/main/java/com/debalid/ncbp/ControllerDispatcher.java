package com.debalid.ncbp;

import com.debalid.ncbp.controllers.OrdersController;
import com.debalid.ncbp.entity.Order;
import com.debalid.ncbp.repository.OrderSqlRepository;
import com.debalid.ncbp.repository.impl.JDBCOrderSqlRepository;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Dispatcher to determine what methods of what controllers should be executed based on request params.
 * Created by debalid on 20.04.2016.
 */
public class ControllerDispatcher extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String uri = req.getServletPath();

        // pattern: /controller/action/?query
        if (!uri.matches("/[A-z]*(/|/[A-z]*/?)?(\\?.*)?")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String[] parts = uri.split("/");
        String controller = parts.length > 0 ? parts[1] : "orders"; //`orders` is default controller
        String action = parts.length > 2 ? parts[2] : null;
        Map<String, String[]> params = req.getParameterMap();

        dispatchGet(req, resp, controller, action, params);
    }

    // Definitely should be some reflection magic here! :)
    private void dispatchGet(HttpServletRequest req, HttpServletResponse resp,
                          String controller, String action, Map<String, String[]> params)
            throws ServletException, IOException {

        ModelViewResult result = null;

        switch (controller) {
            case OrdersController.URI:
            default:
                result = dispatchGetOrders(action, params);
        }

        if (result == null) result = ErrorResult.of("Cannot dispatch",
                "Cannot find controller `" + controller + "` and action `" + action + "`.");

        req.setAttribute(result.getModelName(), result.getModel());
        req.getRequestDispatcher(result.getViewName()).forward(req, resp);
    }

    // Should go to reflection-based implementation
    private ModelViewResult dispatchGetOrders(String action, Map<String, String[]> params){
        OrdersController ordersController = new OrdersController();
        switch(action != null? action : "") {
            case OrdersController.GET_ALL_BY_FILTER:
                return ordersController.getAllByFilter(params);
            default:
                return ordersController.index(params);
        }
    }
}
