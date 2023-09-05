package com.perimeterx.http;

import java.io.InputStream;
import java.util.List;

public interface IPXOutgoingRequest {
    String getUrl();

    PXHttpMethod getHttpMethod();

    InputStream getBody();

    List<PXHttpHeader> getHeaders();


}