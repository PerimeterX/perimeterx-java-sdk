package com.perimeterx.http;

import lombok.*;

import java.io.InputStream;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@ToString
public class PXOutgoingRequestImpl implements IPXOutgoingRequest {

    private final String url;

    @Builder.Default
    private final PXHttpMethod httpMethod = PXHttpMethod.GET;

    @Builder.Default
    private final InputStream body = null;

    @Singular
    private final List<PXHttpHeader> headers;

}
