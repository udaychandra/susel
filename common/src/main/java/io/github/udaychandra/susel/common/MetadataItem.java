package io.github.udaychandra.susel.common;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static io.github.udaychandra.susel.common.Constants.ACTIVATE_SUFFIX;
import static io.github.udaychandra.susel.common.Constants.COLON;
import static io.github.udaychandra.susel.common.Constants.COMMA;
import static io.github.udaychandra.susel.common.Constants.REFS_SUFFIX;
import static io.github.udaychandra.susel.common.Constants.SEMI_COLON;

/**
 * Represents the metadata of a given service provider loaded by Susel.
 * Metadata includes:
 * 1) the activation method name of the service provider that will be invoked by Susel
 * 2) the list of services that are required by the given service provider and that are to be loaded by Susel
 */
public class MetadataItem {

    private final String providerName;
    private final String activateMethodName;
    private final List<Reference> references;

    public MetadataItem(Class<?> serviceProvider, Method activateMethod, List<Reference> references) {
        providerName = serviceProvider.getName();
        activateMethodName = activateMethod != null ? activateMethod.getName() : null;
        this.references = references;
    }

    public MetadataItem(Class<?> serviceProvider, Metadata holder) {
        providerName = serviceProvider.getName();
        activateMethodName = holder.properties().getProperty(providerName + ACTIVATE_SUFFIX);

        var refs = holder.properties().getProperty(providerName + REFS_SUFFIX);

        if (refs != null && !refs.isBlank()) {
            var tokens = refs.split(COMMA);
            references = new ArrayList<>(tokens.length);
            for (String t : tokens) {
                references.add(new Reference(t, holder.module().getLayer()));
            }

        } else {
            references = List.of();
        }
    }

    public String providerName() {
        return providerName;
    }

    public String activateMethodName() {
        return activateMethodName;
    }

    public List<Reference> references() {
        return references;
    }

    public boolean shouldActivate() {
        return activateMethodName != null && activateMethodName.length() > 0;
    }

    public void store(Properties properties) {
        buildRefsEntry(properties);
        buildActivateEntry(properties);
    }

    private void buildRefsEntry(Properties properties) {
        if (references().isEmpty()) return;

        var builder = new StringBuilder();
        var r = 0;

        for (; r<references().size()-1; r++) {
            buildRef(references().get(r), builder);
            builder.append(COMMA);
        }

        buildRef(references().get(r), builder);
        properties.setProperty(providerName() + REFS_SUFFIX, builder.toString());
    }

    private void buildRef(MetadataItem.Reference reference, StringBuilder builder) {
        builder.append(reference.serviceClass().getModule().getName());
        builder.append(COLON);
        builder.append(reference.serviceClass().getName());
        builder.append(SEMI_COLON);
        builder.append(reference.setterMethodName());
        builder.append(SEMI_COLON);
        builder.append(reference.isList());
        builder.append(SEMI_COLON);
        builder.append(reference.isOptional());
    }

    private void buildActivateEntry(Properties properties) {
        if (shouldActivate()) {
            properties.setProperty(providerName() + ACTIVATE_SUFFIX, activateMethodName());
        }
    }

    public static class Reference {
        private final Class<?> serviceClass;
        private final String setterMethodName;
        private final boolean isList;
        private final boolean isOptional;

        public Reference(Class<?> serviceClass, String setterMethodName, boolean isList, boolean isOptional) {
            this.serviceClass = serviceClass;
            this.setterMethodName = setterMethodName;
            this.isList = isList;
            this.isOptional = isOptional;
        }

        public Reference(String reference, ModuleLayer moduleLayer) {
            var tokens = reference.split(SEMI_COLON);

            // Yes, no error handling for index out-of-bound scenarios.
            var serviceTokens = tokens[0].split(COLON);

            var serviceModule = moduleLayer.findModule(serviceTokens[0]);
            if (serviceModule.isPresent()) {
                serviceClass = Class.forName(serviceModule.get(), serviceTokens[1]);
            } else {
                throw new RuntimeException(String.format("Unable to find the module (%s) for service provider (%s)", serviceTokens[0], serviceTokens[1]));
            }

            setterMethodName = tokens[1];
            isList = Boolean.valueOf(tokens[2]);
            isOptional = Boolean.valueOf(tokens[3]);
        }

        public Class<?> serviceClass() {
            return serviceClass;
        }

        public String setterMethodName() {
            return setterMethodName;
        }

        public boolean isList() {
            return isList;
        }

        public boolean isOptional() {
            return isOptional;
        }
    }
}
