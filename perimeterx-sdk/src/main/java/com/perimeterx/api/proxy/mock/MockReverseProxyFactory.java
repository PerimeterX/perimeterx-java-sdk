package com.perimeterx.api.proxy.mock;

import com.perimeterx.api.proxy.ReverseProxy;

public final class MockReverseProxyFactory {
    private MockReverseProxyFactory() {
    }

    public static ReverseProxy createNeverReverseProxy() {
        return NeverReverseProxy.getInstance();
    }
}
