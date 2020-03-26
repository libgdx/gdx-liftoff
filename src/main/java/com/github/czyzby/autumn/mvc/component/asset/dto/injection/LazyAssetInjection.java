package com.github.czyzby.autumn.mvc.component.asset.dto.injection;

import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.czyzby.autumn.mvc.component.asset.AssetService;
import com.github.czyzby.kiwi.util.gdx.asset.lazy.Lazy;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;

/** Delayed injection for assets wrapped with a lazy container.
 *
 * @author MJ */
public class LazyAssetInjection extends StandardAssetInjection {
    private final Class<?> assetClass;

    public LazyAssetInjection(final Field field, final String assetPath, final Object component,
            final Class<?> assetClass) {
        super(field, assetPath, component);
        this.assetClass = assetClass;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void injectAsset(final AssetService assetService) throws ReflectionException {
        final Lazy lazyAsset = (Lazy) Reflection.getFieldValue(field, component);
        if (!lazyAsset.isInitialized()) {
            lazyAsset.set(assetService.get(assetPath, assetClass));
        }
    }
}