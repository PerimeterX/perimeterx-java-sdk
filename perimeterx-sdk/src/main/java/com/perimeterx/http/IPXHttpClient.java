package com.perimeterx.http;

import com.perimeterx.models.PXContext;

import java.io.Closeable;
import java.io.IOException;

public interface IPXHttpClient extends Closeable {
    IPXIncomingResponse send(IPXOutgoingRequest request) throws IOException;

    void sendAsync(IPXOutgoingRequest request, PXContext context) throws IOException;
}
