package com.perimeterx.api.blockhandler;

import com.perimeterx.models.PXContext;
import com.perimeterx.models.exceptions.PXException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

/**
 * Default captcha block handler which display captcha page on block
 * <p>
 * Created by shikloshi on 07/07/2016.
 */
public class CaptchaBlockHandler implements BlockHandler {

    @Override
    public void handleBlocking(PXContext context, HttpServletResponseWrapper responseWrapper) throws PXException {
        String page = this.getCaptchaHtml(context);
        responseWrapper.setStatus(HttpServletResponse.SC_FORBIDDEN);
        responseWrapper.setContentType("text/html");
        try {
            responseWrapper.getWriter().print(page);
        } catch (IOException e) {
            throw new PXException(e);
        }
    }

    private String getCaptchaHtml(PXContext context) {
        return "<html lang=\"en\">\n" +
                "   <head>\n" +
                "      <link type=\"text/css\" rel=\"stylesheet\" media=\"screen, print\" href=\"//fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800\">\n" +
                "      <meta charset=\"UTF-8\">\n" +
                "      <title>Access to This Page Has Been Blocked</title>\n" +
                "      <style> p { width: 60%; margin: 0 auto; font-size: 35px; } body { background-color: #a2a2a2; font-family: \"Open Sans\"; margin: 5%; } img { width: 180px; } a { color: #2020B1; text-decoration: blink; } a:hover { color: #2b60c6; } </style>\n" +
                "      <script src=\"https://www.google.com/recaptcha/api.js\"></script> " +
                "      <script> " +
                "           window.px_vid = '" + context.getVid() + "';\n" +
                "           function handleCaptcha(response) { \n" +
                "               var name = '_pxCaptcha';\n" +
                "               var expiryUtc = new Date(Date.now() + 1000 * 10).toUTCString();\n" +
                "               var cookieParts = [name, '=', response + ':' + window.px_vid, '; expires=', expiryUtc, '; path=/'];\n" +
                "               document.cookie = cookieParts.join('');\n" +
                "               location.reload();\n" +
                "           }\n" +
                "   </script> \n" +
                "   </head>\n" +
                "   <body cz-shortcut-listen=\"true\">\n" +
                "      <div><img src=\"http://storage.googleapis.com/instapage-thumbnails/035ca0ab/e94de863/1460594818-1523851-467x110-perimeterx.png\"> </div>\n" +
                "      <span style=\"color: white; font-size: 34px;\">Access to This Page Has Been Blocked</span> \n" +
                "      <div style=\"font-size: 24px;color: #000042;\">\n" +
                "         <br> Access to '" + context.getFullUrl() + "' is blocked according to the site security policy.<br> Your browsing behaviour fingerprinting made us think you may be a bot. <br> <br> This may happen as a result of the following: \n" +
                "         <ul>\n" +
                "            <li>JavaScript is disabled or not running properly.</li>\n" +
                "            <li>Your browsing behaviour fingerprinting are not likely to be a regular user.</li>\n" +
                "         </ul>\n" +
                "         To read more about the bot defender solution: <a href=\"https://www.perimeterx.com/bot-defender\">https://www.perimeterx.com/bot-defender</a><br> If you think the blocking was done by mistake, contact the site administrator. <br> \n" +
                "         <div class=\"g-recaptcha\" data-sitekey=\"6Lcj-R8TAAAAABs3FrRPuQhLMbp5QrHsHufzLf7b\" data-callback=\"handleCaptcha\" data-theme=\"dark\"></div>\n" +
                "         <br><span style=\"font-size: 20px;\">Block Reference: <span style=\"color: #525151;\">#' " + context.getUuid() + "'</span></span> \n" +
                "         <script type=\"text/javascript\">\r\n    (function(){\r\n        window._pxAppId ='" + context.getAppId() + "';\r\n        // Custom parameters\r\n        // window._pxParam1 = \"<param1>\";\r\n        var p = document.getElementsByTagName('script')[0],\r\n            s = document.createElement('script');\r\n        s.async = 1;\r\n        s.src = '//client.perimeterx.net/" + context.getAppId() + "/main.min.js';\r\n        p.parentNode.insertBefore(s,p);\r\n    }());\r\n</script>\r\n<div style=\"position:fixed; top:0; left:0;\" width=\"1\" height=\"1\">\r\n    <img src=\"//collector-" + context.getAppId() + ".perimeterx.net/api/v1/collector/pxPixel.gif?appId=" + context.getAppId() + "\">\r\n    <!-- With custom parameters: -->\r\n    <!--<img src=\"//collector-" + context.getAppId() + ".perimeterx.net/api/v1/collector/pxPixel.gif?appId=" + context.getAppId() + "&p1=VALUE&p2=VALUE2&p3=VALUE3\">-->\r\n</div>\r\n<noscript>\r\n    <div style=\"position:fixed; top:0; left:0;\" width=\"1\" height=\"1\">\r\n        <img src=\"//collector-" + context.getAppId() + ".perimeterx.net/api/v1/collector/noScript.gif?appId=" + context.getAppId() + "\">\r\n    </div>\r\n</noscript>\r\n " +
                "      </div>\n" +
                "   </body>\n" +
                "</html>";
    }
}
