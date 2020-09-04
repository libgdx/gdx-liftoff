
package com.github.czyzby.autumn.mvc.component.asset;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.ObjectSet.ObjectSetIterator;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.czyzby.autumn.annotation.Destroy;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.mvc.component.asset.dto.injection.ArrayAssetInjection;
import com.github.czyzby.autumn.mvc.component.asset.dto.injection.AssetInjection;
import com.github.czyzby.autumn.mvc.component.asset.dto.injection.ObjectMapAssetInjection;
import com.github.czyzby.autumn.mvc.component.asset.dto.injection.ObjectSetAssetInjection;
import com.github.czyzby.autumn.mvc.component.asset.dto.injection.StandardAssetInjection;
import com.github.czyzby.autumn.mvc.component.asset.dto.provider.ArrayAssetProvider;
import com.github.czyzby.autumn.mvc.component.asset.dto.provider.AssetProvider;
import com.github.czyzby.autumn.mvc.component.asset.dto.provider.ObjectMapAssetProvider;
import com.github.czyzby.autumn.mvc.component.asset.dto.provider.ObjectSetAssetProvider;
import com.github.czyzby.autumn.mvc.config.AutumnActionPriority;
import com.github.czyzby.autumn.mvc.config.AutumnMessage;
import com.github.czyzby.autumn.mvc.stereotype.Asset;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;
import com.github.czyzby.autumn.processor.event.MessageDispatcher;
import com.github.czyzby.kiwi.util.gdx.asset.Disposables;
import com.github.czyzby.kiwi.util.gdx.asset.lazy.Lazy;
import com.github.czyzby.kiwi.util.gdx.asset.lazy.provider.ObjectProvider;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.kiwi.util.gdx.collection.GdxSets;
import com.github.czyzby.kiwi.util.gdx.reflection.Annotations;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;

/** Wraps around two internal {@link AssetManager}s, providing utilities for asset loading.
 * Allows to load assets both eagerly and by constant updating, without forcing loading of "lazy" assets upon "eager"
 * request, like AssetManager does (see {@link AssetManager#finishLoadingAsset(String)}
 * implementation - it basically loads everything, waiting for a specific asset to get loaded). Note that some wrapped
 * methods provide additional utility and validations, so direct access to the managers is not advised.
 *
 * @author MJ */
public class AssetService extends AbstractAnnotationProcessor<Asset> {
    private final AssetManager assetManager = new AssetManager();
    /** There is no reliable way of keeping both eagerly and normally loaded assets together, while preserving a way to
     * both load assets by constant updating and load SOME assets at once. That's why this service uses two managers. */
    private final AssetManager eagerAssetManager = new AssetManager();

    private final ObjectSet<AssetInjection> assetInjections = GdxSets.newSet();
    private final ObjectSet<String> scheduledAssets = GdxSets.newSet();
    private final Array<Runnable> onLoadActions = GdxArrays.newArray();

    @Inject private MessageDispatcher messageDispatcher;

    /** @param loader asset loader for the selected type. Will be registered in all managed {@link AssetManager}
     *            instances.
     * @param assetClass class of the loaded asset.
     * @see AssetManager#setLoader(Class, AssetLoader)
     * @param <Type> type of registered loader. */
    public <Type> void registerLoader(final AssetLoader<Type, AssetLoaderParameters<Type>> loader,
            final Class<Type> assetClass) {
        assetManager.setLoader(assetClass, loader);
        eagerAssetManager.setLoader(assetClass, loader);
    }

    /** @param loader asset loader for the selected type. Will be registered in all managed {@link AssetManager}
     *            instances.
     * @param suffix allows to filter files.
     * @param assetClass class of the loaded asset.
     * @see AssetManager#setLoader(Class, String, AssetLoader)
     * @param <Type> type of registered loader. */
    public <Type> void registerLoader(final AssetLoader<Type, AssetLoaderParameters<Type>> loader, final String suffix,
            final Class<Type> assetClass) {
        assetManager.setLoader(assetClass, suffix, loader);
        eagerAssetManager.setLoader(assetClass, suffix, loader);
    }

    /** Schedules loading of the selected asset, if it was not scheduled already.
     *
     * @param assetPath internal path to the asset.
     * @param assetClass class of the asset. */
    public void load(final String assetPath, final Class<?> assetClass) {
        load(assetPath, assetClass, null);
    }

    /** Schedules loading of the selected asset, if it was not scheduled already.
     *
     * @param assetPath assetPath internal path to the asset.
     * @param assetClass assetClass class of the asset.
     * @param loadingParameters specific loading parameters.
     * @param <Type> type of asset class to load. */
    public <Type> void load(final String assetPath, final Class<Type> assetClass,
            final AssetLoaderParameters<Type> loadingParameters) {
        if (isAssetNotScheduled(assetPath)) {
            assetManager.load(assetPath, assetClass, loadingParameters);
        }
    }

    private boolean isAssetNotScheduled(final String assetPath) {
        return !isLoaded(assetPath) && !scheduledAssets.contains(assetPath);
    }

    /** @param assetPath internal path to the asset.
     * @return true if the asset is fully loaded. */
    public boolean isLoaded(final String assetPath) {
        return assetManager.isLoaded(assetPath) || eagerAssetManager.isLoaded(assetPath);
    }

    /** Schedules disposing of the selected asset.
     *
     * @param assetPath internal path to the asset. */
    public void unload(final String assetPath) {
        if (assetManager.isLoaded(assetPath) || scheduledAssets.contains(assetPath)) {
            assetManager.unload(assetPath);
        } else if (eagerAssetManager.isLoaded(assetPath)) {
            eagerAssetManager.unload(assetPath);
        }
    }

    /** Immediately loads all scheduled assets. */
    public void finishLoading() {
        assetManager.finishLoading();
        doOnLoadingFinish();
    }

    private void invokeOnLoadActions() {
        for (final Runnable action : onLoadActions) {
            if (action != null) {
                action.run();
            }
        }
        onLoadActions.clear();
    }

    /** Immediately loads the chosen asset. Schedules loading of the asset if it wasn't selected to be loaded already.
     *
     * @param assetPath internal path to the asset.
     * @param assetClass class of the loaded asset.
     * @return instance of the loaded asset.
     * @param <Type> type of asset class to load. */
    public <Type> Type finishLoading(final String assetPath, final Class<Type> assetClass) {
        return finishLoading(assetPath, assetClass, null);
    }

    /** Immediately loads the chosen asset. Schedules loading of the asset if it wasn't selected to be loaded already.
     *
     * @param assetPath internal path to the asset.
     * @param assetClass class of the loaded asset.
     * @param loadingParameters used if asset is not already loaded.
     * @return instance of the loaded asset.
     * @param <Type> type of asset class to load. */
    public <Type> Type finishLoading(final String assetPath, final Class<Type> assetClass,
            final AssetLoaderParameters<Type> loadingParameters) {
        if (assetManager.isLoaded(assetPath)) {
            return assetManager.get(assetPath, assetClass);
        }
        if (!eagerAssetManager.isLoaded(assetPath)) {
            eagerAssetManager.load(assetPath, assetClass, loadingParameters);
            eagerAssetManager.finishLoadingAsset(assetPath);
        }
        return eagerAssetManager.get(assetPath, assetClass);
    }

    private void injectRequestedAssets() {
        for (final ObjectSetIterator<AssetInjection> iterator = assetInjections.iterator(); iterator.hasNext();) {
            final AssetInjection assetInjection = iterator.next();
            if (assetInjection.inject(this)) {
                assetInjection.removeScheduledAssets(scheduledAssets);
                iterator.remove();
            }
        }
    }

    /** Manually updates wrapped asset manager.
     *
     * @return true if all scheduled assets are loaded. */
    public boolean update() {
        final boolean isLoaded = assetManager.update();
        if (isLoaded) {
            doOnLoadingFinish();
        }
        return isLoaded;
    }

    private void doOnLoadingFinish() {
        injectRequestedAssets();
        invokeOnLoadActions();
        messageDispatcher.postMessage(AutumnMessage.ASSETS_LOADED);
    }

    /** @return progress of asset loading. Does not include eagerly loaded assets. */
    public float getLoadingProgress() {
        return assetManager.getProgress();
    }

    /** @return progress of asset loading. Includes eagerly loaded assets. */
    public float getTotalLoadingProgress() {
        return assetManager.getProgress() * eagerAssetManager.getProgress();
    }

    /** @return direct reference to internal {@link AssetManager} instance. Use with care.
     * @see #getEagerAssetManager() */
    public AssetManager getAssetManager() {
        return assetManager;
    }

    /** @return direct reference to internal {@link AssetManager} used for eager asset loading. For synchronous asset
     *         loading purposes. Use with care. */
    public AssetManager getEagerAssetManager() {
        return eagerAssetManager;
    }

    /** @param assetPath internal path to the asset.
     * @param assetClass class of the asset.
     * @return an instance of the loaded asset, if available.
     * @param <Type> type of asset class to get. */
    public <Type> Type get(final String assetPath, final Class<Type> assetClass) {
        if (assetManager.isLoaded(assetPath)) {
            return assetManager.get(assetPath, assetClass);
        }
        return eagerAssetManager.get(assetPath, assetClass);
    }

    @Destroy(priority = AutumnActionPriority.MIN_PRIORITY)
    private void destroy() {
        Disposables.disposeOf(assetManager, eagerAssetManager);
    }

    @Override
    public Class<Asset> getSupportedAnnotationType() {
        return Asset.class;
    }

    @Override
    public boolean isSupportingFields() {
        return true;
    }

    @Override
    public void processField(final Field field, final Asset annotation, final Object component, final Context context,
            final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        validateAssetData(component, field, annotation);
        if (field.getType().equals(Lazy.class)) {
            handleLazyAssetInjection(component, field, annotation);
        } else if (field.getType().equals(Array.class)) {
            handleArrayInjection(component, field, annotation);
        } else if (field.getType().equals(ObjectSet.class)) {
            handleSetInjection(component, field, annotation);
        } else if (field.getType().equals(ObjectMap.class)) {
            handleMapInjection(component, field, annotation);
        } else {
            handleRegularAssetInjection(component, field, annotation);
        }
    }

    private static void validateAssetData(final Object component, final Field field, final Asset assetData) {
        if (assetData.value().length == 0) {
            throw new GdxRuntimeException("Asset paths array cannot be empty. Found empty array in field: " + field
                    + " of component: " + component + ".");
        }
        if (assetData.keys().length != 0 && assetData.value().length != assetData.keys().length) {
            throw new GdxRuntimeException(
                    "In @Asset annotation, keys() array length (if specified) has to match value() array length. Found different lengths in field: "
                            + field + " of component: " + component + ".");
        }
    }

    private void handleLazyAssetInjection(final Object component, final Field field, final Asset assetData) {
        if (Annotations.isNotVoid(assetData.lazyCollection())) {
            handleLazyAssetCollectionInjection(component, field, assetData);
            return;
        } else if (assetData.value().length != 1) {
            throw new GdxRuntimeException(
                    "Lazy wrapper can contain only one asset if lazy collection type is not provided. Found multiple assets in field: "
                            + field + " of component: " + component);
        }
        final String assetPath = assetData.value()[0];
        if (!assetData.loadOnDemand()) {
            load(assetPath, assetData.type());
        }
        try {
            Reflection.setFieldValue(field, component,
                    Lazy.providedBy(new AssetProvider(this, assetPath, assetData.type(), assetData.loadOnDemand())));
        } catch (final ReflectionException exception) {
            throw new GdxRuntimeException("Unable to inject lazy asset.", exception);
        }
    }

    private void handleLazyAssetCollectionInjection(final Object component, final Field field, final Asset assetData) {
        final String[] assetPaths = assetData.value();
        final Class<?> assetType = assetData.type();
        if (!assetData.loadOnDemand()) {
            for (final String assetPath : assetPaths) {
                load(assetPath, assetType);
            }
        }
        try {
            ObjectProvider<?> provider;
            final Class<?> collectionClass = assetData.lazyCollection();
            if (collectionClass.equals(Array.class)) {
                provider = new ArrayAssetProvider(this, assetPaths, assetType, assetData.loadOnDemand());
            } else if (collectionClass.equals(ObjectSet.class)) {
                provider = new ObjectSetAssetProvider(this, assetPaths, assetType, assetData.loadOnDemand());
            } else if (collectionClass.equals(ObjectMap.class)) {
                provider = new ObjectMapAssetProvider(this, assetPaths, assetData.keys(), assetType,
                        assetData.loadOnDemand());
            } else {
                throw new GdxRuntimeException("Unsupported collection type in annotated class of component: "
                        + component + ". Expected Array, ObjectSet or ObjectMap, received: " + collectionClass + ".");
            }
            Reflection.setFieldValue(field, component, Lazy.providedBy(provider));
        } catch (final ReflectionException exception) {
            throw new GdxRuntimeException("Unable to inject lazy asset collection.", exception);
        }
    }

    private void handleRegularAssetInjection(final Object component, final Field field, final Asset assetData) {
        if (assetData.value().length != 1) {
            throw new GdxRuntimeException(
                    "Regular fields can store only 1 asset. If the field is a collection, its type is not currently supported: only libGDX Array, ObjectSet and ObjectMap are permitted. Regular arrays will not be supported. Found multiple assets in field: "
                            + field + " of component: " + component);
        }
        final String assetPath = assetData.value()[0];
        if (assetData.loadOnDemand()) {
            // Loaded immediately.
            @SuppressWarnings("unchecked") final Object asset = finishLoading(assetPath, field.getType());
            try {
                Reflection.setFieldValue(field, component, asset);
            } catch (final ReflectionException exception) {
                throw new GdxRuntimeException("Unable to inject asset loaded on demand.", exception);
            }
        } else {
            load(assetPath, field.getType());
            // Scheduled to be loaded, delayed injection.
            assetInjections.add(new StandardAssetInjection(field, assetPath, component));
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void handleArrayInjection(final Object component, final Field field, final Asset assetData) {
        if (assetData.loadOnDemand()) {
            try {
                Array assets = (Array) Reflection.getFieldValue(field, component);
                if (assets == null) {
                    assets = GdxArrays.newArray();
                }
                for (final String assetPath : assetData.value()) {
                    assets.add(finishLoading(assetPath, assetData.type()));
                }
                Reflection.setFieldValue(field, component, assets);
            } catch (final ReflectionException exception) {
                throw new GdxRuntimeException("Unable to inject array of assets into: " + component, exception);
            }
        } else {
            for (final String assetPath : assetData.value()) {
                load(assetPath, assetData.type());
            }
            // Scheduled to be loaded, delayed injection.
            assetInjections.add(new ArrayAssetInjection(assetData.value(), assetData.type(), field, component));
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void handleSetInjection(final Object component, final Field field, final Asset assetData) {
        if (assetData.loadOnDemand()) {
            try {
                ObjectSet assets = (ObjectSet) Reflection.getFieldValue(field, component);
                if (assets == null) {
                    assets = GdxSets.newSet();
                }
                for (final String assetPath : assetData.value()) {
                    assets.add(finishLoading(assetPath, assetData.type()));
                }
                Reflection.setFieldValue(field, component, assets);
            } catch (final ReflectionException exception) {
                throw new GdxRuntimeException("Unable to inject set of assets into: " + component, exception);
            }
        } else {
            for (final String assetPath : assetData.value()) {
                load(assetPath, assetData.type());
            }
            // Scheduled to be loaded, delayed injection.
            assetInjections.add(new ObjectSetAssetInjection(assetData.value(), assetData.type(), field, component));
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void handleMapInjection(final Object component, final Field field, final Asset assetData) {
        if (assetData.loadOnDemand()) {
            final String[] assetPaths = assetData.value();
            final String[] assetKeys = assetData.keys().length == 0 ? assetData.value() : assetData.keys();
            try {
                ObjectMap assets = (ObjectMap) Reflection.getFieldValue(field, component);
                if (assets == null) {
                    assets = GdxMaps.newObjectMap();
                }
                for (int assetIndex = 0; assetIndex < assetPaths.length; assetIndex++) {
                    assets.put(assetKeys[assetIndex], finishLoading(assetPaths[assetIndex], assetData.type()));
                }
                Reflection.setFieldValue(field, component, assets);
            } catch (final ReflectionException exception) {
                throw new GdxRuntimeException("Unable to inject array of assets into: " + component, exception);
            }
        } else {
            for (final String assetPath : assetData.value()) {
                load(assetPath, assetData.type());
            }
            // Scheduled to be loaded, delayed injection.
            assetInjections.add(new ObjectMapAssetInjection(assetData.value(), assetData.keys(), assetData.type(),
                    field, component));
        }
    }

    /** @param action will be executed after all currently scheduled assets are loaded. This requires an
     *            {@link #update()} or {@link #finishLoading()} call. */
    public void addOnLoadAction(final Runnable action) {
        onLoadActions.add(action);
    }
}
