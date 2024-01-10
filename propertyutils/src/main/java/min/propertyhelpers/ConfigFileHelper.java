package min.propertyhelpers;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class ConfigFileHelper {
    private static final Logger logger = Logger.getLogger(ConfigFileHelper.class.getName());

    public static void loadPropertiesFromConfigFile(Properties properties, String configFle) {
        try (final FileReader configFileReader = new FileReader(configFle)) {
            properties.load(configFileReader);
            logger.info("Successfully loaded config file: " + configFle);
        } catch (FileNotFoundException fileNotFoundException) {
            logger.warning("Unable to open config file. Trying to get resources from class loader");

            try (InputStream configFileStream =
                         ConfigFileHelper.class.getClassLoader().getResourceAsStream(configFle)) {
                if (configFileStream == null) {
                    logger.warning("Unalbe to open config file using class loader.");
                } else {
                    properties.load(configFileStream);

                    logger.info("Successfully loaded config file: " + configFle);
                }
            } catch (IOException e) {
                logger.warning("Unable to read config file\n" + e.getMessage());
            }
        } catch (IOException e) {
            logger.warning("Unable to read config file\n" + e.getMessage());
        }
    }
}
