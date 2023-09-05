package com.perimeterx.http;

import java.io.Closeable;
import java.io.IOException;

public interface IPXHttpClient extends Closeable {
    default void init() throws IOException {
    }

    IPXIncomingResponse send(IPXOutgoingRequest request) throws IOException;

    void sendAsync(IPXOutgoingRequest request) throws IOException;
}
