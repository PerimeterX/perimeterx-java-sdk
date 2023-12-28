package com.perimeterx.http;

import com.perimeterx.api.PerimeterX;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.utils.logger.IPXLogger;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;

import java.io.Closeable;
import java.util.Timer;
import java.util.TimerTask;

public class TimerValidateRequestsQueue extends TimerTask implements Closeable {

    private static final IPXLogger logger = PerimeterX.globalLogger;

    private Timer timer = null;
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
        this.close();
        timer = new Timer();
        timer.schedule(this, 0, pxConfiguration.getValidateRequestQueueInterval());
    }

    @Override
    public void close() {
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
