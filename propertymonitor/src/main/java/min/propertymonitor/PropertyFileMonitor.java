package min.propertymonitor;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class PropertyFileMonitor implements Runnable {
    private static Logger logger = Logger.getLogger(PropertyFileMonitor.class.getName());
    private String file;
    private String keyFilter;
    private String serverUrl;

    private HttpClient httpClient;

    public PropertyFileMonitor(String file, String keyFilter, String serverUrl, HttpClient httpClient) {
        this.file = file;
        this.keyFilter = keyFilter;
        this.serverUrl = serverUrl;
        this.httpClient = httpClient;
    }

    @Override
    public void run() {
        Properties properties = readFile();
        Map<String, String> filteredProperties = filterProperty(properties);
        boolean sentProperties = sendToServer(filteredProperties);

        if (sentProperties) {
            delete();
        }
    }

    void delete() {
        try {
            boolean deleted = Files.deleteIfExists(FileSystems.getDefault().getPath(file));

            if (!deleted) {
                logger.warning("Unable to delete file " + file);
            }
        } catch (IOException e) {
            logger.warning("Unable to delete file " + file + " " + e.getMessage());
        }
    }

    boolean sendToServer(Map<String, String> properties) {
        String propertyFileName = new File(file).getName();

        String completeUrl = serverUrl;

        if (!serverUrl.endsWith("/")) {
            completeUrl = completeUrl + "/" + propertyFileName;
        } else {
            completeUrl = completeUrl + propertyFileName;
        }

        URI serverUri = URI.create(completeUrl);

        String propertiesInJson = new Gson().toJson(properties);

        HttpRequest httpRequest = HttpRequest.newBuilder(serverUri)
                .POST(HttpRequest.BodyPublishers.ofString(propertiesInJson))
                .header("Content-Type", "application/json")
                .build();

        try {
            HttpResponse httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() != 200) {
                logger.warning("Failed to send properties to the server");
                return false;
            }

            logger.info("sent " + propertyFileName);
            return true;
        } catch (IOException e) {
            logger.warning("Failed to send properties to the server");
        } catch (InterruptedException e) {
            logger.warning("Failed to send properties to the server");
        }

        return false;
    }

    Map<String, String> filterProperty(Properties properties) {
        Map<String, String> filteredProperties = new HashMap<>(properties.size());

        for (Object key : properties.keySet()) {
            String keyString = key.toString();
            if (Pattern.matches(keyFilter, keyString)) {
                filteredProperties.put(keyString, properties.getProperty(keyString));
            }
        }

        return filteredProperties;
    }

    Properties readFile() {
        Properties properties = new Properties();

        try (FileReader fileReader = new FileReader(file)) {
            properties.load(fileReader);
            return properties;
        } catch (FileNotFoundException e) {
            logger.warning("File " + file + " does not exist");
        } catch (IOException e) {
            logger.warning("Unalbe to read file " + file + " " + e.getMessage());
        }

        return properties;
    }
}
