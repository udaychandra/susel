package ud.susel.common;

import java.util.Properties;

public class PropertiesHolder {
    private final Module module;
    private final Properties properties;

    public PropertiesHolder(Module module, Properties properties) {
        this.module = module;
        this.properties = properties;
    }

    public Module module() {
        return module;
    }

    public Properties properties() {
        return properties;
    }
}
