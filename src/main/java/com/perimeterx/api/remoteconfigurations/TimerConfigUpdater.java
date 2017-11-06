package com.perimeterx.api.remoteconfigurations;

import com.perimeterx.api.activities.ActivityHandler;
import com.perimeterx.http.PXClient;
import com.perimeterx.models.activities.EnforcerTelemetry;
import com.perimeterx.models.activities.EnforcerTelemetryActivityDetails;
import com.perimeterx.models.activities.UpdateReason;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.configuration.PXDynamicConfiguration;
import com.perimeterx.models.exceptions.PXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class TimerConfigUpdater extends TimerTask {

    private static final Logger logger = LoggerFactory.getLogger(TimerConfigUpdater.class);

    private RemoteConfigurationManager configManager;
    private PXConfiguration pxConfiguration;
    private ActivityHandler activityHandler;

    public TimerConfigUpdater(RemoteConfigurationManager configManager, PXConfiguration pxConfiguration, ActivityHandler activityHandler) {
        logger.debug("TimerConfigUpdater[init]");
        this.configManager = configManager;
        this.pxConfiguration = pxConfiguration;
        this.activityHandler = activityHandler;
    }

    @Override
    public void run() {
        // Fetch the configuration from server
        PXDynamicConfiguration dynamicConfig = configManager.getConfiguration();
        if (dynamicConfig != null) {
            configManager.updateConfiguration(dynamicConfig);
            try {
                activityHandler.handleEnforcerTelemetryActivity(pxConfiguration, UpdateReason.REMOTE_CONFIG);
            } catch (PXException e) {
                logger.error("Failed to report telemetry, {}", e.getMessage());
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
