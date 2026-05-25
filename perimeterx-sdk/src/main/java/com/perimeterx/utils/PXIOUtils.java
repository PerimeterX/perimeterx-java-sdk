package com.perimeterx.utils;

import java.io.*;

public final class PXIOUtils {
    final static int BUFFER_SIZE = 8192;

    private PXIOUtils() {

    }

    public static void copy(InputStream source, OutputStream target) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(source, BUFFER_SIZE);
            BufferedOutputStream bos = new BufferedOutputStream(target, BUFFER_SIZE)) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;

            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
        }
    }

}
