package filters;

import com.perimeterx.api.PXConfiguration;
import com.perimeterx.api.PerimeterX;
import com.perimeterx.models.exceptions.PXException;
import org.springframework.stereotype.Component;
import px.CustomBlockHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

/**
 * Filter implementation based on PerimeterX SDK
 */
@Component
public class PXFilter implements Filter {

    private PerimeterX enforcer;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Creating filter configuration
        PXConfiguration pxConf = new PXConfiguration.Builder()
                .appId("") // Your PerimeterX application ID
                .cookieKey("") // Should copy from RiskCookie section in https://console.perimeterx.com/#/app/policiesmgmt
                .captchaEnabled(false) // This will trigger captcha validation flow when blocking
                .blockingScore(50) // Any request getting higher score than this score will be displayed the blocking page
                .authToken("") // PX Server request auth token to be copied from Token section in https://console.perimeterx.com/#/app/applicationsmgmt
                .build();
        try {
            this.enforcer = PerimeterX.getInstance(pxConf);
            // This will set the blocking handler from the default one to the our custom block handler
            // note that when we enable captcha logic we must use a blocking handler that display the appropriate html page with captcha
            // for instance CaptchaBlockHandler that is included in the SDK
            this.enforcer.setBlockHandler(new CustomBlockHandler());
        } catch (Exception e) {
            // Could not init enforcer
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // This will apply PerimeterX SDK logic on each request going through this filter
        HttpServletRequest httpReq = (HttpServletRequest) servletRequest;
        HttpServletResponse httpRes = (HttpServletResponse) servletResponse;
        try {
            boolean verified = enforcer.pxVerify(httpReq, new HttpServletResponseWrapper(httpRes));
            if (verified) {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        } catch (PXException e) {
            // ignoring error for now and passing the request
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
    }
}
