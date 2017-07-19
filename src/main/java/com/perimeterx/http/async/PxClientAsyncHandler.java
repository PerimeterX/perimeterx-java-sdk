package com.perimeterx.http.async;

import com.perimeterx.http.PXHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nitzangoldfeder on 27/02/2017.
 */
public class PxClientAsyncHandler implements FutureCallback<HttpResponse> {

    private static final Logger logger = LoggerFactory.getLogger(PxClientAsyncHandler.class);

    @Override
    public void completed(HttpResponse httpResponse) {
        logger.info("Response completed {}", httpResponse.getEntity());
    }

    @Override
    public void failed(Exception e) {
        logger.error("Response failed {}", e.getMessage());
    }

    @Override
    public void cancelled() {
        logger.warn("Response was canceled");
    }
}
