package com.debalid.ncbp;

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

/**
 * Dispatcher to determine what orders should be displayed based on request params.
 * Created by debalid on 20.04.2016.
 */
public class OrdersDispatcher extends HttpServlet {
    private OrderSqlRepository repo;

    public OrdersDispatcher() {
        try {
            repo = new JDBCOrderSqlRepository();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (repo == null) {
            req.setAttribute("reason", "Cannot establish JNDI data source :(");
            req.getRequestDispatcher("error.jsp").forward(req, resp);
            return;
        }

        String numberChunk = req.getParameter("number");
        String clientTitleChunk = req.getParameter("clientTitle");

        try {
            List<Order> orders = repo.findByNumberAndClient(numberChunk, clientTitleChunk); // nulls are acceptable
            req.setAttribute("orders", orders);
            req.getRequestDispatcher("results.jsp").forward(req, resp);

        } catch (SQLException e) {
            e.printStackTrace();

            req.setAttribute("reason", "SQL error :(");
            req.setAttribute("reasonExplicit", e.getMessage());
            req.getRequestDispatcher("error.jsp").forward(req, resp);
        }
    }
}
