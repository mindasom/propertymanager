package min.propertymanager.controllers;

import min.propertymanager.helpers.PropertyFileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyControllerTest {
    public static final String PROPERTY_FILES_DIRECTORY_PATH = "properties";

    public static final String KEY = "a";

    public static final String VALUE = "1";

    private PropertyController propertyController;

    @Mock
    private PropertyFileHandler propertyFileHandler;

    @BeforeEach
    public void initPropertyController() {
        propertyController = new PropertyController();
        propertyController.setPropertyFilesPath(PROPERTY_FILES_DIRECTORY_PATH);
    }

    @Test
    void testSendProperties() {
        propertyController.sendProperties(Collections.singletonMap(KEY, VALUE), propertyFileHandler);

        verify(propertyFileHandler).loadExistingPropertiesTo(any(Properties.class));

        verify(propertyFileHandler).deleteExistingPropertyFile();

        ArgumentCaptor<Properties> mergedPropertiesCaptor = ArgumentCaptor.forClass(Properties.class);
        verify(propertyFileHandler).writeProperties(mergedPropertiesCaptor.capture());

        assertTrue(mergedPropertiesCaptor.getValue().containsKey(KEY));
        assertEquals(VALUE, mergedPropertiesCaptor.getValue().getProperty(KEY));
    }
}