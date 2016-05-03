package com.debalid.mvc.result;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Represents server redirect.
 * Created by debalid on 03.05.2016.
 */
public class RedirectResult extends ActionResult {
    private final URL redirectURL;

    public URL getRedirectURL() {
        return redirectURL;
    }

    protected RedirectResult(URL redirectURL) {
        this.redirectURL = redirectURL;
    }

    public static RedirectResult of(String redirect) {
        try {
            URL url = new URL(redirect);
            return new RedirectResult(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed url!");
        }
    }

    @Override
    public Type getType() {
        return Type.Redirect;
    }
}
