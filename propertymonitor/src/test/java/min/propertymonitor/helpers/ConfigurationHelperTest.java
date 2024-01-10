package min.propertymonitor.helpers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.regex.PatternSyntaxException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigurationHelperTest {

    public static final String VALID_REGEX = "[a-z].*";
    public static final String INVALID_REGEX = "[";
    public static final String VALID_SERVER_URL = "http://localhost:8080/properties/send/a.txt";

    @Test
    public void testValidateDirectoryToMonitor(@TempDir
                                        File testDirectoryToMonitor) {
        assertTrue(ConfigurationHelper.validateDirectoryToMonitor(testDirectoryToMonitor.getAbsolutePath()));
    }

    @Test
    public void testValidateDirectoryToMonitorWithInvalidDirectory(
            @TempDir File testDirectory
    ) throws IOException {
        Path textPath = Files.createFile(FileSystems.getDefault().getPath(testDirectory.getAbsolutePath(), "a.txt"));

        assertThrows(InvalidPathException.class, () -> {
            ConfigurationHelper.validateDirectoryToMonitor(textPath.toAbsolutePath().toString());
        });
    }

    @Test
    public void testValidateKeyFilter() {
        assertTrue(ConfigurationHelper.validateKeyFilter(VALID_REGEX));
    }

    @Test
    public void testValidateKeyFilterWithInvalidRegex() {
        assertThrows(PatternSyntaxException.class, () -> {
            ConfigurationHelper.validateKeyFilter(INVALID_REGEX);
        });
    }

    @Test
    public void testValidateServerUrl(@Mock HttpClient httpClient) throws IOException, InterruptedException {
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(200);

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        assertTrue(ConfigurationHelper.validateServerUrl(VALID_SERVER_URL, httpClient));
    }

    @Test
    public void testValidateServerUrlWithIOException(@Mock HttpClient httpClient) throws IOException, InterruptedException {
        when(httpClient.send(any(), any())).thenThrow(new IOException());

        assertThrows(RuntimeException.class, () -> {
            ConfigurationHelper.validateServerUrl(VALID_SERVER_URL, httpClient);
        });
    }
}