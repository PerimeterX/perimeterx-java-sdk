package com.perimeterx.http.mock;

import com.perimeterx.http.PXClient;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.activities.Activity;
import com.perimeterx.models.activities.EnforcerTelemetry;
import com.perimeterx.models.configuration.PXDynamicConfiguration;
import com.perimeterx.models.httpmodels.RiskResponse;
import com.perimeterx.utils.PXLogger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;

@Builder
@AllArgsConstructor
public class MockPXClient implements PXClient {
    private static final PXLogger logger = PXLogger.getLogger(MockPXClient.class);
    protected RiskResponse riskResponse;
    protected PXDynamicConfiguration pxDynamicConfiguration;
    @Override
    public final RiskResponse riskApiCall(PXContext pxContext) {
        logger.debug("Mocking riskApiCall - {}", riskResponse);
        return riskResponse;
    }

    @Override
    public void sendActivity(Activity activity) {
        logger.debug("Mocking sendActivity");
    }

    @Override
    public void sendBatchActivities(List<Activity> activities) {
        logger.debug("Mocking sendBatchActivities");

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
