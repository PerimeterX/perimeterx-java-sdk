package com.perimeterx.api.remoteconfigurations;

import com.perimeterx.http.PXHttpClient;
import com.perimeterx.models.configuration.PXDynamicConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by nitzangoldfeder on 05/07/2017.
 */
public class DefaultRemoteConfigManager implements RemoteConfigurationManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRemoteConfigManager.class);

    private PXHttpClient pxClient;

    public DefaultRemoteConfigManager(PXHttpClient pxClient){
        this.pxClient = pxClient;
    }

    @Override
    public PXDynamicConfiguration getConfiguration() {
        logger.debug("Getting configuration from server");
        PXDynamicConfiguration dynamicConfig = null;
        try {
            dynamicConfig = pxClient.getConfigurationFromServer();
        } catch (IOException e){
            logger.debug("an error was caught while trying to close the connection {}", e.getMessage() );
        }
        return dynamicConfig;
    }

}
