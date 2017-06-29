package com.perimeterx.api.remoteconfigurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perimeterx.http.PXClient;
import com.perimeterx.http.PXHttpClient;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.api.PerimeterX;
import com.perimeterx.models.configuration.PXConfigurationStub;
import com.perimeterx.utils.Constants;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by nitzangoldfeder on 19/06/2017.
 */
public class DefaultRemoteConfigurationManager extends TimerTask implements RemoteConfigurationManager{

    private PXClient httpClient;
    private Logger logger = LoggerFactory.getLogger(PerimeterX.class);
    private PXConfiguration pxConfiguration;
    private ObjectMapper objectMapper;

    public DefaultRemoteConfigurationManager(PXConfiguration pxConfiguration, PXClient httpClient){
        logger.debug("DefaultRemoteConfigurationManager[init]");
        this.pxConfiguration = pxConfiguration;
        this.objectMapper = new ObjectMapper();
        this.httpClient = httpClient;
    }

    @Override
    public boolean getConfiguration() {
        PXConfigurationStub pxConfigurationStub = httpClient.getConfigurationFromServer();
        if (pxConfigurationStub != null){
            int apiTimeout = pxConfiguration.getApiTimeout();
            int connectionTimeout = pxConfiguration.getConnectionTimeout();
            pxConfiguration.updateConfigurationFromStub(pxConfigurationStub);
            if (pxConfigurationStub.getS2sTimeout() != apiTimeout || pxConfigurationStub.getApiConnectTimeout() != connectionTimeout){
                logger.debug("DefaultRemoteConfigurationManager[getConfiguration]: api/connection timeout values were changed, updating client");
                httpClient.updateHttpClient();
            }
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        // Fetch the configuration from server
        boolean isConfigurationFetched = getConfiguration();

        // On first run, if configuration failed to updated, disable module
        if (this.pxConfiguration.getChecksum() == null && !isConfigurationFetched) {
            logger.debug("DefaultRemoteConfigurationManager[run]: switching module to disable, failed to pull on");
            this.pxConfiguration.disableModule();
        }
    }
}
