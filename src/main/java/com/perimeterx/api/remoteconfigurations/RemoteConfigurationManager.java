package com.perimeterx.api.remoteconfigurations;

import com.perimeterx.http.PXClient;
import com.perimeterx.models.configuration.PXDynamicConfiguration;

/**
 * Created by nitzangoldfeder on 19/06/2017.
 */
public interface RemoteConfigurationManager {
    PXDynamicConfiguration getConfiguration();
}
