package com.abohomol.cookietray;

import org.junit.Test;

import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class OriginUriTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNullCookie() throws URISyntaxException {
        new OriginUri(null, new URI("http", "google.com", "/", ""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNullUri() {
        new OriginUri(new HttpCookie("name", "value"), null);
    }

    @Test
    public void shouldOriginUriReturnAssociatedIfCookieUriIsEmpty() throws URISyntaxException {
        URI associated = new URI("http", "google.com", "/", "");
        HttpCookie cookie = new HttpCookie("name", "value");
        OriginUri uri = new OriginUri(cookie, associated);
        assertEquals(associated, uri.uri());
    }

    @Test
    public void shouldOriginUriReturnUriFromHttpCookie() throws URISyntaxException {
        URI associated = new URI("http", "google.com", "/", "");
        HttpCookie cookie = new HttpCookie("name", "value");
        cookie.setDomain("abc.xyz");
        OriginUri uri = new OriginUri(cookie, associated);
        assertNotEquals(associated, uri.uri());
    }

    @Test
    public void shouldOriginUriProperlyFormatUri() throws URISyntaxException {
        URI associated = new URI("http", "google.com", "/", "");
        HttpCookie cookie = new HttpCookie("name", "value");
        cookie.setDomain("abc.xyz");
        cookie.setPath("/home");
        OriginUri uri = new OriginUri(cookie, associated);
        assertEquals("http://abc.xyz/home", uri.uri().toString());
    }

    @Test
    public void shouldSkipDotWhileFormattingUri() throws URISyntaxException {
        URI associated = new URI("http", "google.com", "/", "");
        HttpCookie cookie = new HttpCookie("name", "value");
        cookie.setDomain(".abc.xyz");
        OriginUri uri = new OriginUri(cookie, associated);
        assertEquals("http://abc.xyz/", uri.uri().toString());
    }
}
