package com.perimeterx.api.remoteconfigurations;

import com.perimeterx.http.PXClient;
import com.perimeterx.http.PXHttpClient;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.configuration.PXDynamicConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by nitzangoldfeder on 05/07/2017.
 */
public class DefaultRemoteConfigManager implements RemoteConfigurationManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRemoteConfigManager.class);

    private PXClient pxClient;
    private PXConfiguration pxConfiguration;

    public DefaultRemoteConfigManager(PXConfiguration pxConfiguration, PXClient pxClient){
        this.pxClient = pxClient;
        this.pxConfiguration = pxConfiguration;
    }

    @Override
    public PXDynamicConfiguration getConfiguration() throws IOException {
        logger.debug("Getting configuration from server");
        return pxClient.getConfigurationFromServer();
    }

    @Override
    public void updateConfiguration(PXDynamicConfiguration pxDynamicConfiguration) {
        pxConfiguration.update(pxDynamicConfiguration);
    }

    @Override
    public void disableModuleOnError() {
        pxConfiguration.disableModule();
    }

}
