package com.debalid.mvc.result;

import javax.servlet.http.HttpServletResponse;

/**
 * Represents state of actual controller's action result.
 * Created by debalid on 03.05.2016.
 */
public abstract class ActionResult {
    public static enum Type{
        ModelView, Error, Redirect
    }
    public int getCode() {
        return HttpServletResponse.SC_OK;
    }

    public abstract Type getType();
}
