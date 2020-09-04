package com.github.czyzby.autumn.mvc.component.asset.dto.provider;

import com.badlogic.gdx.utils.Array;
import com.github.czyzby.autumn.mvc.component.asset.AssetService;
import com.github.czyzby.kiwi.util.gdx.asset.lazy.provider.ObjectProvider;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;

/** Asset provider for array injections.
 *
 * @author MJ */
public class ArrayAssetProvider implements ObjectProvider<Array<Object>> {
    private final AssetService assetService;
    private final String[] assetPaths;
    private final Class<?> assetClass;
    private final boolean loadOnDemand;

    public ArrayAssetProvider(final AssetService assetService, final String assetPaths[], final Class<?> assetClass,
            final boolean loadOnDemand) {
        this.assetService = assetService;
        this.assetPaths = assetPaths;
        this.assetClass = assetClass;
        this.loadOnDemand = loadOnDemand;
    }

    @Override
    public Array<Object> provide() {
        final Array<Object> assets = GdxArrays.newArray();
        for (final String assetPath : assetPaths) {
            if (loadOnDemand) {
                assets.add(assetService.finishLoading(assetPath, assetClass));
                continue;
            }
            if (!assetService.isLoaded(assetPath)) {
                // libGDX method that should load a specific asset immediately does pretty much the same.
                assetService.finishLoading();
            }
            assets.add(assetService.get(assetPath, assetClass));
        }
        return assets;
    }
}
