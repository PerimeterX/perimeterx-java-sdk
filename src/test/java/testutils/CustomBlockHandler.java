package testutils;

import com.perimeterx.api.blockhandler.DefaultBlockHandler;
import com.perimeterx.api.blockhandler.templates.TemplateFactory;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.utils.Constants;

import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomBlockHandler extends DefaultBlockHandler {

    @Override
    public void handleBlocking(PXContext context, PXConfiguration pxConfig, HttpServletResponseWrapper responseWrapper) throws PXException {
        Map<String, String> props = new HashMap<>();
        String filePrefix;
        String blockPageResponse;
        switch (context.getBlockAction()) {
            case RATE:
                filePrefix = Constants.RATELIMIT_TEMPLATE;
                blockPageResponse = getPage(props, filePrefix);
                break;

            case CHALLENGE:
                String actionData = context.getBlockActionData();
                if (actionData != null) {
                    blockPageResponse = actionData;
                    break;
                }
            default:
                filePrefix = Constants.CAPTCHA_BLOCK_TEMPLATE;
                props = TemplateFactory.getProps(context, pxConfig);
                blockPageResponse = getPage(props, filePrefix);
        }
        try {
            sendMessage(blockPageResponse, responseWrapper, context, pxConfig);
            context.setMonitoredRequest(true); // This row is an example of how a client is used the custom class to modify the context state
        } catch (IOException e) {
            throw new PXException(e);
        }
    }
}
