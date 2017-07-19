package com.perimeterx.api.remoteconfigurations;

import com.perimeterx.models.configuration.PXDynamicConfiguration;

/**
 * This interface will be used to retrieve new values for the PXConfiguration
 */
public interface RemoteConfigurationManager {
    /**
     * Returns new values to update PXConfiguration settings
     * @return PXDynamicConfiguration
     */
    PXDynamicConfiguration getConfiguration();
}
