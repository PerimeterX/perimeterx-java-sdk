package com.perimeterx.http;

import com.perimeterx.models.PXContext;
import com.perimeterx.models.activities.Activity;
import com.perimeterx.models.activities.EnforcerTelemetry;
import com.perimeterx.models.configuration.PXDynamicConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.httpmodels.RiskResponse;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * Interface for com.perimeterx.http request between PerimeterX Server and running Server
 * <p>
 * Created by Shikloshi on 03/07/2016.
 */
public interface PXClient extends Closeable {

    /**
     * Calling PX Server with Risk API call
     *
     * @param riskRequest - risk request to send
     * @return server response (in both failed or success) as object {@link RiskResponse}
     * @throws PXException
     * @throws IOException
     */
    RiskResponse riskApiCall(PXContext pxContext) throws IOException;

    /**
     * Calling PX Server to report Activity
     *
     * @param activity - the activity we want to report
     * @param context
     * @throws PXException
     * @throws IOException
     */
    void sendActivity(Activity activity, PXContext context) throws PXException, IOException;

    /**
     * Calling PX Server to report Activity
     *
     * @param activities - the activites we want to report
     * @param context
     * @throws PXException
     * @throws IOException
     */
    void sendBatchActivities(List<Activity> activities, PXContext context) throws PXException, IOException;

    /**
     * Calling remote configuration server and fetching the latest configuration values
     *
     * @return PXDynamicConfiguration
     * @throws IOException when trying to close the connection
     */
    PXDynamicConfiguration getConfigurationFromServer();

    /**
     * Calling PX Servers and reporting enforcer telemetry asynchronously
     *
     * @param enforcerTelemetry
     */
    void sendEnforcerTelemetry(EnforcerTelemetry enforcerTelemetry, PXContext context) throws IOException;


    void sendLogs(String activities, PXContext context) throws IOException;

        @Override
    default void close() throws IOException {
        // by default do nothing
    }
}
