package ud.susel.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SuselPropertiesLoaderTest {

    @Test
    public void loadSuselPropertiesTest() throws Exception {
        SuselMetadataLoader loader = new SuselMetadataLoader();

        var holder = loader.load(this.getClass());

        assertNotNull(holder.properties().getProperty("com.example.MyServiceImpl_Refs"),
                "Susel properties should be loaded and 'com.example.MyServiceImpl_Refs' key should have a value");
    }
}

