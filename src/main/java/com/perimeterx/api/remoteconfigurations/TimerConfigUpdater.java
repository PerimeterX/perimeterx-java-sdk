package com.perimeterx.api.remoteconfigurations;

import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.configuration.PXDynamicConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by nitzangoldfeder on 19/06/2017.
 */
public class TimerConfigUpdater extends TimerTask {

    private Logger logger = LoggerFactory.getLogger(TimerConfigUpdater.class);

    private RemoteConfigurationManager configManager;
    private PXConfiguration pxConfiguration;

    public TimerConfigUpdater(RemoteConfigurationManager configManager, PXConfiguration pxConfiguration) {
        logger.debug("TimerConfigUpdater[init]");
        this.configManager = configManager;
        this.pxConfiguration = pxConfiguration;
    }

    @Override
    public void run() {
        // Fetch the configuration from server
        PXDynamicConfiguration dynamicConfig = configManager.getConfiguration();
        if (dynamicConfig != null){
            pxConfiguration.update(dynamicConfig);
        } else if (pxConfiguration.getChecksum() == null) {
            pxConfiguration.disableModule();
        }
    }

    public void schedule(int interval, int delay) {
        Timer timer = new Timer();
        timer.schedule(this, interval, delay);
    }


}
