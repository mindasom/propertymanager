package min.propertymonitor.helpers;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.InvalidPathException;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class ConfigurationHelper {
    private static Logger logger = Logger.getLogger(ConfigurationHelper.class.getName());

    public static boolean validateDirectoryToMonitor(String directoryToMonitor) {
        File directoryToMonitorFile = new File(directoryToMonitor);

        if (!directoryToMonitorFile.exists()) {
            logger.severe("Unable to monitor: " + directoryToMonitorFile.getAbsolutePath() + " does not exist");
            throw new InvalidPathException(directoryToMonitorFile.getAbsolutePath(), "does not exist");
        }

        if (!directoryToMonitorFile.isDirectory()) {
            logger.severe("Unable to monitor: " + directoryToMonitorFile.getAbsolutePath() + " is not a directory");
            throw new InvalidPathException(directoryToMonitorFile.getAbsolutePath(), "is not a directory");
        }

        if (!directoryToMonitorFile.canRead() || !directoryToMonitorFile.canWrite()) {
            logger.severe("Unable to read / write : " + directoryToMonitorFile.getAbsolutePath());
            throw new InvalidPathException(directoryToMonitorFile.getAbsolutePath(), " is not writable nor readable");
        }

        logger.info("Monitoring directory: " + directoryToMonitorFile.getAbsolutePath());
        return true;
    }

    public static boolean validateKeyFilter(String keyFilter) {
        Pattern.compile(keyFilter);
        return true;
    }

    public static boolean validateServerUrl(String serverUrl) {
        String url = "";

        if (serverUrl.endsWith("/")) {
            url = serverUrl + "filename";
        } else {
            url = serverUrl + "/filename";
        }

        HttpClient httpClient = HttpClient.newHttpClient();

        return validateServerUrl(url, httpClient);
    }

    static boolean validateServerUrl(String url, HttpClient httpClient) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = null;

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (response != null && response.statusCode() != 200) {
            logger.severe("Unable to connect to server " + url);
            throw new RuntimeException("Unable to connect to server " + url);
        }

        return true;
    }

}
