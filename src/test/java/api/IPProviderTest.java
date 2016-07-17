package api;

import com.perimeterx.api.ip.IPByHeaderProvider;
import com.perimeterx.api.ip.RemoteAddressIPProvider;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Testing {@link com.perimeterx.api.ip.IPProvider}
 * <p>
 * Created by shikloshi on 12/07/2016.
 */
@Test
public class IPProviderTest {

    public static final String IP_HEADER = "this-is-where-my-ip-is";
    private HttpServletRequest request;

    public void setUp() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put(IP_HEADER, "127.0.0.1");
        this.request = Mockito.mock(HttpServletRequest.class);
        Iterator<String> iterator = headers.keySet().iterator();
        Enumeration headerNames = new Enumeration() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public Object nextElement() {
                return iterator.next();
            }
        };
        Mockito.when(request.getHeaderNames()).thenReturn(headerNames);
        Mockito.doAnswer(invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            return headers.get(args[0]);
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
