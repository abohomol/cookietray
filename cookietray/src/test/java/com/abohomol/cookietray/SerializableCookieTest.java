package com.abohomol.cookietray;

import org.junit.Test;

import java.net.HttpCookie;

import static org.junit.Assert.assertEquals;

public class SerializableCookieTest {

    @Test
    public void shouldSerializeCookie() throws Exception {
        SerializableCookie origin = new SerializableCookie(createHttpCookie());
        SerializableCookie deserialized = new SerializableCookie(origin.asString());
        assertEquals(origin.httpCookie(), deserialized.httpCookie());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotSerializeCookieFromFakeString() {
        String fakeHttpCookieAsString = "qwertyuiop[]asdfghjklasdfghjklzxcvbn";
        new SerializableCookie(fakeHttpCookieAsString);
    }

    private HttpCookie createHttpCookie() {
        HttpCookie cookie = new HttpCookie("name", "value");
        cookie.setPath("/");
        cookie.setComment("comment");
        cookie.setCommentURL("localhost");
        cookie.setDiscard(true);
        cookie.setDomain("home");
        cookie.setMaxAge(System.currentTimeMillis());
        cookie.setPortlist("42");
        cookie.setSecure(true);
        cookie.setVersion(1);
        return cookie;
    }
}
