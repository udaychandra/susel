package ud.susel.util;

import ud.susel.common.Metadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static ud.susel.common.Constants.SUSEL_METADATA_RESOURCE_PATH;

/**
 * Loads the Susel properties file (susel.properties) embedded in the META-INF resource folder
 * of the module holding a given service provider.
 */
public class SuselMetadataLoader {

    private static final Object LOCK = new int[0];

    private final Map<Module, Metadata> perModuleMetadataCache;

    public SuselMetadataLoader() {
        perModuleMetadataCache = new HashMap<>();
    }

    /**
     * Load and get Susel {@link ud.susel.common.MetadataItem} of the module holding the specified service provider.
     *
     * @param serviceProvider the class that provides a specific service.
     * @param <S> the type of service provided.
     * @return the Susel {@link ud.susel.common.MetadataItem} of the module holding the specified service provider.
     * @throws IOException if Susel is unable to read the META-INF/susel.metadata resource file from the module
     * @throws SuselMetadataNullException if META-INF/susel.metadata resource file is not found in the module
     */
    public <S> Metadata load(Class<S> serviceProvider)
            throws IOException, SuselMetadataNullException {

        var module = serviceProvider.getModule();
        var metadata = perModuleMetadataCache.get(module);

        if (metadata != null) {
            return metadata;
        }

        synchronized (LOCK) {
            // Check one more time to see if another thread loaded the metadata.
            metadata = perModuleMetadataCache.get(module);

            if (metadata == null) {
                var properties = new Properties();

                try (InputStream inStream = serviceProvider.getModule().getResourceAsStream(SUSEL_METADATA_RESOURCE_PATH)) {
                    properties.load(inStream);
                } catch (NullPointerException ex) {
                    throw new SuselMetadataNullException(ex);
                }

                metadata = new Metadata(module, properties);
                perModuleMetadataCache.put(module, metadata);
            }
        }

        return metadata;
    }
}

