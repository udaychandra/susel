package ud.susel.util;

import ud.susel.common.PropertiesHolder;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static ud.susel.common.Constants.SUSEL_PROPERTIES_RESOURCE_PATH;

/**
 * Loads the Susel properties file (susel.properties) embedded in the META-INF resource folder
 * of the module holding a given service provider.
 */
public class SuselPropertiesLoader {

    private static final Object LOCK = new int[0];

    private final Map<Module, PropertiesHolder> perModulePropertiesHolderCache;

    public SuselPropertiesLoader() {
        perModulePropertiesHolderCache = new HashMap<>();
    }

    /**
     * Load and get Susel {@link Properties} of the module holding the specified service provider.
     *
     * @param serviceProvider the class that provides a specific service.
     * @param <S> the type of service provided.
     * @return the Susel {@link Properties} of the module holding the specified service provider.
     * @throws IOException
     */
    public <S> PropertiesHolder load(Class<S> serviceProvider)
            throws IOException, SuselPropertiesNullException {

        var module = serviceProvider.getModule();
        var holder = perModulePropertiesHolderCache.get(module);

        if (holder != null) {
            return holder;
        }

        synchronized (LOCK) {
            // Check one more time to see if another thread loaded the properties.
            holder = perModulePropertiesHolderCache.get(module);

            if (holder == null) {
                var properties = new Properties();

                try (InputStream inStream = serviceProvider.getModule().getResourceAsStream(SUSEL_PROPERTIES_RESOURCE_PATH)) {
                    properties.load(inStream);
                } catch (NullPointerException ex) {
                    throw new SuselPropertiesNullException(ex);
                }

                holder = new PropertiesHolder(module, properties);
                perModulePropertiesHolderCache.put(module, holder);
            }
        }

        return holder;
    }
}

