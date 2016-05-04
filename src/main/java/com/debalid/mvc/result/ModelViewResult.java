package com.debalid.mvc.result;

/**
 * Represents result of http request based on simple model and jsp view.
 * Created by debalid on 01.05.2016.
 */
public class ModelViewResult<T> extends ActionResult {
    private final String viewName;
    private final String modelName;
    private final T model;

    protected ModelViewResult(String modelName, T model, String viewName) {
        if (viewName == null || modelName == null || model == null)
            throw new IllegalArgumentException("Args cannot be null");
        if (!viewName.matches(".*\\.jsp"))
            throw new RuntimeException("Only jsp views are allowed");
        this.viewName = viewName;
        this.modelName = modelName;
        this.model = model;
    }

    public static <V> ModelViewResult<V> of(String modelName, V model, String viewName) {
        return new ModelViewResult<V>(modelName, model, viewName);
    }

    public String getViewName() {
        return viewName;
    }

    public String getModelName() {
        return modelName;
    }

    public T getModel() {
        return model;
    }

    @Override
    public Type getType() {
        return Type.ModelView;
    }
}
