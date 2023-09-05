package com.perimeterx.http;

import java.io.Closeable;
import java.io.IOException;

public interface IPXIncomingResponse extends Closeable {
    String body() throws IOException;
    PXHttpStatus status();
    PXHttpHeader[] headers();
}
