package min.propertymanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PropertymanagerApplicationTests {

	@Test
	public void testLoadPropertiesFromConfigFileArgument() {
		Properties properties = PropertymanagerApplication.getPropertiesFromConfigFileArg(new String[] {"testConfig.properties"});

		assertEquals("properties", properties.getProperty(PropertymanagerApplication.CONFIG_PROPERTY_DIR));
		assertEquals("8068", properties.getProperty(PropertymanagerApplication.CONFIG_SERVER_PORT));
	}

}
