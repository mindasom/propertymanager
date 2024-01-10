package min.propertymonitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyFileMonitorTest {
    public static final String TEST_PROPERTIES_FILE_NAME = "test.properties";

    public static final String VALID_KEY_REGEX = "[a-z].*";

    public static final String VALID_SERVER_URL_PREFIX = "http://localhost:8080/properties/send/";
    public static final String VALID_SERVER_URL = VALID_SERVER_URL_PREFIX + TEST_PROPERTIES_FILE_NAME;

    public static final String VALID_KEY1 = "a";

    public static final String VALUE1 = "1";

    public static final String INVALID_KEY2 = "2";

    private static final String VALUE2 = "2";

    @TempDir
    File propertyFilesDirectory;

    Path propertyFilePath;

    @Mock
    HttpClient httpClient;

    private PropertyFileMonitor propertyFileMonitor;

    @BeforeEach
    public void initPropertyFileMonitor() {
        propertyFilePath = FileSystems.getDefault().getPath(propertyFilesDirectory.getAbsolutePath(), TEST_PROPERTIES_FILE_NAME);

        propertyFileMonitor = new PropertyFileMonitor(propertyFilePath.toString(),
                VALID_KEY_REGEX, VALID_SERVER_URL_PREFIX, httpClient);
    }

    @Test
    public void testReadFile() throws IOException {
        createValidPropertyFile();

        Properties properties = propertyFileMonitor.readFile();

        assertTrue(properties.containsKey(VALID_KEY1));
        assertEquals(VALUE1, properties.getProperty(VALID_KEY1));
    }

    private void createValidPropertyFile() throws IOException {
        Files.writeString(propertyFilePath, VALID_KEY1 + "=" + VALUE1, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE_NEW);
    }

    @Test
    public void testFilterProperty() {
        Properties properties = new Properties();
        properties.put(VALID_KEY1, VALUE1);
        properties.put(INVALID_KEY2, VALUE2);

        Map<String, String> filterdProperties = propertyFileMonitor.filterProperty(properties);

        assertTrue(filterdProperties.containsKey(VALID_KEY1));
        assertEquals(VALUE1, filterdProperties.get(VALID_KEY1));

        assertFalse(filterdProperties.containsKey(INVALID_KEY2));
    }

    @Test
    public void testSendToServer() throws IOException, InterruptedException {
        HttpResponse httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(200);

        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(httpResponse);

        Map<String, String> properties = Collections.singletonMap(VALID_KEY1, VALUE1);

        boolean sent = propertyFileMonitor.sendToServer(properties);

        ArgumentCaptor<HttpRequest> httpRequestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(httpRequestCaptor.capture(), eq(HttpResponse.BodyHandlers.ofString()));

        HttpRequest httpRequestCaptorValue = httpRequestCaptor.getValue();

        assertEquals(VALID_SERVER_URL, httpRequestCaptorValue.uri().toString());
        assertEquals("POST", httpRequestCaptorValue.method());
        assertEquals(Collections.singletonList("application/json"), httpRequestCaptorValue.headers().map().get("Content-Type"));
    }

    @Test
    public void testDelete() throws IOException {
        createValidPropertyFile();

        propertyFileMonitor.delete();

        assertFalse(Files.exists(propertyFilePath));
    }
}