package com.abohomol.cookietray;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of {@link CookieStore} that allows storing cookies in SharedPreferences.
 */
public class CookieTray implements CookieStore {

    private static final String LOG_TAG = CookieTray.class.getSimpleName();

    private static final String STORE_NAME = "cookie_tray";
    private static final String COOKIE_KEY_DELIMITER = "|";
    private static final String COOKIE_KEY_DELIMITER_REGEXP = "\\" + COOKIE_KEY_DELIMITER;

    private final Map<URI, Set<HttpCookie>> cookiesCache = new HashMap<>();
    private final SharedPreferences sharedPreferences;

    /**
     * Initialize store with specific SharedPreferences.
     *
     * @param preferences used to store cookies
     */
    public CookieTray(SharedPreferences preferences) {
        this.sharedPreferences = preferences;
        loadCache();
    }

    /**
     * Initialize store with application context to access SharedPreferences.
     *
     * @param context application context
     */
    public CookieTray(Context context) {
        sharedPreferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
        loadCache();
    }

    private void loadCache() {
        Map<String, ?> allPairs = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allPairs.entrySet()) {
            String[] uriAndName = entry.getKey().split(COOKIE_KEY_DELIMITER_REGEXP, 2);
            try {
                URI uri = new URI(uriAndName[0]);
                Set<HttpCookie> targetCookies = cookiesCache.get(uri);
                if (targetCookies == null) {
                    targetCookies = new HashSet<>();
                    cookiesCache.put(uri, targetCookies);
                }
                String encoded = (String) entry.getValue();
                HttpCookie cookie = new SerializableCookie(encoded).httpCookie();
                targetCookies.add(cookie);
            } catch (URISyntaxException e) {
                Log.w(LOG_TAG, "Error while loading cookies from persistence", e);
            }
        }
    }

    @Override
    public synchronized void add(URI associatedUri, HttpCookie cookie) {
        URI uri = new OriginUri(cookie, associatedUri).uri();

        Set<HttpCookie> targetCookies = cookiesCache.get(uri);
        if (targetCookies == null) {
            targetCookies = new HashSet<>();
            cookiesCache.put(uri, targetCookies);
        }
        targetCookies.remove(cookie);
        targetCookies.add(cookie);

        persistCookie(uri, cookie);
    }

    private void persistCookie(URI uri, HttpCookie cookie) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(keyForCookie(uri, cookie), new SerializableCookie(cookie).asString());
        editor.apply();
    }

    @Override
    public synchronized List<HttpCookie> get(URI uri) {
        return getValidCookies(uri);
    }

    @Override
    public synchronized List<HttpCookie> getCookies() {
        List<HttpCookie> cookies = new ArrayList<>();
        for (URI storedUri : cookiesCache.keySet()) {
            cookies.addAll(getValidCookies(storedUri));
        }
        return cookies;
    }

    private List<HttpCookie> getValidCookies(URI uri) {
        List<HttpCookie> validCookies = new ArrayList<>();
        for (URI storedUri : cookiesCache.keySet()) {
            if (uriMatch(storedUri, uri)) {
                validCookies.addAll(cookiesCache.get(storedUri));
            }
        }

        Iterator<HttpCookie> it = validCookies.iterator();
        while (it.hasNext()) {
            HttpCookie cookie = it.next();
            if (cookie.hasExpired()) {
                removeFromPersistence(uri, cookie);
                cookiesCache.get(uri).remove(cookie);
                it.remove();
            }
        }
        return validCookies;
    }

    private static boolean uriMatch(URI stored, URI requested) {
        return checkDomainsMatch(stored.getHost(), requested.getHost())
                && checkPathsMatch(stored.getPath(), requested.getPath());
    }

    /**
     * Check if the domains match according to RFC 6265
     *
     * @see <a href="http://tools.ietf.org/html/rfc6265#section-5.1.3">RFC 6265 section 5.1.3</a>
     */
    private static boolean checkDomainsMatch(String cookieHost, String requestHost) {
        return requestHost.equals(cookieHost) || requestHost.endsWith("." + cookieHost);
    }

    /**
     * Check if the paths match according to RFC 6265
     *
     * @see <a href="http://tools.ietf.org/html/rfc6265#section-5.1.4">RFC 6265 section 5.1.4</a>
     */
    private static boolean checkPathsMatch(String cookiePath, String requestPath) {
        return requestPath.equals(cookiePath) ||
                (requestPath.startsWith(cookiePath) && cookiePath.charAt(cookiePath.length() - 1) == '/') ||
                (requestPath.startsWith(cookiePath) && requestPath.substring(cookiePath.length()).charAt(0) == '/');
    }

    @Override
    public synchronized List<URI> getURIs() {
        return new ArrayList<>(cookiesCache.keySet());
    }

    @Override
    public synchronized boolean remove(URI uri, HttpCookie cookie) {
        Set<HttpCookie> targetCookies = cookiesCache.get(uri);
        boolean cookieRemoved = targetCookies != null && targetCookies.remove(cookie);
        if (cookieRemoved) {
            removeFromPersistence(uri, cookie);
        }
        return cookieRemoved;
    }

    private void removeFromPersistence(URI uri, HttpCookie cookieToRemove) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(keyForCookie(uri, cookieToRemove));
        editor.apply();
    }

    private String keyForCookie(URI uri, HttpCookie cookie) {
        return uri.toString() + COOKIE_KEY_DELIMITER + cookie.getName();
    }

    @Override
    public synchronized boolean removeAll() {
        cookiesCache.clear();
        sharedPreferences.edit().clear().apply();
        return true;
    }
}