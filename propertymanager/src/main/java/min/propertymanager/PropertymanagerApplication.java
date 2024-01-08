package min.propertymanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Logger;

@SpringBootApplication
public class PropertymanagerApplication {
	public static final String CONFIG_PROPERTY_DIR = "property.dir";
	public static final String CONFIG_SERVER_PORT = "server.port";
	public static final String DEFAULT_SERVER_PORT = "8080";

	private static final Logger logger = Logger.getLogger(PropertymanagerApplication.class.getName());

	public static void main(String[] args) {
		Properties properties = getPropertiesFromConfigFileArg(args);

		SpringApplication propertyManagerApp = new SpringApplication(PropertymanagerApplication.class);

		propertyManagerApp.setDefaultProperties(
				Collections.singletonMap(CONFIG_SERVER_PORT,
						properties.getProperty(CONFIG_SERVER_PORT, DEFAULT_SERVER_PORT)));

		propertyManagerApp.run(args);
	}

	static Properties getPropertiesFromConfigFileArg(String[] args) {
		final Properties properties = new Properties();

		if (args == null || args.length == 0) {
			return properties;
		}

		final String configFilePath = args[0];

		try (final FileReader configFileReader = new FileReader(configFilePath)) {
			properties.load(configFileReader);
			logger.info("Successfully loaded config file: " + configFilePath);

			return properties;
		} catch (FileNotFoundException fileNotFoundException) {
			logger.warning("Unable to open config file. Trying to get resources from class loader");

			try (InputStream configFileStream =
						 PropertymanagerApplication.class.getClassLoader().getResourceAsStream(configFilePath)) {
				if (configFileStream == null) {
					logger.warning("Unalbe to open config file using class loader.");
				} else {
					properties.load(configFileStream);

					logger.info("Successfully loaded config file: " + configFilePath);

					return properties;
				}
			} catch (IOException e) {
				logger.warning("Unable to read config file\n" + e.getMessage());
			}
		} catch (IOException e) {
			logger.warning("Unable to read config file\n" + e.getMessage());
		}

		return properties;
	}
}
