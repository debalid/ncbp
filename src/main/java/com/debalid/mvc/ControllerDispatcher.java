package com.debalid.mvc;

import com.debalid.mvc.result.ActionResult;
import com.debalid.mvc.result.ErrorResult;
import com.debalid.mvc.result.ModelViewResult;
import com.debalid.mvc.result.RedirectResult;
import com.debalid.mvc.util.HttpVerb;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Dispatcher servlet to determine what methods of what controllers should be executed based on request params.
 * Created by debalid on 20.04.2016.
 */
public class ControllerDispatcher extends HttpServlet {
    // uri pattern: /controller/action/?query
    private static final String URI_REGEXP = "/[A-z]*(/|/[A-z]*/?)?(\\?.*)?";

    //default controller for root `/`
    private String defaultController;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(HttpVerb.GET, req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(HttpVerb.POST, req, resp);
    }

    private void processRequest(HttpVerb verb, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        //Try to resolve specified GET request.
        ResolvedRequest resolved = resolveRequest(verb, req);
        if (resolved == null) { //not resolved
            finishRequest(req, resp,
                    ErrorResult.of("Cannot resolve GET request",
                            "Cannot resolve specified uri `" + req.getServletPath() + "`.",
                            404));
            return;
        }

        // Dispatch specified request and action.
        ActionResult result = dispatchController(req, resp, resolved);

        // Process resolved and dispatched request.
        finishRequest(req, resp, result);
    }

    private ResolvedRequest resolveRequest(HttpVerb verb, HttpServletRequest req) {
        if (this.defaultController == null) this.defaultController = this.getInitParameter("DefaultController");
        if (this.defaultController == null) throw new RuntimeException("Default controller must be declared!");

        String uri = req.getServletPath();
        // pattern: /controller/action/?query
        if (!uri.matches(URI_REGEXP)) {
            return null;
        }

        String[] parts = uri.split("/");
        String controller = parts.length > 0
                ? parts[1].substring(0, 1).toUpperCase() + parts[1].substring(1).toLowerCase()
                : this.defaultController;
        String action = parts.length > 2 ? parts[2] : "index";
        Map<String, String[]> params = req.getParameterMap();

        return new ResolvedRequest(verb, controller, action, params);
    }

    private void finishRequest(HttpServletRequest req, HttpServletResponse resp, ActionResult ar)
            throws IOException, ServletException {
        switch (ar.getType()) {
            case ModelView:
                // Forwards to specified jsp-view with model as attribute.
                ModelViewResult mv = (ModelViewResult) ar;
                req.setAttribute(mv.getModelName(), mv.getModel());
                req.getRequestDispatcher(mv.getViewName()).forward(req, resp);
                return;
            case Redirect:
                RedirectResult rr = (RedirectResult)ar;
                resp.sendRedirect(rr.getRedirectURL().toString());
                return;
            case Error: // Process error from any layer (resolve, dispatch, controllers, ...).
            default:
                // Invokes error with forwarding to error.jsp (see web.xml)
                req.setAttribute("error", ar);
                resp.sendError(ar.getCode());
        }
    }

    // Definitely should be some reflection magic here! :)
    private ActionResult dispatchController(HttpServletRequest req, HttpServletResponse resp, ResolvedRequest resolved)
            throws ServletException, IOException {

        try {
            //Instantiate request-scoped controller...
            String controllersPackage = this.getInitParameter("ControllersPackage");
            if (controllersPackage == null) {
                return ErrorResult.of("Can't find proper controllers package!", null);
            }
            Class controllerClass = Class.forName(controllersPackage + "." + resolved.controller + "Controller");
            Controller controller = (Controller)controllerClass.newInstance();
            controller.setRequest(req);
            controller.setResponse(resp);

            //...and invoke action
            Method actionMethod = controllerClass
                    .getDeclaredMethod(resolved.action.toLowerCase(), HttpVerb.class, Map.class);
            ActionResult result = (ActionResult) actionMethod.invoke(controller, resolved.verb, resolved.params);
            if (result == null)
                throw new RuntimeException("Action `" + resolved.action +
                        "` of controller `" + resolved.controller +
                        "` returned null");
            return result;
        } catch (Exception e) {
            return ErrorResult.of("Cannot dispatch " + resolved.verb.toString().toUpperCase() + " request " +
                    "with controller `" + resolved.controller + "` and action `" + resolved.action + "`.",
                    e.getMessage(),
                    500);
        }
    }

    /**
     * Represents resolved request based on HttpServletRequest.
     * Pattern is HTTP_VERB: /controller/action/?query
     */
    private static final class ResolvedRequest {
        public final HttpVerb verb;
        public final String controller;
        public final String action;
        public final Map<String, String[]> params;

        private ResolvedRequest(HttpVerb verb, String controller, String action, Map<String, String[]> params) {
            this.verb = verb;
            this.controller = controller;
            this.action = action;
            this.params = params;
        }
    }
}
