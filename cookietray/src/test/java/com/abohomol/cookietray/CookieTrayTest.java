package com.abohomol.cookietray;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CookieTrayTest {

    private SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
    private SharedPreferences preferences;

    private final Map<String, String> cookies = new HashMap<>();

    @Before
    @SuppressLint("all")
    public void setUp() {
        preferences = mock(SharedPreferences.class);
        doReturn(editor).when(preferences).edit();
        doReturn(cookies).when(preferences).getAll();
    }

    private void addCookies(int count, boolean expired) {
        for (int i = 0; i < count; i++) {
            URI uri = URI.create("http://google.com/home" + i);
            HttpCookie cookie = new HttpCookie("name" + i, "value" + i);
            if (expired) {
                cookie.setMaxAge(0);
            }
            cookies.put(uri.toString() + "|" + cookie.getName(), new SerializableCookie(cookie).asString());
        }
    }

    @Test
    public void shouldReturnNoCookiesOnEmptyStore() {
        CookieStore store = new CookieTray(preferences);
        assertTrue(store.getCookies().isEmpty());
    }

    @Test
    public void shouldReturnNoUrisOnEmptyStore() {
        CookieStore store = new CookieTray(preferences);
        assertTrue(store.getURIs().isEmpty());
    }

    @Test
    public void shouldReturnAllCookies() {
        addCookies(10, false);
        CookieStore store = new CookieTray(preferences);
        assertEquals(10, store.getCookies().size());
    }

    @Test
    public void shouldFilterCookiesOutByUri() {
        addCookies(10, false);
        CookieStore store = new CookieTray(preferences);
        assertEquals(1, store.get(URI.create("http://google.com/home0")).size());
    }

    @Test
    public void shouldFilterOutExpiredCookies() {
        addCookies(10, true);
        CookieStore store = new CookieTray(preferences);
        assertEquals(0, store.getCookies().size());
    }

    @Test
    public void shouldReturnStoredUris() {
        addCookies(7, false);
        CookieStore store = new CookieTray(preferences);
        assertEquals(7, store.getURIs().size());
    }

    @Test
    public void shouldAddCookieToSharedPreferences() throws URISyntaxException {
        URI uri = URI.create("http://google.com");
        CookieStore store = new CookieTray(preferences);
        HttpCookie httpCookie = new HttpCookie("name", "value");
        store.add(uri, httpCookie);
        String cookieVal = new SerializableCookie(httpCookie).asString();
        verify(editor).putString(uri.toString() + "|" + httpCookie.getName(), cookieVal);
    }

    @Test
    public void shouldBeAbleToRemoveCookie() {
        addCookies(1, false);
        CookieStore store = new CookieTray(preferences);
        store.remove(URI.create("http://google.com/home0"), new HttpCookie("name0", "value0"));
        verify(editor).remove("http://google.com/home0|name0");
    }

    @Test
    public void shouldBeAbleToRemoveAllCookies() {
        doReturn(editor).when(editor).clear();
        CookieStore store = new CookieTray(preferences);
        store.removeAll();
        verify(editor).clear();
        assertTrue("No cookies should be present after removing", store.getCookies().isEmpty());
    }

}
