package com.perimeterx.api.remoteconfigurations;

import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.configuration.PXDynamicConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class TimerConfigUpdater extends TimerTask {

    private static final Logger logger = LoggerFactory.getLogger(TimerConfigUpdater.class);

    private RemoteConfigurationManager configManager;
    private PXConfiguration pxConfiguration;

    public TimerConfigUpdater(RemoteConfigurationManager configManager, PXConfiguration pxConfiguration) {
        logger.debug("TimerConfigUpdater[init]");
        this.configManager = configManager;
        this.pxConfiguration = pxConfiguration;
    }

    @Override
    public void run() {
        try{
            // Fetch the configuration from server
            PXDynamicConfiguration dynamicConfig = configManager.getConfiguration();
            if (dynamicConfig != null){
                configManager.updateConfiguration(dynamicConfig);
            } else if (pxConfiguration.getChecksum() == null) {
                configManager.disableModuleOnError();
            }
        } catch (IOException e){
            if (pxConfiguration.getChecksum() == null){
                configManager.disableModuleOnError();
            }
        }
    }


    /**
     * Sets a new timer object and runs its execution method
     */
    public void schedule() {
        Timer timer = new Timer();
        timer.schedule(this,pxConfiguration.getRemoteConfigurationDelay(),  pxConfiguration.getRemoteConfigurationInterval());
    }


}
