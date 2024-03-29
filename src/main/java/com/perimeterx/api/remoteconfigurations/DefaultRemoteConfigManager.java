package com.perimeterx.api.remoteconfigurations;

import com.perimeterx.http.PXClient;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.configuration.PXDynamicConfiguration;
import com.perimeterx.utils.logger.IPXLogger;

/**
 * Created by nitzangoldfeder on 05/07/2017.
 */
public class DefaultRemoteConfigManager implements RemoteConfigurationManager {

    private final IPXLogger logger;

    private PXClient pxClient;
    private PXConfiguration pxConfiguration;

    public DefaultRemoteConfigManager(PXConfiguration pxConfiguration, PXClient pxClient) {
        this.logger = pxConfiguration.getLoggerFactory().getRequestContextLogger();
        this.pxClient = pxClient;
        this.pxConfiguration = pxConfiguration;
    }

    @Override
    public PXDynamicConfiguration getConfiguration() {
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
