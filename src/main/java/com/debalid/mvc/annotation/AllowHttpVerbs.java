package com.debalid.mvc.annotation;

import com.debalid.mvc.util.HttpVerb;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that shoul be used in controllers methods to determine what http requests types it handles.
 * Created by debalid on 04.05.2016.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowHttpVerbs {
    HttpVerb[] values();
}
