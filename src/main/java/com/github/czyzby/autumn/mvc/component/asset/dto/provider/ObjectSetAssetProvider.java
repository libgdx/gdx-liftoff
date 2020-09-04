package com.github.czyzby.autumn.mvc.component.asset.dto.provider;

import com.badlogic.gdx.utils.ObjectSet;
import com.github.czyzby.autumn.mvc.component.asset.AssetService;
import com.github.czyzby.kiwi.util.gdx.asset.lazy.provider.ObjectProvider;
import com.github.czyzby.kiwi.util.gdx.collection.GdxSets;

/** Asset provider for set injections.
 *
 * @author MJ */
public class ObjectSetAssetProvider implements ObjectProvider<ObjectSet<Object>> {
    private final AssetService assetService;
    private final String[] assetPaths;
    private final Class<?> assetClass;
    private final boolean loadOnDemand;

    public ObjectSetAssetProvider(final AssetService assetService, final String assetPaths[], final Class<?> assetClass,
            final boolean loadOnDemand) {
        this.assetService = assetService;
        this.assetPaths = assetPaths;
        this.assetClass = assetClass;
        this.loadOnDemand = loadOnDemand;
    }

    @Override
    public ObjectSet<Object> provide() {
        final ObjectSet<Object> assets = GdxSets.newSet();
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
