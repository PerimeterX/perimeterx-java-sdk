package px;

import com.perimeterx.api.blockhandler.BlockHandler;
import com.perimeterx.models.PXContext;
import com.perimeterx.models.exceptions.PXException;

import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

/**
 * This will implement the BlockHandler interface which can later be set in PerimeterX object initializtion
 */
public class CustomBlockHandler implements BlockHandler {

    /**
     * Simple block handler which set status code to be 403 add one header with score and write to page "You are blocked"
     */
    @Override
    public void handleBlocking(PXContext context, HttpServletResponseWrapper responseWrapper) throws PXException {
        responseWrapper.setHeader("PX-Blocking-Score", String.valueOf(context.getScore()));
        try {
            responseWrapper.setStatus(403);
            responseWrapper.getWriter().print("You are blocked");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
