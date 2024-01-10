package min.propertymanager.helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import java.util.logging.Logger;

public class PropertyFileHandler {
    private static Logger logger = Logger.getLogger(PropertyFileHandler.class.getName());
    private Path propertyFilePath;
    private String propertyFileAbsolutePath;

    public PropertyFileHandler(Path propertyFilePath) {
        this.propertyFilePath = propertyFilePath;
        propertyFileAbsolutePath = propertyFilePath.toAbsolutePath().toString();
    }

    public void loadExistingPropertiesTo(Properties properties) {
        if (Files.exists(propertyFilePath)) {
            if (Files.isDirectory(propertyFilePath) || !Files.isReadable(propertyFilePath)) {
                logger.warning("Unable to read existing property file " + propertyFileAbsolutePath);
            } else {
                try {
                    properties.load(Files.newBufferedReader(propertyFilePath));
                } catch (IOException e) {
                    logger.warning("Unable to load existing property file " + propertyFileAbsolutePath + ", " + e.getMessage());
                }
            }
        }
    }

    public void deleteExistingPropertyFile() {
        try {
            Files.deleteIfExists(propertyFilePath);
        } catch (IOException e) {
            logger.warning("Unable to delete existing property file " + propertyFileAbsolutePath + ", " + e.getMessage());
        }
    }

    public void writeProperties(Properties properties) {
        try {
            if (!Files.exists(propertyFilePath.getParent())) {
                Files.createDirectories(propertyFilePath.getParent());
            }

            properties.store(Files.newBufferedWriter(propertyFilePath, StandardOpenOption.CREATE_NEW, StandardOpenOption.TRUNCATE_EXISTING), null);
        } catch (IOException e) {
            logger.warning("Unable to create property file " + propertyFileAbsolutePath + ", " + e.getMessage());
        }
    }
}
