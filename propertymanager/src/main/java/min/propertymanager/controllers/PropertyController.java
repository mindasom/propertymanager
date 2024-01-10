package min.propertymanager.controllers;

import min.propertymanager.helpers.PropertyFileHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.nio.file.FileSystems;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

@RestController
@RequestMapping("/properties")
public class PropertyController {
    private static Logger logger = Logger.getLogger(PropertyController.class.getName());
    @Value("${property.dir}")
    private String propertyFilesPath;

    @PostMapping(path="/send/{filename}")
    public String sendProperties(@RequestBody Map<String, String> propertiesMap,
                                 @PathVariable String filename) {
        logger.info("Received properties " + filename);

        if (propertiesMap.isEmpty()) {
            return "0";
        }

        return sendProperties(propertiesMap, new PropertyFileHandler(FileSystems.getDefault().getPath(propertyFilesPath, filename)));
    }

    String sendProperties(Map<String, String> newPropertiesMap,
                          PropertyFileHandler propertyFileHandler) {
        Properties properties = new Properties();

        propertyFileHandler.loadExistingPropertiesTo(properties);
        propertyFileHandler.deleteExistingPropertyFile();

        mergeNewProperties(properties, newPropertiesMap);

        propertyFileHandler.writeProperties(properties);

        return Integer.toString(newPropertiesMap.size());
    }

    void mergeNewProperties(Properties existingProperties, Map<String, String> newPropertiesMap) {
        for(String key : newPropertiesMap.keySet()) {
            existingProperties.put(key, newPropertiesMap.get(key));
        }
    }

    void setPropertyFilesPath(String propertyFilesPath) {
        this.propertyFilesPath = propertyFilesPath;
    }
}
