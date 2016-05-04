package com.debalid.mvc.result;

import javax.servlet.http.HttpServletResponse;

/**
 * Represents custom error view and model.
 * Created by debalid on 01.05.2016.
 */
public class ErrorResult extends ActionResult {
    private final String reason;
    private final String explicit;
    private final int code;

    protected ErrorResult(String reason, String explicit, int code) {
        if (reason == null) throw new IllegalArgumentException("Reason should not be null");
        this.reason = reason;
        this.explicit = explicit;
        this.code = code;
    }

    public String getReason() {
        return reason;
    }

    public String getExplicit() {
        return explicit;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public Type getType() {
        return Type.Error;
    }

    /**
     * Returns error with corresponding information.
     *
     * @param reason Short description of error.
     * @return ErrorResult - simple model and view representation.
     */
    public static ErrorResult of(String reason) {
        return new ErrorResult(reason, null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    /**
     * Returns error with corresponding information.
     *
     * @param reason   Short description of error.
     * @param explicit Full description of error.
     * @return ErrorResult - simple model and view representation.
     */
    public static ErrorResult of(String reason, String explicit) {
        return new ErrorResult(reason, explicit, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    /**
     * Returns error with corresponding information.
     *
     * @param reason   Short description of error.
     * @param explicit Full description of error.
     * @param code     HTTP code of error.
     * @return ErrorResult - simple model and view representation.
     */
    public static ErrorResult of(String reason, String explicit, int code) {
        return new ErrorResult(reason, explicit, code);
    }
}
