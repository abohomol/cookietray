package com.abohomol.cookietray;

import android.util.Log;

import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Acquires the real URI from the cookie "domain" and "path" attributes, if they
 * are not set then uses the originally provided URI (coming from the response).
 */
class OriginUri {

    private static final String LOG_TAG = OriginUri.class.getSimpleName();

    private final URI uri;

    OriginUri(HttpCookie cookie, URI associated) {
        notNull(cookie, "Cookie can't be null");
        notNull(associated, "URI can't be null");
        URI uri = associated;
        String domain = cookie.getDomain();
        if (domain != null) {
            if (domain.charAt(0) == '.') {
                domain = domain.substring(1);
            }
            try {
                String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
                String path = cookie.getPath() == null ? "/" : cookie.getPath();
                uri = new URI(scheme, domain, path, null);
            } catch (URISyntaxException e) {
                Log.w(LOG_TAG, "Error while getting cookie URI", e);
            }
        }
        this.uri = uri;
    }

    URI uri() {
        return uri;
    }

    private void notNull(Object object, String message) {
        if (object == null)
            throw new IllegalArgumentException(message);
    }
}
