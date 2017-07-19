package com.perimeterx.api.remoteconfigurations;

import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.configuration.PXDynamicConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        // Fetch the configuration from server
        PXDynamicConfiguration dynamicConfig = configManager.getConfiguration();
        if (dynamicConfig != null){
            pxConfiguration.update(dynamicConfig);
        } else if (pxConfiguration.getChecksum() == null) {
            pxConfiguration.disableModule();
        }
    }


    /**
     * Sets a new timer object and runs its execution method
     * @param interval
     * @param delay
     */
    public void schedule(int interval, int delay) {
        Timer timer = new Timer();
        timer.schedule(this, interval, delay);
    }


}
