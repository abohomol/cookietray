package com.abohomol.cookietray;

import android.util.Log;

import com.abohomol.cookietray.utils.ByteUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.HttpCookie;

/**
 * Wrapper for {@link HttpCookie} that enables cookies serialization.
 */
class SerializableCookie implements Serializable {

    private static final String LOG_TAG = SerializableCookie.class.getSimpleName();
    private static final long serialVersionUID = 6374381323722046732L;

    private transient HttpCookie cookie;

    SerializableCookie(HttpCookie cookie) {
        this.cookie = cookie;
    }

    SerializableCookie(String asString) {
        byte[] bytes = ByteUtils.hexStringToByteArray(asString);
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(
                    new ByteArrayInputStream(bytes)
            );
            this.cookie = ((SerializableCookie) objectInputStream.readObject()).cookie;
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalArgumentException("Provided data couldn't be decoded", e);
        }
    }

    HttpCookie httpCookie() {
        return cookie;
    }

    String asString() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(this);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Cookie couldn't be encoded", e);
            return null;
        }

        return ByteUtils.byteArrayToHexString(os.toByteArray());
    }

    private boolean getHttpOnly() {
        try {
            Field httpOnlyField = initHttpOnlyField();
            return (boolean) httpOnlyField.get(cookie);
        } catch (Exception e) {
            Log.w(LOG_TAG, "Error in getHttpOnly");
        }
        return false;
    }

    private void setHttpOnly(boolean httpOnly) {
        try {
            Field httpOnlyField = initHttpOnlyField();
            httpOnlyField.set(cookie, httpOnly);
        } catch (Exception e) {
            Log.w(LOG_TAG, "Error in setHttpOnly");
        }
    }

    private Field initHttpOnlyField() throws NoSuchFieldException {
        Field fieldHttpOnly = cookie.getClass().getDeclaredField("httpOnly");
        fieldHttpOnly.setAccessible(true);
        return fieldHttpOnly;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(cookie.getName());
        out.writeObject(cookie.getValue());
        out.writeObject(cookie.getComment());
        out.writeObject(cookie.getCommentURL());
        out.writeObject(cookie.getDomain());
        out.writeLong(cookie.getMaxAge());
        out.writeObject(cookie.getPath());
        out.writeObject(cookie.getPortlist());
        out.writeInt(cookie.getVersion());
        out.writeBoolean(cookie.getSecure());
        out.writeBoolean(cookie.getDiscard());
        out.writeBoolean(getHttpOnly());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        String name = (String) in.readObject();
        String value = (String) in.readObject();
        cookie = new HttpCookie(name, value);
        cookie.setComment((String) in.readObject());
        cookie.setCommentURL((String) in.readObject());
        cookie.setDomain((String) in.readObject());
        cookie.setMaxAge(in.readLong());
        cookie.setPath((String) in.readObject());
        cookie.setPortlist((String) in.readObject());
        cookie.setVersion(in.readInt());
        cookie.setSecure(in.readBoolean());
        cookie.setDiscard(in.readBoolean());
        setHttpOnly(in.readBoolean());
    }

}