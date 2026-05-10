package com.perimeterx.http;

import java.util.List;

public interface IPXOutgoingRequest {
    String getUrl();
    PXHttpMethod getHttpMethod();
    PXRequestBody getBody();
    List<PXHttpHeader> getHeaders();
}