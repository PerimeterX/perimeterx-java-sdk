package com.perimeterx.http;

import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.utils.PXLogger;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;

import java.util.Timer;
import java.util.TimerTask;

public class TimerValidateRequestsQueue extends TimerTask {

    private static final PXLogger logger = PXLogger.getLogger(TimerValidateRequestsQueue.class);

    private PoolingNHttpClientConnectionManager nHttpConnectionManager;
    private PXConfiguration pxConfiguration;

    public TimerValidateRequestsQueue(PoolingNHttpClientConnectionManager nHttpConnectionManager, PXConfiguration pxConfiguration) {
        logger.debug("TimerValidateRequestsQueue[init]");
        this.nHttpConnectionManager = nHttpConnectionManager;
        this.pxConfiguration = pxConfiguration;
    }

    @Override
    public void run() {
        nHttpConnectionManager.validatePendingRequests();
    }

    /**
     * Sets a new timer object and runs its execution method
     */
    public void schedule() {
        Timer timer = new Timer();
        timer.schedule(this, 0, pxConfiguration.getValidateRequestQueueInterval());
    }
}
