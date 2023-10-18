package com.perimeterx.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class PXIOUtils {
    private PXIOUtils() {

    }

    public static void copy(InputStream source, OutputStream target) throws IOException {
        byte[] buf = new byte[8192];
        int length;
        while ((length = source.read(buf)) != -1) {
            target.write(buf, 0, length);
        }
    }

}
