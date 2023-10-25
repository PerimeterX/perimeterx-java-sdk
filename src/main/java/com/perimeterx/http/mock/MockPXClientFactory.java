package com.perimeterx.http.mock;

import com.perimeterx.http.PXClient;
import com.perimeterx.models.httpmodels.RiskResponse;

public final class MockPXClientFactory {
    private MockPXClientFactory() {
    }

    public static PXClient createPassAllPXClient() {
        return MockPXClient.builder()
                .riskResponse(new RiskResponse("uuid", 0, 0, "c", null, null, "", "", ""))
                .build();
    }

    public static PXClient createBlockAllPXClient() {
        return MockPXClient.builder()
                .riskResponse(new RiskResponse("uuid", 0, 100, "c", null, null, "", "", ""))
                .build();
    }
}
