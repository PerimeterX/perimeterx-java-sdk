package com.perimeterx.api;

import com.perimeterx.api.providers.IPByHeaderProvider;
import com.perimeterx.api.providers.RemoteAddressIPProvider;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Testing {@link com.perimeterx.api.providers.IPProvider}
 * <p>
 * Created by shikloshi on 12/07/2016.
 */
@Test
public class IPProviderTest {

    private static final String IP_HEADER = "this-is-where-my-ip-is";
    private HttpServletRequest request;

    public void setUp() throws Exception {
        final Map<String, String> headers = new HashMap<>();
        headers.put(IP_HEADER, "127.0.0.1");
        this.request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeaderNames()).thenReturn(Collections.enumeration(headers.keySet()));
        Mockito.doAnswer(new Answer() {
            @Override
            public String answer(final InvocationOnMock invocationOnMock) throws Throwable {
                Object[] args = invocationOnMock.getArguments();
                final String name = (String)args[0];
                return headers.get(name);
            }
        }).when(request).getHeader(IP_HEADER);
        Mockito.when(request.getRemoteAddr()).thenReturn("81.82.81.82");
    }

    @Test
    public void testGetRequestIPByHeader() throws Exception {
        IPByHeaderProvider ipProvider = new IPByHeaderProvider(IP_HEADER);
        String ipHeaderValue = request.getHeader(IP_HEADER);
        Assert.assertEquals(ipHeaderValue, ipProvider.getRequestIP(request));
    }

    @Test
    public void testGetRequestIPByRemoteAddress() {
        RemoteAddressIPProvider ipProvider = new RemoteAddressIPProvider();
        Assert.assertEquals(ipProvider.getRequestIP(request), "81.82.81.82");
    }
}
