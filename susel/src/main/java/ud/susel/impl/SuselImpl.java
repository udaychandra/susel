package ud.susel.impl;

import ud.susel.api.Context;
import ud.susel.common.MetadataItem;
import ud.susel.util.SuselMetadataLoader;
import ud.susel.util.SuselMetadataNullException;

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

    private final SuselMetadataLoader metadataLoader;
    private final Map<Class<?>, List<?>> serviceProvidersCache;
    private final Context context;

    public SuselImpl(Context context) {
        metadataLoader = new SuselMetadataLoader();
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
                // TODO: Can't rely on this assumption that Susel module layer will also contain the module in question.
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
            MetadataItem metadataItem = new MetadataItem(
                    serviceProvider.getClass(),
                    metadataLoader.load(serviceProvider.getClass()));

            for (var ref : metadataItem.references()) {
                prepareReference(serviceProvider, ref);
            }

            if (metadataItem.shouldActivate()) {
                var activateMethod = serviceProvider.getClass().getMethod(metadataItem.activateMethodName(), Context.class);
                activateMethod.invoke(serviceProvider, context);
            }

        } catch (IOException | SuselMetadataNullException ex) {
            throw new RuntimeException("Unable to load Susel metadata. Check to see if you've generated the metadata with Susel plugin", ex);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException("Unable to load service provider. Activation method not found", ex);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException("Unable to activate service provider", ex);
        }
    }

    private <S> void prepareReference(S serviceProvider, MetadataItem.Reference ref)
            throws NoSuchMethodException, RuntimeException, IllegalAccessException, InvocationTargetException {

        var setterMethod = serviceProvider.getClass().getMethod(ref.setterMethodName(), ref.serviceClass());
        var serviceProviderList = getAll(ref.serviceClass());

        // TODO: Handle cardinality and out-of-bounds.
        setterMethod.invoke(serviceProvider, serviceProviderList.get(0));
    }
}
