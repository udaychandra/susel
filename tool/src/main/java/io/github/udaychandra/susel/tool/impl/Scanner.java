package io.github.udaychandra.susel.tool.impl;

import io.github.udaychandra.susel.api.Activate;
import io.github.udaychandra.susel.api.Cardinality;
import io.github.udaychandra.susel.api.Context;
import io.github.udaychandra.susel.api.ServiceReference;
import io.github.udaychandra.susel.common.Metadata;
import io.github.udaychandra.susel.common.MetadataItem;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static io.github.udaychandra.susel.tool.util.ReflectionUtils.disallowArrayParam;
import static io.github.udaychandra.susel.tool.util.ReflectionUtils.isPublicInstance;
import static io.github.udaychandra.susel.tool.util.ReflectionUtils.paramType;
import static io.github.udaychandra.susel.tool.util.ReflectionUtils.requiresParam;
import static io.github.udaychandra.susel.tool.util.ReflectionUtils.requiresSingleParam;

public class Scanner {

    public Metadata scan(Module module) {
        var metadata = new Metadata(module);
        var descriptor = module.getDescriptor();

        descriptor.provides().forEach(provides -> {
            provides.providers().forEach(provider -> {
                var providerClass = Class.forName(module, provider);
                var metadataItem = buildMetadataItem(providerClass);
                metadata.addItem(metadataItem);
            });
        });

        return metadata;
    }

    private MetadataItem buildMetadataItem(Class<?> providerClass) {
        var methods = providerClass.getMethods();
        var references = new ArrayList<MetadataItem.Reference>();
        Method activateMethod = null;

        for (var method : methods) {
            if (!isPublicInstance(method)) continue;

            if (method.isAnnotationPresent(ServiceReference.class)) {
                requiresSingleParam(method);
                disallowArrayParam(method, method.getParameters()[0]);

                var reference = method.getAnnotation(ServiceReference.class);
                references.add(buildReference(method, reference));
            }

            if (method.isAnnotationPresent(Activate.class)) {
                requiresSingleParam(method);
                requiresParam(method, method.getParameters()[0], Context.class);

                activateMethod = method;
            }
        }

        return new MetadataItem(providerClass, activateMethod, references);
    }

    private MetadataItem.Reference buildReference(Method method, ServiceReference serviceReference) {
        var paramHolder = paramType(method, 0);

        if (paramHolder.isList()) {
            // Unary cardinality and List param type is a mismatch.
            if (serviceReference.cardinality() != Cardinality.ZERO_OR_MORE &&
                    serviceReference.cardinality() != Cardinality.ONE_OR_MORE) {
                throw new RuntimeException(String.format(
                        "@ServiceReference cardinality on the method (%s) is specified as unary but the type for parameter (%s) is specified as List",
                        method.getName(),
                        method.getParameters()[0].getName()));
            }
        } else {
            // Multiplicity (of the cardinality) and param type is a mismatch.
            if (serviceReference.cardinality() != Cardinality.ZERO_OR_ONE &&
                    serviceReference.cardinality() != Cardinality.ONE) {
                throw new RuntimeException(String.format(
                        "@ServiceReference cardinality on the method (%s) is specified as multiple but the type for parameter (%s) is not specified as List",
                        method.getName(),
                        method.getParameters()[0].getName()));
            }
        }

        return new MetadataItem.Reference(
                paramHolder.actualType(),
                method.getName(),
                paramHolder.isList(),
                isOptional(serviceReference));
    }

    private boolean isOptional(ServiceReference serviceReference) {
        return serviceReference.cardinality() == Cardinality.ZERO_OR_ONE ||
                serviceReference.cardinality() == Cardinality.ZERO_OR_MORE;
    }
}
