package testutils;

import com.perimeterx.http.PXClient;
import com.perimeterx.models.activities.Activity;
import com.perimeterx.models.exceptions.PXException;
import com.perimeterx.models.httpmodels.CaptchaRequest;
import com.perimeterx.models.httpmodels.CaptchaResponse;
import com.perimeterx.models.httpmodels.RiskRequest;
import com.perimeterx.models.httpmodels.RiskResponse;
import com.perimeterx.models.risk.Scores;

import java.io.IOException;
import java.util.List;

/**
 * Mocking PXClient that is usually create an http request to PX servers
 * Can be configured to block or not.
 * <p>
 * Created by shikloshi on 12/07/2016.
 */
public class PXClientMock implements PXClient {

    private final int score;
    private final int captchaReturnStatus;

    public PXClientMock(int scoreToReturn, int captchaReturnStatus) {
        this.score = scoreToReturn;
        this.captchaReturnStatus = captchaReturnStatus;
    }

    @Override
    public RiskResponse riskApiCall(RiskRequest riskRequest) throws PXException, IOException {
        return new RiskResponse("uuid", 0, this.score, "c");
    }

    @Override
    public void sendActivity(Activity activity) throws PXException, IOException {
        // noop
    }

    @Override
    public void sendBatchActivities(List<Activity> activities) throws PXException, IOException {
        // noop
    }

    @Override
    public CaptchaResponse sendCaptchaRequest(CaptchaRequest captchaRequest) throws PXException, IOException {
        return new CaptchaResponse(captchaReturnStatus, "1", "vid", "cid");
    }
}
