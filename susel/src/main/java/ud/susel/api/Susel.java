package ud.susel.api;

import ud.susel.impl.SuselImpl;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A super charged JPMS based service loader.
 */
public class Susel {

    private static final String ACTIVATE_ERROR = "You have to activate Susel before using it";

    private static SuselImpl SUSEL;

    public static void activate(Map<String, Object> contextMap) {
        SUSEL = new SuselImpl(new Context(contextMap));
    }

    public static <S> S get(Class<S> service) {
        Objects.requireNonNull(SUSEL, ACTIVATE_ERROR);
        return SUSEL.get(service);
    }

    public static <S> List<S> getAll(Class<S> service) {
        Objects.requireNonNull(SUSEL, ACTIVATE_ERROR);
        return SUSEL.getAll(service);
    }

    /**
     * @apiNote Internal use only--for unit testing purposes.
     */
    static void reset() {
        SUSEL = null;
    }
}
