package min.propertymanager.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class PropertyFileHandlerTest {

    public static final String TEST_PROPERTIES_FILE_NAME = "test.properties";

    public static final String EXISTING_KEY1 = "a";

    public static final String EXISTING_VALUE1 = "1";

    public static final String EXISTING_KEY2 = "b";

    public static final String EXISTING_VALUE2 = "2";

    public static final String KEY3 = "c";

    public static final String VALUE3 = "3";

    @TempDir
    private File testFilesDirectory;

    private PropertyFileHandler propertyFileHandler;

    private Path testPropertiesPath;

    @BeforeEach
    void createPropertyFileHandler() {
        testPropertiesPath = FileSystems.getDefault().getPath(
                testFilesDirectory.getAbsolutePath(), TEST_PROPERTIES_FILE_NAME);
        propertyFileHandler = new PropertyFileHandler(testPropertiesPath);
    }

    void createValidExistingTestPropertiesFile() {
        try {
            Files.writeString(testPropertiesPath, "a=1\nb=2", StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testLoadExistingPropertiesTo() {
        createValidExistingTestPropertiesFile();

        Properties properties = new Properties();
        propertyFileHandler.loadExistingPropertiesTo(properties);

        assertTrue(properties.containsKey(EXISTING_KEY1));
        assertEquals(EXISTING_VALUE1, properties.getProperty(EXISTING_KEY1));

        assertTrue(properties.containsKey(EXISTING_KEY2));
        assertEquals(EXISTING_VALUE2, properties.getProperty(EXISTING_KEY2)) ;
    }

    @Test
    void testDeleteExistingPropertyFile() {
        createValidExistingTestPropertiesFile();

        propertyFileHandler.deleteExistingPropertyFile();

        assertFalse(Files.exists(testPropertiesPath));
    }

    @Test
    void testWriteProperties() {
        Properties properties = new Properties();
        properties.put(KEY3, VALUE3);

        propertyFileHandler.writeProperties(properties);

        assertTrue(Files.exists(testPropertiesPath));

        try {
            List<String> propertyFilelines = Files.readAllLines(testPropertiesPath);

            for (String propertyFileLine : propertyFilelines) {
                if (!propertyFileLine.startsWith("#")) {
                    assertEquals(KEY3 + "=" + VALUE3, propertyFileLine.trim());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}