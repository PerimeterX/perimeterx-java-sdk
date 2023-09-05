package com.perimeterx.http;

import lombok.*;

import java.io.ByteArrayInputStream;
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
    private final PXRequestBody body = null;

    @Singular
    private final List<PXHttpHeader> headers;

    public static class PXOutgoingRequestImplBuilder {
        public PXOutgoingRequestImplBuilder stringBody(String body) {
            if (body == null) {
                this.body(null);
                return this;
            }

            PXRequestBody b = new PXRequestBody(
                    new ByteArrayInputStream(body.getBytes()),
                    body.length()
            );
            return this.body(b);
        }
    }
}
