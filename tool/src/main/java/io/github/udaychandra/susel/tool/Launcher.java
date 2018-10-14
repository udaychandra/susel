package io.github.udaychandra.susel.tool;

import io.github.udaychandra.susel.tool.impl.Processor;

public class Launcher {
    private static final String MODULE_NAME_OPTION = "module-name";
    private static final String META_INF_ROOT_PATH_OPTION = "meta-inf-root-path";

    /**
     * Entry point into the Susel tool that scans a module and saves service providers' metadata.
     *
     * @param args CLI options used to pull "moduleName" and "metaInfPath" values.
     */
    public static void main(String...args) {
        var moduleName = getOption(MODULE_NAME_OPTION, args);
        var metaInfRootPath = getOption(META_INF_ROOT_PATH_OPTION, args);

        new Processor().process(moduleName, metaInfRootPath);
    }

    private static String getOption(String optionName, String...args) {
        var fullOptionName = "--" + optionName;
        var index = -1;

        for (var a=0; a<args.length; a++) {
            if (fullOptionName.equalsIgnoreCase(args[a])) {
                index = a;
                break;
            }
        }

        if (index < 0 || ((index + 1) >= args.length)) {
            throw new RuntimeException(String.format("Required option %s not found", fullOptionName));
        }

        var optionValue = args[index + 1];

        if (optionValue == null || optionValue.isBlank()) {
            throw new RuntimeException(String.format("Required option %s cannot be blank", fullOptionName));
        }

        return optionValue.trim();
    }
}

