package com.perimeterx.api.additionals2s.credentialsIntelligence.protocol;

import com.perimeterx.api.additionals2s.credentialsIntelligence.CIProtocol;
import com.perimeterx.models.exceptions.PXException;

import java.util.Arrays;

public class CredentialsIntelligenceProtocolFactory {
    public static CredentialsIntelligenceProtocol create(CIProtocol ciProtocol) throws PXException {
        switch (ciProtocol) {
            case V1:
                return new V1CIProtocol();
            case V2:
                return new V2CIProtocol();
            case MULTI_STEP_SSO:
                return new MultiStepSSOCIProtocol();
            default:
                throw new PXException(String.format("Unknown CI protocol version %s , acceptable versions are %s", ciProtocol, Arrays.toString(CIProtocol.values())));
        }
    }
}
