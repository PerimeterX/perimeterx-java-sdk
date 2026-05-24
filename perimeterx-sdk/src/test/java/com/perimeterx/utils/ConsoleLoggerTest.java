package com.perimeterx.utils;

import com.perimeterx.api.activities.DefaultActivityHandler;
import com.perimeterx.api.providers.DefaultHostnameProvider;
import com.perimeterx.api.providers.HostnameProvider;
import com.perimeterx.api.providers.RemoteAddressIPProvider;
import com.perimeterx.http.PXClient;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.utils.logger.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.mockito.ArgumentCaptor;
import junit.framework.Assert;
import testutils.PXClientMock;
import testutils.TestObjectUtils;

import java.io.PrintStream;
import java.util.List;

import static org.mockito.Mockito.*;

public class ConsoleLoggerTest {

    private PXConfiguration config;
    private HostnameProvider hostnameProvider;
    private RemoteAddressIPProvider ipProvider;
    private MockHttpServletRequest request;

    @BeforeMethod
    public void setUp() {
        request = new MockHttpServletRequest();
        ipProvider = new RemoteAddressIPProvider();
        hostnameProvider = new DefaultHostnameProvider();
        config = spy(PXConfiguration.builder()
                .cookieKey("COOKIE_KEY_STRING_MOBILE")
                .appId("APP_ID")
                .authToken("AUTH_TOKEN")
                .build());
    }

    @Test
    public void testLoggingWithProperSeverity_printLog() {
        PrintStream mockStream = mock(PrintStream.class);
        System.setOut(mockStream); // Redirecting System.out to a mock stream

        ConsoleLogger logger = new ConsoleLogger(LoggerSeverity.DEBUG, false);
        logger.debug("Test message");

        ArgumentCaptor<StringBuilder> captor = ArgumentCaptor.forClass(StringBuilder.class);
        verify(mockStream).println(captor.capture());

        String loggedMessage = captor.getValue().toString();
        Assert.assertTrue(loggedMessage.contains("[PerimeterX - DEBUG] Test message"));
    }

    @Test
    public void testLoggingWithProperSeverity_skipLog() {
        PrintStream mockStream = mock(PrintStream.class);
        System.setOut(mockStream); // Redirecting System.out to a mock stream

        ConsoleLogger logger = new ConsoleLogger(LoggerSeverity.ERROR, false);
        logger.debug("Test message");

        ArgumentCaptor<StringBuilder> captor = ArgumentCaptor.forClass(StringBuilder.class);
        verify(mockStream,never()).println(captor.capture());
    }

    @Test
    public void testMemoryLoggingIsEnabled() {
        ConsoleLogger logger = new ConsoleLogger(LoggerSeverity.DEBUG, true);
        logger.debug("Test debug message");
        Assert.assertFalse(logger.isMemoryEmpty());
    }

    @Test
    public void testMemoryLoggingIsDisabled() {
        ConsoleLogger logger = new ConsoleLogger(LoggerSeverity.DEBUG, false);
        logger.debug("Test debug message");
        Assert.assertTrue(logger.isMemoryEmpty());
    }
}
