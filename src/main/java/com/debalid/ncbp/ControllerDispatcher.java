package com.debalid.ncbp;

import com.debalid.ncbp.controllers.OrdersController;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Dispatcher servlet to determine what methods of what controllers should be executed based on request params.
 * Created by debalid on 20.04.2016.
 */
public class ControllerDispatcher extends HttpServlet {
    // uri pattern: /controller/action/?query
    private static final String URI_REGEXP = "/[A-z]*(/|/[A-z]*/?)?(\\?.*)?";
    // default (root `/`) controller is `orders`
    private static final String DEFAULT_CONTROLLER = OrdersController.CONTROLLER_URI;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Try to resolve specified GET request.
        ResolvedRequest resolved = resolveRequest(ResolvedRequest.HTTP_VERB.GET, req);
        if (resolved == null) { //not resolved
            finishRequest(req, resp,
                    ErrorResult.of("Cannot resolve GET request",
                    "Cannot resolve specified uri `" + req.getServletPath() + "`.",
                    404));
            return;
        }

        // Try to dispatch specified request.
        ModelViewResult result = dispatchGet(req, resp, resolved);
        if (result == null) {
            finishRequest(req, resp,
                    ErrorResult.of("Cannot dispatch GET request",
                    "Cannot find controller `" + resolved.controller + "` and action `" + resolved.action + "`.",
                    500));
            return;
        }

        // Process resolved and dispatched request.
        finishRequest(req, resp, result);
    }

    private ResolvedRequest resolveRequest(ResolvedRequest.HTTP_VERB verb, HttpServletRequest req) {
        String uri = req.getServletPath();

        // pattern: /controller/action/?query
        if (!uri.matches(URI_REGEXP)) {
            return null;
        }

        String[] parts = uri.split("/");
        String controller = parts.length > 0 ? parts[1] : DEFAULT_CONTROLLER; //`orders` is default controller
        String action = parts.length > 2 ? parts[2] : null;
        Map<String, String[]> params = req.getParameterMap();

        return new ResolvedRequest(verb, controller, action, params);
    }

    private void finishRequest(HttpServletRequest req, HttpServletResponse resp, ModelViewResult mv)
            throws IOException, ServletException {
        req.setAttribute(mv.getModelName(), mv.getModel());
        // Process error from any layer (resolve, dispatch, controllers, ...).
        if (mv.isError()) {
            // Invokes error with forwarding to error.jsp (see web.xml)
            resp.sendError(((ErrorResult) mv).getModel().getCode());
        } else {
            // Forwards to specified jsp-view with model as attribute.
            req.getRequestDispatcher(mv.getViewName()).forward(req, resp);
        }
    }

    // Definitely should be some reflection magic here! :)
    private ModelViewResult dispatchGet(HttpServletRequest req, HttpServletResponse resp, ResolvedRequest resolved)
            throws ServletException, IOException {

        ModelViewResult result = null;

        switch (resolved.controller) {
            case OrdersController.CONTROLLER_URI:
            default:
                result = dispatchGetOrders(resolved);
        }

        return result;
    }

    // Should go to reflection-based implementation
    private ModelViewResult dispatchGetOrders(ResolvedRequest resolved) {
        OrdersController ordersController = null;
        try {
            ordersController = new OrdersController();
        } catch (NamingException e) {
            return ErrorResult.of("Cannot establish JNDI resource :(", e.getMessage());
        }
        switch (resolved.action != null ? resolved.action : "") {
            case OrdersController.ACTION_GET_ALL_BY_FILTER:
                return ordersController.getAllByFilter(resolved.params);
            case OrdersController.ACTION_GET_EDIT:
                return ordersController.getEdit(resolved.params);
            default:
                return ordersController.index(resolved.params);
        }
    }

    /**
     * Represents resolved request based on HttpServletRequest.
     * Pattern is HTTP_VERB: /controller/action/?query
     */
    private static final class ResolvedRequest {
        public static enum HTTP_VERB {GET, POST, OTHER}

        public final HTTP_VERB verb;
        public final String controller;
        public final String action;
        public final Map<String, String[]> params;

        private ResolvedRequest(HTTP_VERB verb, String controller, String action, Map<String, String[]> params) {
            this.verb = verb;
            this.controller = controller;
            this.action = action;
            this.params = params;
        }
    }
}
