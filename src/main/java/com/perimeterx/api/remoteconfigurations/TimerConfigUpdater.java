package com.perimeterx.api.remoteconfigurations;

import com.perimeterx.api.PerimeterX;
import com.perimeterx.http.PXClient;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.utils.Constants;
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

    public TimerConfigUpdater(RemoteConfigurationManager configManager) {
        logger.debug("TimerConfigUpdater[init]");
        this.configManager = configManager;
    }

    @Override
    public void run() {
        // Fetch the configuration from server
        configManager.getConfiguration();
    }

    public void schedule(int interval, int delay) {
        Timer timer = new Timer();
        timer.schedule(this, interval, delay);
    }
}
