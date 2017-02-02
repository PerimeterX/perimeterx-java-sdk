package testutils;

import com.perimeterx.api.PXConfiguration;
import com.perimeterx.api.PerimeterX;
import com.perimeterx.api.activities.ActivityHandler;
import com.perimeterx.api.activities.DefaultActivityHandler;
import com.perimeterx.http.PXClient;
import com.perimeterx.internals.PXCaptchaValidator;
import com.perimeterx.internals.PXS2SValidator;
import com.perimeterx.utils.Constants;

import java.lang.reflect.Field;

/**
 * Utility to create objects needed for testing
 * <p>
 * Created by shikloshi on 17/07/2016.
 */
public class TestObjectUtils {

    public static PXClient blockingPXClient(int minScoreToBlock) {
        int scoreToReturn = minScoreToBlock + 1;
        return new PXClientMock(scoreToReturn, Constants.CAPTCHA_SUCCESS_CODE);
    }

    public static PXClient nonBlockingPXClient(int minScoreToBlock) {
        int scoreToReturn = minScoreToBlock - 1;
        return new PXClientMock(scoreToReturn, Constants.CAPTCHA_SUCCESS_CODE);
    }

    public static PXClient verifiedCaptchaClient() {
        return new PXClientMock(0, Constants.CAPTCHA_SUCCESS_CODE);
    }

    public static PXClient notVerifiedCaptchaClient() {
        return new PXClientMock(0, Constants.CAPTCHA_FAILED_CODE);
    }

    public static PXConfiguration generateConfiguration() {
        return new PXConfiguration.Builder()
                .appId("appId")
                .authToken("token")
                .cookieKey("cookieKey")
                .blockingScore(30)
                .build();
    }

    public static PerimeterX testablePerimeterXObject(PXConfiguration configuration, PXClient client) throws Exception {
        PerimeterX instance = new PerimeterX(configuration);
        PXS2SValidator validator = new PXS2SValidator(client);
        PXCaptchaValidator captchaValidator = new PXCaptchaValidator(client);
        ActivityHandler activityHandler = new DefaultActivityHandler(client, configuration);
        Field validatorField = PerimeterX.class.getDeclaredField("serverValidator");
        validatorField.setAccessible(true);
        Field activityHandlerField = PerimeterX.class.getDeclaredField("activityHandler");
        activityHandlerField.setAccessible(true);
        Field captchaValidatorField = PerimeterX.class.getDeclaredField("captchaValidator");
        captchaValidatorField.setAccessible(true);
        validatorField.set(instance, validator);
        activityHandlerField.set(instance, activityHandler);
        captchaValidatorField.set(instance, captchaValidator);
        return instance;
    }
}
