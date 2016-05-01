package com.debalid.ncbp;

/**
 * Represents custom error.
 * Created by debalid on 01.05.2016.
 */
public class ErrorResult extends ModelViewResult<ErrorResult.Error> {
    /**
     * Returns error with corresponding information.
     * @param reason Short description of error.
     * @param explicit Full description of error.
     * @return
     */
    public static ErrorResult of(String reason, String explicit) {
        return new ErrorResult("error", new ErrorResult.Error(reason, explicit), "error.jsp");
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

        public Error(String reason, String explicit) {
            if (reason == null) throw new IllegalArgumentException("Reason should not be null");
            this.reason = reason;
            this.explicit = explicit;
        }

        public String getReason() {
            return reason;
        }

        public String getExplicit() {
            return explicit;
        }
    }
}
