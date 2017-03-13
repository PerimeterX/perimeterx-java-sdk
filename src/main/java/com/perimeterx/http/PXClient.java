package com.perimeterx.http;

import com.perimeterx.models.activities.Activity;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.httpmodels.CaptchaRequest;
import com.perimeterx.models.httpmodels.CaptchaResponse;
import com.perimeterx.models.httpmodels.RiskRequest;
import com.perimeterx.models.httpmodels.RiskResponse;

import java.io.IOException;
import java.util.List;

/**
 * Interface for com.perimeterx.http request between PerimeterX Server and running Server
 * <p>
 * Created by Shikloshi on 03/07/2016.
 */
public interface PXClient {

    /**
     * Calling PX Server with Risk API call
     *
     * @param riskRequest - risk request to send
     * @return server response (in both failed or success) as object {@link RiskResponse}
     * @throws PXException
     * @throws IOException
     */
    RiskResponse riskApiCall(RiskRequest riskRequest) throws PXException, IOException;

    /**
     * Calling PX Server to report Activity
     *
     * @param activity - the activity we want to report
     * @throws PXException
     * @throws IOException
     */
    void sendActivity(Activity activity) throws PXException, IOException;

    /**
     * Calling PX Server to report Activity
     *
     * @param activities - the activites we want to report
     * @throws PXException
     * @throws IOException
     */
    void sendBatchActivities(List<Activity> activities) throws PXException, IOException;

    /**
     * Calling PX Server to validate user captcha
     *
     * @param captchaRequest - captcha request
     * @return response from server as object {@link CaptchaResponse}
     * @throws PXException
     * @throws IOException
     */
    CaptchaResponse sendCaptchaRequest(CaptchaRequest captchaRequest) throws PXException, IOException;
}
