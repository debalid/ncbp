package com.debalid.mvc;

import com.debalid.mvc.util.HttpVerb;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
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
