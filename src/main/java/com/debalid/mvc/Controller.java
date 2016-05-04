package com.debalid.mvc;

import com.debalid.mvc.util.HttpVerb;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Base class for all controllers. They are required to implement this class.
 * Action is a method that receive Map<String, String[]> args and produces ActionResult.
 * All custom actions should implement this pattern.
 * Also, this class provides injected HttpServletRequest and HttpServletResponse instances for convenience.
 * Created by debalid on 03.05.2016.
 */
public abstract class Controller {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpVerb httpVerb;

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public HttpVerb getHttpVerb() {
        return httpVerb;
    }

    public void setHttpVerb(HttpVerb httpVerb) {
        this.httpVerb = httpVerb;
    }
}
