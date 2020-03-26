package com.github.czyzby.autumn.mvc.component.asset.dto.injection;

import com.badlogic.gdx.utils.ObjectSet;
import com.github.czyzby.autumn.mvc.component.asset.AssetService;

/** Allows for delayed injection.
 *
 * @author MJ */
public interface AssetInjection {
    /** Injects the value of annotated field.
     *
     * @param assetService provides the asset.
     * @return true if asset was injected. */
    public boolean inject(AssetService assetService);

    /** @param scheduledAssets will contain handled asset paths. */
    public void fillScheduledAssets(ObjectSet<String> scheduledAssets);

    /** @param scheduledAssets will have handled asset paths removed. */
    public void removeScheduledAssets(ObjectSet<String> scheduledAssets);
}