package ud.susel.impl;

import ud.susel.api.Context;
import ud.susel.common.Metadata;
import ud.susel.util.SuselPropertiesLoader;
import ud.susel.util.SuselPropertiesNullException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public class SuselImpl {

    private static final Object LOCK = new int[0];

    private static final Module SUSEL_MODULE = SuselImpl.class.getModule();

    private final SuselPropertiesLoader propertiesLoader;
    private final Map<Class<?>, List<?>> serviceProvidersCache;
    private final Context context;

    public SuselImpl(Context context) {
        propertiesLoader = new SuselPropertiesLoader();
        serviceProvidersCache = new HashMap<>();
        this.context = context;
    }

    public <S> S get(Class<S> service) {
        var serviceProviderList = getAll(service);
        return serviceProviderList.isEmpty() ? null : serviceProviderList.get(0);
    }

    @SuppressWarnings("unchecked")
    public <S> List<S> getAll(Class<S> service) {
        // Attempt to load service providers from the cache.
        var serviceProviderList = (List<S>) serviceProvidersCache.get(service);

        if (serviceProviderList != null) {
            return serviceProviderList;
        }

        synchronized (LOCK) {
            // Attempt one more time to see if another thread loaded the service providers.
            serviceProviderList = (List<S>) serviceProvidersCache.get(service);

            if (serviceProviderList == null) {
                serviceProviderList = new ArrayList<>();

                // Susel's module should indicate the intention to use the given service so that
                // the ServiceLoader can lookup the requested service providers.
                SUSEL_MODULE.addUses(service);

                // Pass the application module layer that typically loads Susel.
                var serviceProvidersIterator = ServiceLoader.load(SUSEL_MODULE.getLayer(), service);

                for (S serviceProvider : serviceProvidersIterator) {
                    prepare(serviceProvider);

                    serviceProviderList.add(serviceProvider);
                }

                serviceProvidersCache.put(service, serviceProviderList);
            }
        }

        return serviceProviderList;
    }

    private <S> void prepare(S serviceProvider) {
        try {
            Metadata metadata = new Metadata(
                    serviceProvider.getClass(),
                    propertiesLoader.load(serviceProvider.getClass()));

            for (var ref : metadata.references()) {
                prepareReference(serviceProvider, ref);
            }

            if (metadata.shouldActivate()) {
                var activateMethod = serviceProvider.getClass().getMethod(metadata.activateMethodName(), Context.class);
                activateMethod.invoke(serviceProvider, context);
            }

        } catch (IOException | SuselPropertiesNullException ex) {
            throw new RuntimeException("Unable to load Susel properties/metadata. Check to see if you've generated the metadata with Susel plugin", ex);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException("Unable to load service provider. Activation method not found", ex);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException("Unable to activate service provider", ex);
        }
    }

    private <S> void prepareReference(S serviceProvider, Metadata.Reference ref)
            throws NoSuchMethodException, RuntimeException, IllegalAccessException, InvocationTargetException {

        var setterMethod = serviceProvider.getClass().getMethod(ref.setterMethodName(), ref.serviceClass());
        var serviceProviderList = getAll(ref.serviceClass());

        // TODO: Handle cardinality and out-of-bounds.
        setterMethod.invoke(serviceProvider, serviceProviderList.get(0));
    }
}
