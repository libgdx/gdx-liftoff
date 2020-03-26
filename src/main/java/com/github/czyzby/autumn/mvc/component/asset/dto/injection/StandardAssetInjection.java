package com.github.czyzby.autumn.mvc.component.asset.dto.injection;

import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.czyzby.autumn.mvc.component.asset.AssetService;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;

/** Delayed asset injection container.
 *
 * @author MJ */
public class StandardAssetInjection implements AssetInjection {
    protected final Field field;
    protected final String assetPath;
    protected final Object component;

    public StandardAssetInjection(final Field field, final String assetPath, final Object component) {
        this.field = field;
        this.assetPath = assetPath;
        this.component = component;
    }

    @Override
    public boolean inject(final AssetService assetService) {
        if (assetService.isLoaded(assetPath)) {
            try {
                injectAsset(assetService);
                return true;
            } catch (final ReflectionException exception) {
                throw new GdxRuntimeException("Unable to inject asset into component: " + component + ".", exception);
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    protected void injectAsset(final AssetService assetService) throws ReflectionException {
        Reflection.setFieldValue(field, component, assetService.get(assetPath, field.getType()));
    }

    @Override
    public void fillScheduledAssets(final ObjectSet<String> scheduledAssets) {
        scheduledAssets.add(assetPath);
    }

    @Override
    public void removeScheduledAssets(final ObjectSet<String> scheduledAssets) {
        scheduledAssets.remove(assetPath);
    }
}