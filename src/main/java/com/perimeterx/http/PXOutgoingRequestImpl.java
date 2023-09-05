package com.perimeterx.http;

import lombok.*;

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
    private final String body = null;

    @Singular
    private final List<PXHttpHeader> headers;

}
