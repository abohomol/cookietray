package com.abohomol.cookietray.utils;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class ByteUtilsTest {

    @Test
    public void shouldConvertBytesToHexAndViceVersa() {
        byte[] data = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 8, 8, 9, 0};
        String hexString = ByteUtils.byteArrayToHexString(data);
        assertArrayEquals(data, ByteUtils.hexStringToByteArray(hexString));
    }
}
