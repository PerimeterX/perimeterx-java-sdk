package com.perimeterx.api.remoteconfigurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perimeterx.api.PXConfiguration;
import com.perimeterx.api.PerimeterX;
import com.perimeterx.models.configuration.PXConfigurationStub;
import com.perimeterx.utils.Constants;
import org.apache.commons.io.IOUtils;
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

    private CloseableHttpClient httpClient;
    private Logger logger = LoggerFactory.getLogger(PerimeterX.class);
    private PXConfiguration pxConfiguration;
    private ObjectMapper objectMapper;

    public DefaultRemoteConfigurationManager(PXConfiguration pxConfiguration, CloseableHttpClient httpClient){
        logger.debug("DefaultRemoteConfigurationManager[init]");
        this.pxConfiguration = pxConfiguration;
        this.objectMapper = new ObjectMapper();
        this.httpClient = httpClient;
    }

    @Override
    public void getConfiguration() {
        logger.debug("DefaultRemoteConfigurationManager[getConfiguration]");
        CloseableHttpResponse httpResponse;

        String queryParams = "";
        if (pxConfiguration.getChecksum() != null){
            queryParams = "?checksum=" + pxConfiguration.getChecksum();
        }

        try {
            HttpGet get = new HttpGet(Constants.REMOTE_CONFIGURATION_SERVER_URL + Constants.API_REMOTE_CONFIGUTATION + queryParams);

            get.setHeader("Authorization", "Bearer " + this.pxConfiguration.getAuthToken());
            get.setHeader("Content-Type", "application/json");

            httpResponse = httpClient.execute(get);
            if (httpResponse.getStatusLine().getStatusCode() == 200){
                String bodyContent = IOUtils.toString(httpResponse.getEntity().getContent(), UTF_8);
                PXConfigurationStub stub = objectMapper.readValue(bodyContent, PXConfigurationStub.class);
                logger.debug("DefaultRemoteConfigurationManager[getConfiguration] GET request successfully executed {}", bodyContent);
                pxConfiguration.updateConfigurationFromStub(stub);
            }else{
                // Failed to fetch the configurations
                logger.debug("DefaultRemoteConfigurationManager[getConfiguration] GET request failed to be executed");

            }
        } catch (Exception e) {
            logger.error("DefaultRemoteConfigurationManager[getConfiguration] EXCEPTION ");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        getConfiguration();
    }
}
