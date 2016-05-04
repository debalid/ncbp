package com.debalid.mvc;

import com.debalid.mvc.annotation.AllowHttpVerbs;
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
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

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

    /**
     * Main method that processes current request.
     * Includes 3 steps: resolving, dispatching, finishing (executing).
     * Resolving means creating of ResolvedRequest instance.
     * Dispatching means instantiation of controller and getting its action result (ActionResult instance)
     * Executing means processing of given ActionResult instance - it may be jsp-view forwarding, redirect, error, etc...
     *
     * @param verb Http verb of current request (GET, POST, ...)
     * @param req  Instance of HttpServletRequest that will be injected in controller.
     * @param resp Instance of HttpServletResponse that will be injected in controller.
     * @throws ServletException
     * @throws IOException
     * @see com.debalid.mvc.ControllerDispatcher.ResolvedRequest
     * @see com.debalid.mvc.result.ActionResult
     */
    private void processRequest(HttpVerb verb, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        //Try to resolve specified request.
        ResolvedRequest resolved = resolveRequest(verb, req);

        // Dispatch specified request and action.
        ActionResult result = dispatchControllerAndAction(req, resp, resolved);

        // Process resolved and dispatched request.
        finishRequest(req, resp, result);
    }

    /**
     * Creating ResolvedRequest object based on current request.
     *
     * @param verb Http verb of current request (GET, POST, ...)
     * @param req  Instance of HttpServletRequest that will be used for building ResolvedRequest.
     * @return Instance that represents information about controller and action that needs to be dispatched.
     * @see com.debalid.mvc.ControllerDispatcher.ResolvedRequest
     */
    protected ResolvedRequest resolveRequest(HttpVerb verb, HttpServletRequest req) {
        if (this.defaultController == null) this.defaultController = this.getInitParameter("DefaultController");
        if (this.defaultController == null) throw new RuntimeException("Default controller must be declared!");

        String uri = req.getServletPath();
        // pattern: /controller/action/?query
        if (!uri.matches(URI_REGEXP)) {
            //not resolved
            throw new RuntimeException("Cannot resolve specified uri `" + req.getServletPath() + "`.");
        }

        String[] parts = uri.split("/");
        String controller = parts.length > 0
                ? parts[1].substring(0, 1).toUpperCase() + parts[1].substring(1).toLowerCase()
                : this.defaultController;
        String action = parts.length > 2 ? parts[2] : "index";
        Map<String, String[]> params = req.getParameterMap();

        return new ResolvedRequest(verb, controller, action, params);
    }

    /**
     * Executes result of controller's action. It may be jsp-view forward, redirect, error, ...
     *
     * @param req  Instance of HttpServletRequest that will be used for executing current request.
     * @param resp Instance of HttpServletResponse that will be used for executing current request.
     * @param ar   Controller's action result to be executed.
     * @throws IOException
     * @throws ServletException
     * @see com.debalid.mvc.result.ActionResult
     */
    protected void finishRequest(HttpServletRequest req, HttpServletResponse resp, ActionResult ar)
            throws IOException, ServletException {
        switch (ar.getType()) {
            case ModelView:
                // Forwards to specified jsp-view with model as attribute.
                ModelViewResult mv = (ModelViewResult) ar;
                req.setAttribute(mv.getModelName(), mv.getModel());
                req.getRequestDispatcher(mv.getViewName()).forward(req, resp);
                return;
            case Redirect:
                RedirectResult rr = (RedirectResult) ar;
                resp.sendRedirect(rr.getRedirectURL().toString());
                return;
            case Error: // Process error from any layer (resolve, dispatch, controllers, ...).
            default:
                // Invokes error with forwarding to error.jsp (see web.xml)
                req.setAttribute("error", ar);
                resp.sendError(ar.getCode());
        }
    }

    /**
     * Finds corresponding controller(class) and action (method) based on the resolved request.
     * Definitely should be some reflection magic here! :)
     *
     * @param req      Instance of HttpServletRequest that will be injected in controller.
     * @param resp     Instance of HttpServletResponse that will be injected in controller.
     * @param resolved Object representing current resolved request.
     * @return Result of dispatched action.
     * @throws ServletException
     * @throws IOException
     */
    private ActionResult dispatchControllerAndAction(HttpServletRequest req, HttpServletResponse resp, ResolvedRequest resolved)
            throws ServletException, IOException {

        try {
            //Instantiate request-scoped controller...
            Controller controller = this.buildController(req, resp, resolved);

            //...and invoke action
            Method actionMethod = controller.getClass()
                    .getDeclaredMethod(resolved.action.toLowerCase(), Map.class);
            checkHttpVerbAnnotations(actionMethod, resolved);

            ActionResult result = (ActionResult) actionMethod.invoke(controller, resolved.params);
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

    //Builds controller's instance based on resolved request.
    private Controller buildController(HttpServletRequest req, HttpServletResponse resp, ResolvedRequest resolved)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String controllersPackage = this.getInitParameter("ControllersPackage");
        if (controllersPackage == null) {
            throw new RuntimeException("Can't find proper controllers package!");
        }
        Class controllerClass = Class.forName(controllersPackage + "." + resolved.controller + "Controller");
        Controller controller = (Controller) controllerClass.newInstance();
        controller.setRequest(req);
        controller.setResponse(resp);
        controller.setHttpVerb(resolved.verb);
        return controller;
    }

    // Checks provided http verb annotations for resolved controller's method.
    private void checkHttpVerbAnnotations(Method actionMethod, ResolvedRequest resolved) {
        AllowHttpVerbs ahv = actionMethod.getDeclaredAnnotation(AllowHttpVerbs.class);
        if (ahv == null) {
            throw new RuntimeException("Action `" + resolved.action +
                    "` of controller `" + resolved.controller +
                    "` does not allow any of http verbs!");
        }
        if (!Arrays.asList(ahv.values()).contains(resolved.verb)) {
            throw new RuntimeException("Action `" + resolved.action +
                    "` of controller `" + resolved.controller +
                    "` does not allow http verb `" + resolved.verb + "`!");
        }
    }

    /**
     * Represents resolved request based on HttpServletRequest.
     * Pattern is HTTP_VERB: /controller/action/?query
     */
    protected static final class ResolvedRequest {
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
