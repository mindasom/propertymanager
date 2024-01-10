package min.propertymanager.helpers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationHelperTest {

    public static final String TEST_CONFIG_PROPERTIES_FILE = "testConfig.properties";
    public static final String TEST_PROPERTIES_FILE_DIR = "properties";
    public static final String TEST_SERVER_PORT = "8068";

    @TempDir
    private File tempDir;

    @Test
    public void testValidatePropertyFilesDirectoryWithReusableOne() {
        File reusablePropertyFilesDirectoryFile = createValidExistingPropertyFilesDirectory();
        String reusablePropertyFilesDirectoryAbsolutePath = reusablePropertyFilesDirectoryFile.getAbsolutePath();

        String validPropertyFilesDirectory = ConfigurationHelper.validatePropertyFilesDirectory(reusablePropertyFilesDirectoryAbsolutePath);

        assertEquals(reusablePropertyFilesDirectoryAbsolutePath, validPropertyFilesDirectory);
    }

    private File createValidExistingPropertyFilesDirectory() {
        File propertyFilesDirectory = new File(tempDir, TEST_PROPERTIES_FILE_DIR);
        propertyFilesDirectory.mkdir();
        return propertyFilesDirectory;
    }

    @Test
    public void testCreatePropertyFilesDirectory() {
        File newPropertyFilesDirectory = new File(tempDir, TEST_PROPERTIES_FILE_DIR);
        String newPropertyFilesDirectoryAbsolutePath = newPropertyFilesDirectory.getAbsolutePath();

        boolean isCreated = ConfigurationHelper.createPropertyFilesDirectory(newPropertyFilesDirectoryAbsolutePath);

        assertTrue(isCreated);
        assertTrue(newPropertyFilesDirectory.exists());
        assertTrue(newPropertyFilesDirectory.isDirectory());
    }
}