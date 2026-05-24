package com.perimeterx.http;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public interface IPXIncomingResponse extends Closeable {
    InputStream body() throws IOException;
    PXHttpStatus status();
    PXHttpHeader[] headers();
}
