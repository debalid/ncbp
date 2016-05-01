package com.debalid.ncbp;

/**
 * Represents custom error view and model.
 * Created by debalid on 01.05.2016.
 */
public class ErrorResult extends ModelViewResult<ErrorResult.Error> {
    /**
     * Returns error with corresponding information.
     * @param reason Short description of error.
     * @param explicit Full description of error.
     * @return ErrorResult - simple model and view representation.
     */
    public static ErrorResult of(String reason, String explicit) {
        return new ErrorResult("error", new ErrorResult.Error(reason, explicit, 500), "error.jsp");
    }

    /**
     * Returns error with corresponding information.
     * @param reason Short description of error.
     * @param explicit Full description of error.
     * @param code HTTP code of error.
     * @return ErrorResult - simple model and view representation.
     */
    public static ErrorResult of(String reason, String explicit, int code) {
        return new ErrorResult("error", new ErrorResult.Error(reason, explicit, code), "error.jsp");
    }

    protected ErrorResult(String modelName, ErrorResult.Error model, String viewName) {
        super(modelName, model, viewName);
    }

    @Override
    public boolean isError() {
        return true;
    }

    /**
     * Represents error with short and optional full description.
     */
    public static class Error {
        private final String reason;
        private final String explicit;
        private final int code;

        public Error(String reason, String explicit, int code) {
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

        public int getCode() {
            return code;
        }
    }
}
