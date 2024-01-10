package min.propertymanager;

import min.propertyhelpers.ConfigFileHelper;
import min.propertymanager.helpers.ConfigurationHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;
import java.util.Properties;
import java.util.logging.Logger;

@SpringBootApplication
public class PropertymanagerApplication {
	private static final Logger logger = Logger.getLogger(PropertymanagerApplication.class.getName());

	public static void main(String[] args) {
		Properties properties = new Properties();

		if (args != null && args.length > 0) {
			ConfigFileHelper.loadPropertiesFromConfigFile(properties, args[0]);
		}

		SpringApplication propertyManagerApp = new SpringApplication(PropertymanagerApplication.class);

		setServerPort(properties, propertyManagerApp);
		setPropertyFilesDirectory(properties, propertyManagerApp);

		propertyManagerApp.run(args);
	}

	static void setServerPort(Properties properties, SpringApplication propertyManagerApp) {
		propertyManagerApp.setDefaultProperties(
				Collections.singletonMap(ConfigurationHelper.CONFIG_SERVER_PORT,
						properties.getProperty(ConfigurationHelper.CONFIG_SERVER_PORT, ConfigurationHelper.DEFAULT_SERVER_PORT)));
	}

	static void setPropertyFilesDirectory(Properties properties, SpringApplication propertyManagerApp) {
		String propertyFilesDirectory = properties.getProperty(ConfigurationHelper.CONFIG_PROPERTY_DIR, ConfigurationHelper.DEFAULT_PROPERTY_DIR);

		String validPropertyFilesDirectoryName =
				ConfigurationHelper.validatePropertyFilesDirectory(propertyFilesDirectory);
		ConfigurationHelper.createPropertyFilesDirectory(validPropertyFilesDirectoryName);

		propertyManagerApp.setDefaultProperties(Collections.singletonMap(ConfigurationHelper.CONFIG_PROPERTY_DIR, propertyFilesDirectory));
	}
}
