package com.debalid.ncbp.controllers;

import com.debalid.mvc.Controller;
import com.debalid.mvc.annotation.AllowHttpVerbs;
import com.debalid.mvc.result.ActionResult;
import com.debalid.mvc.result.ErrorResult;
import com.debalid.mvc.result.ModelViewResult;
import com.debalid.mvc.result.RedirectResult;
import com.debalid.ncbp.entity.Client;
import com.debalid.ncbp.entity.Order;
import com.debalid.ncbp.repository.ClientSqlRepository;
import com.debalid.ncbp.repository.OrderSqlRepository;
import com.debalid.ncbp.repository.impl.JDBCClientSqlRepository;
import com.debalid.ncbp.repository.impl.JDBCOrderSqlRepository;
import com.debalid.mvc.util.HttpVerb;
import com.debalid.ncbp.util.Tuple;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Request-scoped representation of orders controller in MVC
 * Created by debalid on 01.05.2016.
 */
public class OrdersController extends Controller {
    // Repositories of entities. Should be injected.
    private OrderSqlRepository ordersRepo;
    private ClientSqlRepository clientsRepo;

    public OrdersController() throws NamingException {
        // TODO: switch to DI
        ordersRepo = new JDBCOrderSqlRepository();
        clientsRepo = new JDBCClientSqlRepository();
    }

    /**
     * GET: / or /orders/
     * @param params map of params that can contain any keys.
     * @return Page of all available orders or error.
     */
    @AllowHttpVerbs(values = {HttpVerb.GET})
    public ActionResult index(Map<String, String[]> params) {
        return allByFilter(params);
    }

    /**
     * GET: /orders/getAllByFilter?number=[number]&clientTitle=[word]
     * List of available orders by its number content (chunk :)) and its client's title content.
     * Note: if number or clientTitle are null then it shows all avaialble orders.
     * @param params map of params that should contain keys `number` and `clientTitle`.
     * @return Page of filtered orders or error.
     */
    @AllowHttpVerbs(values = {HttpVerb.GET})
    public ActionResult allByFilter(Map<String, String[]> params) {
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

    /**
     * GET: /orders/edit/?number=[number]
     * Gets the edit page of chosen order.
     * @param params map of params that should contain key `number`.
     * @return Page of editing an order or error.
     */
    @AllowHttpVerbs(values = {HttpVerb.GET})
    public ActionResult edit(Map<String, String[]> params) {
        Long number = extractNumber(params);
        if (number == null) return ErrorResult.of("Wrong number argument", null, HttpServletResponse.SC_BAD_REQUEST);

        try {
            Optional<Order> order = ordersRepo.find(number);

            if (!order.isPresent())
                return ErrorResult.of("Cannot find order :(", "Cannot find order with number " + number,
                        HttpServletResponse.SC_NOT_FOUND);

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

    /**
     * GET: /orders/create/
     * Gets the create page of chosen order.
     * @param params map of params that can contain any key.
     * @return Page of creating an order or error.
     */
    @AllowHttpVerbs(values = {HttpVerb.GET})
    public ActionResult create(Map<String, String[]> params) {
        try {
            List<Client> availableClients = clientsRepo.findAll();

            return ModelViewResult.of(
                    "orderAndAvailableClients",
                    new Tuple<>(null, availableClients),
                    "/edit.jsp"
            );

        } catch (SQLException e) {
            return ErrorResult.of("SQL error :(", e.getMessage());
        }
    }

    /**
     * GET: POST: /orders/save/
     * BODY: ?number=[number]&clientId=[number|null]&date=[yyyy-MM-dd]&total=[number]
     * Saves order in backend database. It means updating if provided order already exists or creating a new one.
     * Note: params of this method will be mapped to OrderParam object.
     * @see com.debalid.ncbp.controllers.OrdersController.OrderParams
     * @param params map of params that should contain keys `number`, `clientId`, `date`, `total`.
     * @return Redirect to index (`/`) or error.
     */
    @AllowHttpVerbs(values = {HttpVerb.POST})
    public ActionResult save(Map<String, String[]> params) {
        OrderParams op = extractOrderParams(params);

        if (op.number == null) return ErrorResult.of("Cannot extract order number :(",
                "Cannot extract order number from request params.", HttpServletResponse.SC_BAD_REQUEST);
        if (op.number < 100) return ErrorResult.of("Cannot process order number :(",
                "There is a requirement that order number `" + op.number + "` should have at least 3 digits.",
                HttpServletResponse.SC_BAD_REQUEST);
        if (op.date == null) return ErrorResult.of("Cannot extract date :(",
                "Cannot extract date from request params.", HttpServletResponse.SC_BAD_REQUEST);
        if (op.priceTotal == null) return ErrorResult.of("Cannot extract price :(",
                "Cannot extract price from request params.", HttpServletResponse.SC_BAD_REQUEST);

        try {
            Order order = new Order();
            order.setNumber(op.number);
            order.setPriceTotal(op.priceTotal);
            order.setDate(op.date);

            if (op.clientId != null) {
                Optional<Client> client = clientsRepo.find(op.clientId);
                if (!client.isPresent())
                    return ErrorResult.of("Cannot find client :(", "Cannot find client with id " + op.clientId + ".");
                order.setClient(client.get());
            } else order.setClient(null);

            ordersRepo.save(order);

            return RedirectResult.of(this.buildRootURL());

        } catch (SQLException e) {
            return ErrorResult.of("SQL error :(", e.getMessage());
        }
    }

    /**
     * GET: /orders/remove/?number=[number]
     * Removes order with chosen number.
     * @param params map of params that should contain key `number`.
     * @return Redirect to index (`/`) or error.
     */
    @AllowHttpVerbs(values = {HttpVerb.GET})
    public ActionResult remove(Map<String, String[]> params) {
        Long number = extractNumber(params);
        if (number == null) return ErrorResult.of("Wrong number argument", null, HttpServletResponse.SC_BAD_REQUEST);

        try {
            ordersRepo.delete(number);
            return RedirectResult.of(this.buildRootURL());
        } catch (SQLException e) {
            return ErrorResult.of("SQL error :(", e.getMessage());
        }

    }

    // Determine its root url.
    private String buildRootURL() {
        HttpServletRequest req = this.getRequest();
        return req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() +
                req.getServletContext().getContextPath();
    }

    // Extracts `number` from params and parses it to nullable Long.
    private Long extractNumber(Map<String, String[]> params) {
        String[] numberParams = params.get("number");

        Long number = null;
        if (numberParams != null && numberParams.length > 0) number = Long.parseLong(numberParams[0]);

        return number;
    }

    // Builds OrderParams instance based on provided params.
    private OrderParams extractOrderParams(Map<String, String[]> params) {
        String[] numberParams = params.get("number");
        String[] clientIdParams = params.get("clientId");
        String[] dateParams = params.get("date");
        String[] priceTotalParams = params.get("total");

        Long number = null;
        Integer clientId = null;
        try {
            if (numberParams != null && numberParams.length > 0) number = Long.parseLong(numberParams[0]);
            if (clientIdParams != null && clientIdParams.length > 0) clientId = Integer.parseInt(clientIdParams[0]);
        } catch (NumberFormatException nfe) {
            //nothing - values are already nulls.
        }

        Date date = null;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        if (dateParams != null && dateParams.length > 0) try {
            date = df.parse(dateParams[0]);
        } catch (ParseException e) {
            //nothing - value is already null
        }

        Integer priceTotal = null;
        if (priceTotalParams != null && priceTotalParams.length > 0) priceTotal = Integer.parseInt(priceTotalParams[0]);

        return new OrderParams(number, clientId, date, priceTotal);
    }

    // Represents params of order that should be saved.
    private class OrderParams {
        public final Long number;
        public final Integer clientId;
        public final Date date;
        public final Integer priceTotal;

        private OrderParams(Long number, Integer clientId, Date date, Integer priceTotal) {
            this.number = number;
            this.clientId = clientId;
            this.date = date;
            this.priceTotal = priceTotal;
        }
    }
}
