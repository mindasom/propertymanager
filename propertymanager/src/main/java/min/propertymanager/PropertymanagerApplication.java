package min.propertymanager;

import min.propertyhelpers.ConfigFileHelper;
import min.propertymanager.helpers.ConfigurationHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Logger;

@SpringBootApplication
public class PropertymanagerApplication {
	private static final Logger logger = Logger.getLogger(PropertymanagerApplication.class.getName());

	public static void main(String[] args) {
		Properties properties = new Properties();

		if (args != null && args.length > 0) {
			logger.info("Command line arguments " + Arrays.toString(args));
			ConfigFileHelper.loadPropertiesFromConfigFile(properties, args[0]);
		}

		SpringApplication propertyManagerApp = new SpringApplication(PropertymanagerApplication.class);

		String[] newArgs = Arrays.copyOf(args, args.length);

		setServerPort(properties, propertyManagerApp, newArgs);
		setPropertyFilesDirectory(properties, propertyManagerApp);

		propertyManagerApp.run(newArgs);
	}

	static void setServerPort(Properties properties, SpringApplication propertyManagerApp, String[] newArgs) {
		String serverPort = properties.getProperty(ConfigurationHelper.CONFIG_SERVER_PORT, ConfigurationHelper.DEFAULT_SERVER_PORT);

		propertyManagerApp.setDefaultProperties(
				Collections.singletonMap(ConfigurationHelper.CONFIG_SERVER_PORT, serverPort));

		newArgs[0] = "--server.port=" + serverPort;

		logger.info("Set server port " + serverPort);
	}

	static void setPropertyFilesDirectory(Properties properties, SpringApplication propertyManagerApp) {
		String propertyFilesDirectory = properties.getProperty(ConfigurationHelper.CONFIG_PROPERTY_DIR, ConfigurationHelper.DEFAULT_PROPERTY_DIR);

		String validPropertyFilesDirectoryName =
				ConfigurationHelper.validatePropertyFilesDirectory(propertyFilesDirectory);
		ConfigurationHelper.createPropertyFilesDirectory(validPropertyFilesDirectoryName);

		propertyManagerApp.setDefaultProperties(Collections.singletonMap(ConfigurationHelper.CONFIG_PROPERTY_DIR, propertyFilesDirectory));
	}
}
