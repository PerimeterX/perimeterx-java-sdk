package com.perimeterx.api.remoteconfigurations;

import com.perimeterx.http.PXHttpClient;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.configuration.PXDynamicConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Observable;

/**
 * Created by nitzangoldfeder on 05/07/2017.
 */
public class ObserverRemoteConfigManager extends Observable implements RemoteConfigurationManager {

    private Logger logger = LoggerFactory.getLogger(ObserverRemoteConfigManager.class);

    private PXConfiguration pxConfiguration;
    private PXHttpClient pxClient;

    public ObserverRemoteConfigManager(PXConfiguration pxConfiguration, PXHttpClient pxClient){
        this.pxConfiguration = pxConfiguration;
        this.pxClient = pxClient;

        addObserver(pxClient);
        addObserver(pxConfiguration);
    }

    @Override
    public void getConfiguration() {
        logger.debug("Getting configuration from server");
        try {
            PXDynamicConfiguration pxDynamicConfiguration = pxClient.getConfigurationFromServer();
            if (pxDynamicConfiguration != null) {
                setChanged();
                logger.debug("Updating configuration");
                notifyObservers(pxDynamicConfiguration);
                clearChanged();
            } else if (pxConfiguration.getChecksum() == null) {
                //Disable module if its first time run
                logger.debug("Disable PerimeterX module because was unable to fetch first configuration file");
                pxConfiguration.disableModule();
            }
        } catch (IOException e){
            logger.debug("an error was cought while trying to close the connection {}", e.getMessage() );
        }
    }

}
