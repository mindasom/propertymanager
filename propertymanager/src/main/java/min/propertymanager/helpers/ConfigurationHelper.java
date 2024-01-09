package min.propertymanager.helpers;

import min.propertymanager.PropertymanagerApplication;

import java.io.*;
import java.util.Properties;
import java.util.logging.Logger;

public class ConfigurationHelper {
    private static final Logger logger = Logger.getLogger(ConfigurationHelper.class.getName());

    public static final String CONFIG_PROPERTY_DIR = "property.dir";
    public static final String DEFAULT_PROPERTY_DIR = "properties";
    public static final String CONFIG_SERVER_PORT = "server.port";
    public static final String DEFAULT_SERVER_PORT = "8080";

    public static void loadPropertiesFromConfigFile(Properties properties, String configFle) {
        try (final FileReader configFileReader = new FileReader(configFle)) {
            properties.load(configFileReader);
            logger.info("Successfully loaded config file: " + configFle);
        } catch (FileNotFoundException fileNotFoundException) {
            logger.warning("Unable to open config file. Trying to get resources from class loader");

            try (InputStream configFileStream =
                         PropertymanagerApplication.class.getClassLoader().getResourceAsStream(configFle)) {
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

    public static String validatePropertyFilesDirectory(String propertyFilesDirectory) {
        File propertyFilesDirectoryFile = new File(propertyFilesDirectory);

        boolean useDefault = false;

        if (propertyFilesDirectoryFile.exists()) {
            boolean isDirectory = propertyFilesDirectoryFile.isDirectory();
            boolean canWrite = propertyFilesDirectoryFile.canWrite();

            if (isReusable(isDirectory, canWrite)) {
                return propertyFilesDirectory;
            }

            boolean isDefaultPropertyDirectory = ConfigurationHelper.DEFAULT_PROPERTY_DIR.equals(propertyFilesDirectory);

            if (!isRecreatable(canWrite, isDefaultPropertyDirectory)) {
                logger.severe("Unable to create property files directroy " +
                        propertyFilesDirectoryFile.getAbsolutePath());
                System.exit(1);
            }

            if (canWrite) {
                boolean isDeleted = propertyFilesDirectoryFile.delete();

                if (!isDeleted) {
                    if (!isDefaultPropertyDirectory) {
                        logger.severe("Unable to create property files directroy " +
                                propertyFilesDirectoryFile.getAbsolutePath());
                        System.exit(1);
                    } else {
                        useDefault = true;
                    }
                }
            } else if (!isDefaultPropertyDirectory) {
                useDefault = true;
            }
        }

        String directoryName = propertyFilesDirectory;

        if (useDefault) {
            directoryName = ConfigurationHelper.DEFAULT_PROPERTY_DIR;
        }

        return directoryName;
    }

    static boolean isReusable(boolean isDrectory, boolean canWrite) {
        return isDrectory && canWrite;
    }

    static boolean isRecreatable(boolean canWrite, boolean isDefaultPropertyDirectory) {
        return canWrite || !isDefaultPropertyDirectory;
    }

    public static boolean createPropertyFilesDirectory(String propertyFilesDirectoryName) {
        File newPropertyFilesDirectoryFile = new File(propertyFilesDirectoryName);

        if (newPropertyFilesDirectoryFile.exists()) {
            return false;
        }

        boolean created = newPropertyFilesDirectoryFile.mkdir();

        if (created) {
            logger.info("Created property files directory " + newPropertyFilesDirectoryFile.getAbsolutePath());
        } else {
            logger.severe("Unable to create property files directory " + newPropertyFilesDirectoryFile.getAbsolutePath());
            System.exit(1);
        }

        return created;
    }
}
