package com.perimeterx.http.mock;

import com.perimeterx.api.PerimeterX;
import com.perimeterx.http.PXClient;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.activities.Activity;
import com.perimeterx.models.activities.EnforcerTelemetry;
import com.perimeterx.models.configuration.PXDynamicConfiguration;
import com.perimeterx.models.httpmodels.RiskResponse;
import com.perimeterx.utils.logger.IPXLogger;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.IOException;
import java.util.List;

@Builder
@AllArgsConstructor
public class MockPXClient implements PXClient {
    private static final IPXLogger logger = PerimeterX.globalLogger;
    protected RiskResponse riskResponse;
    protected PXDynamicConfiguration pxDynamicConfiguration;
    @Override
    public final RiskResponse riskApiCall(PXContext pxContext) {
        logger.debug("Mocking riskApiCall - {}", riskResponse);
        return riskResponse;
    }

    @Override
    public void sendActivity(Activity activity, PXContext context) {
        logger.debug("Mocking sendActivity");
    }

    @Override
    public void sendBatchActivities(List<Activity> activities, PXContext context) {
        logger.debug("Mocking sendBatchActivities");

    }

    public void sendLogs(String activities, PXContext context) throws IOException {
        logger.debug("Mocking sendLoggingServiceLogs");
    }

    @Override
    public final PXDynamicConfiguration getConfigurationFromServer() {
        logger.debug("Mocking sendBatchActivities - {}", pxDynamicConfiguration);
        return pxDynamicConfiguration;
    }

    @Override
    public void sendEnforcerTelemetry(EnforcerTelemetry enforcerTelemetry) {
        logger.debug("Mocking sendEnforcerTelemetry");
    }
}
