package testutils;

import com.perimeterx.api.PerimeterX;
import com.perimeterx.api.activities.ActivityHandler;
import com.perimeterx.api.activities.DefaultActivityHandler;
import com.perimeterx.http.PXClient;
import com.perimeterx.internals.PXS2SValidator;
import com.perimeterx.models.configuration.ModuleMode;
import com.perimeterx.models.configuration.PXConfiguration;
import com.perimeterx.utils.Constants;

import java.lang.reflect.Field;

/**
 * Utility to create objects needed for testing
 * <p>
 * Created by shikloshi on 17/07/2016.
 */
public class TestObjectUtils {

    public static Object[][] cookieSecretsDataProvider(PXConfiguration configuration) {
        return configuration.getCookieKeys()
                .stream()
                .map(key -> new Object[]{key})
                .toArray(Object[][]::new);

    }

    public static PXClient blockingPXClient(int minScoreToBlock) {
        int scoreToReturn = minScoreToBlock;
        return new PXClientMock(scoreToReturn, Constants.CAPTCHA_SUCCESS_CODE);
    }

    public static PXClient nonBlockingPXClient(int minScoreToBlock) {
        int scoreToReturn = minScoreToBlock - 1;
        return new PXClientMock(scoreToReturn, Constants.CAPTCHA_SUCCESS_CODE);
    }

    public static PXConfiguration generateConfiguration() {
        return PXConfiguration.builder()
                .appId("appId")
                .authToken("token")
                .cookieKey("cookieKey")
                .cookieKey("cookieKey2")
                .cookieKey("cookieKey3")
                .loggerAuthToken("logger_token_123")
                .moduleMode(ModuleMode.BLOCKING)
                .remoteConfigurationEnabled(false)
                .blockingScore(30)
                .build();
    }

    public static PerimeterX testablePerimeterXObject(PXConfiguration configuration, PXClient client) throws Exception {
        PerimeterX instance = new PerimeterX(configuration);
        PXS2SValidator validator = new PXS2SValidator(client, configuration);
        ActivityHandler activityHandler = new DefaultActivityHandler(client, configuration);
        Field validatorField = PerimeterX.class.getDeclaredField("serverValidator");
        validatorField.setAccessible(true);
        Field activityHandlerField = PerimeterX.class.getDeclaredField("activityHandler");
        activityHandlerField.setAccessible(true);
        validatorField.set(instance, validator);
        activityHandlerField.set(instance, activityHandler);
        return instance;
    }
}
