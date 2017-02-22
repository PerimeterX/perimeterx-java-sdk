package com.perimeterx.http;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nitzangoldfeder on 22/02/2017.
 */
public class PXHttpClientAsyncHandler extends AsyncCompletionHandler<Response> {

    private Logger logger = LoggerFactory.getLogger(PXHttpClient.class);

    @Override
    public Response onCompleted(Response response) throws Exception {
        logger.info("Risk API returned response: {}", response);
        return null;
    }


    @Override
    public void onThrowable(Throwable t){
        logger.error("Risk API returned error: {}", t.getMessage());
    }
}
