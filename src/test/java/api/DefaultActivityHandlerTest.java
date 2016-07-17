package api;

import com.perimeterx.api.PXConfiguration;
import com.perimeterx.api.activities.DefaultActivityHandler;
import com.perimeterx.http.PXClient;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.exceptions.PXException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import testutils.TestObjectUtils;

/**
 * Testing {@link DefaultActivityHandler}
 * <p>
 * Created by shikloshi on 17/07/2016.
 */
@Test
public class DefaultActivityHandlerTest {

    private DefaultActivityHandler activityHandler;

    @BeforeMethod
    public void setUp() {
        PXConfiguration config = TestObjectUtils.generateConfiguration();
        PXClient pxClient = TestObjectUtils.blockingPXClient(config.getBlockingScore());
        this.activityHandler = new DefaultActivityHandler(pxClient, config);
    }

    @Test
    public void testHandleBlockActivity() {
        boolean thrown = false;
        MockHttpServletRequest request = new MockHttpServletRequest();
        PXContext context = new PXContext(request);
        try {
            activityHandler.handleBlockActivity(context);
        } catch (PXException e) {
            thrown = true;
        }
        Assert.isTrue(!thrown);
    }

    @Test
    public void testHandlePageRequestedActivity() throws Exception {
        boolean thrown = false;
        MockHttpServletRequest request = new MockHttpServletRequest();
        PXContext context = new PXContext(request);
        try {
            activityHandler.handlePageRequestedActivity(context);
        } catch (PXException e) {
            thrown = true;
        }
        Assert.isTrue(!thrown);
    }
}
