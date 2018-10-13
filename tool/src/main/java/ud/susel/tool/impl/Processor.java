package ud.susel.tool.impl;

import java.io.File;
import java.io.IOException;

import static ud.susel.common.Constants.META_INF_FOLDER_NAME;

public class Processor {

    public void process(String moduleName, String metaInfRootPath) {
        var metaInfFolder = getMetaInfFolder(metaInfRootPath);

        this.getClass().getModule().getLayer().findModule(moduleName).ifPresentOrElse(module -> {
            var metadata = new Scanner().scan(module);
            try {
                metadata.store(metaInfFolder);
            } catch (IOException ex) {
                throw new RuntimeException("Unable to save Susel metadata for module: " + moduleName, ex);
            }

        }, () -> {
            throw new RuntimeException("Unable to find module: " + moduleName);
        });
    }

    private File getMetaInfFolder(String metaInfRootPath) {
        var metaInfFolder = new File(metaInfRootPath, META_INF_FOLDER_NAME);

        if (!metaInfFolder.exists() && !metaInfFolder.mkdir()) {
            throw new RuntimeException("Unable to create META-INF folder: " + metaInfFolder);
        } else if (!metaInfFolder.isDirectory()) {
            throw new RuntimeException("META-INF should be a folder: " + metaInfFolder);
        }

        return metaInfFolder;
    }
}
