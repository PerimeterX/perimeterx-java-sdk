package com.perimeterx.http.async;

import com.perimeterx.api.PerimeterX;
import com.perimeterx.utils.logger.IPXLogger;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;

/**
 * Created by nitzangoldfeder on 27/02/2017.
 */
public class PxClientAsyncHandler implements FutureCallback<HttpResponse> {

    private static final IPXLogger logger = PerimeterX.globalLogger;

    @Override
    public void completed(HttpResponse httpResponse) {
        logger.debug("Response completed {}", httpResponse != null ? httpResponse.getEntity() : "");
    }

    @Override
    public void failed(Exception e) {
        logger.error("Response failed {}", e.getMessage());
    }

    @Override
    public void cancelled() {
        logger.debug("Response was canceled");
    }
}
