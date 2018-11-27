package testutils;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.Properties;

/**
 * Prepare test by reading configuration file (resources/testconfig.properties)
 * Each test extends this class will get the configuration properties
 * <p>
 * Created by shikloshi on 17/07/2016.
 */
@Test
public abstract class ConfiguredTest {

    public static final String CONFIG_FILE_NAME = "/testconfig.properties";
    protected Properties configProperties;

    @BeforeClass
    public void setUp() throws Exception {
        configProperties = new Properties();
        InputStream resourceAsStream = ConfiguredTest.class.getResourceAsStream(CONFIG_FILE_NAME);
        if (resourceAsStream != null) {
            configProperties.load(resourceAsStream);
        }
        this.testSetup();
    }

    abstract protected void testSetup() throws Exception;

}
