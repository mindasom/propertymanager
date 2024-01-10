package min.propertyhelpers;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class ConfigFileHelperTest {
    public static final String TEST_CONFIG_PROPERTIES_FILE = "testConfig.properties";

    @Test
    public void testLoadPropertiesFromConfigFile() {
        Properties properties = new Properties();
        ConfigFileHelper.loadPropertiesFromConfigFile(properties, TEST_CONFIG_PROPERTIES_FILE);

        assertEquals("properties", properties.getProperty("property.dir"));
        assertEquals("8068", properties.getProperty("server.port"));
    }
}