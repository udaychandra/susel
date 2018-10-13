package ud.susel.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static ud.susel.common.Constants.ACTIVATE_SUFFIX;
import static ud.susel.common.Constants.COLON;
import static ud.susel.common.Constants.COMMA;
import static ud.susel.common.Constants.REFS_SUFFIX;
import static ud.susel.common.Constants.SEMI_COLON;
import static ud.susel.common.Constants.SUSEL_METADATA_FILE_NAME;

public class Metadata {
    private final Module module;
    private final List<MetadataItem> metadataItems;
    private Properties properties;

    public Metadata(Module module) {
        this(module, null);
    }

    public Metadata(Module module, Properties properties) {
        this.module = module;
        metadataItems = new ArrayList<>();
        this.properties = properties;
    }

    public Module module() {
        return module;
    }

    public Properties properties() {
        return properties;
    }

    public List<MetadataItem> items() {
        return metadataItems;
    }

    public void addItem(MetadataItem item) {
        items().add(item);
    }

    public void store(File metaInfFolder) throws IOException {
        var suselFile = new File(metaInfFolder, SUSEL_METADATA_FILE_NAME);
        loadProperties();

        var writer = new OutputStreamWriter(new FileOutputStream(suselFile), StandardCharsets.UTF_8);
        properties.store(writer, null);
    }

    private void loadProperties() {
        if (properties != null) return;

        properties = new Properties();
        metadataItems.forEach(item -> item.store(properties));
    }
}
