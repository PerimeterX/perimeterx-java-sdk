package com.perimeterx.http;

import java.util.List;

public interface IPXOutgoingRequest {
    String getUrl();

    PXHttpMethod getHttpMethod();

    String getBody();

    List<PXHttpHeader> getHeaders();


}