package com.github.czyzby.autumn.mvc.component.asset.dto.injection;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.czyzby.autumn.mvc.component.asset.AssetService;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;

/** Handles delayed asset injection into {@link ObjectMap} field.
 *
 * @author MJ */
public class ObjectMapAssetInjection implements AssetInjection {
    private final String[] assetPaths;
    private final String[] assetKeys;
    private final Class<?> assetType;
    private final Field field;
    private final Object component;

    public ObjectMapAssetInjection(final String[] assetPaths, final String[] assetKeys, final Class<?> assetType,
            final Field field, final Object component) {
        this.assetPaths = assetPaths;
        this.assetKeys = assetKeys.length == 0 ? assetPaths : assetKeys;
        this.assetType = assetType;
        this.field = field;
        this.component = component;
    }

    @Override
    public boolean inject(final AssetService assetService) {
        for (final String assetPath : assetPaths) {
            if (!assetService.isLoaded(assetPath)) {
                return false;
            }
        }
        injectAssets(assetService);
        return true;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void injectAssets(final AssetService assetService) {
        try {
            ObjectMap map = (ObjectMap) Reflection.getFieldValue(field, component);
            if (map == null) {
                map = GdxMaps.newObjectMap();
            }
            for (int assetIndex = 0; assetIndex < assetPaths.length; assetIndex++) {
                map.put(assetKeys[assetIndex], assetService.get(assetPaths[assetIndex], assetType));
            }
            Reflection.setFieldValue(field, component, map);
        } catch (final ReflectionException exception) {
            throw new GdxRuntimeException("Unable to inject map of assets into component: " + component + ".",
                    exception);
        }
    }

    @Override
    public void fillScheduledAssets(final ObjectSet<String> scheduledAssets) {
        for (final String assetPath : assetPaths) {
            scheduledAssets.add(assetPath);
        }
    }

    @Override
    public void removeScheduledAssets(final ObjectSet<String> scheduledAssets) {
        for (final String assetPath : assetPaths) {
            scheduledAssets.remove(assetPath);
        }
    }
}
