package ud.susel.api;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Represents Susel global context that will be passed to service providers' activation methods
 * for context or config look-ups.
 */
public class Context {
    private final Map<String, Object> internalContext;

    Context(Map<String, Object> contextMap) {
        Objects.requireNonNull(contextMap, "A non-null context map should be specified");
        this.internalContext = Collections.unmodifiableMap(contextMap);
    }

    @SuppressWarnings("unchecked")
    public <V> V value(String key) {
        return (V) internalContext.get(key);
    }
}
