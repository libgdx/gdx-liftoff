package com.github.czyzby.kiwi.util.gdx.asset;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;

/** Contains path, class and some basic information of a single asset. Meant to be represented by an enum - asset
 * management utility.
 *
 * @author MJ */
public interface Asset {
    /** @return location at which the resource should be present. */
    public String getPath();

    /** @return class of the object that should be created using the loaded asset. */
    public Class<?> getAssetClass();

    /** @return file handle created with the assets path. */
    public FileHandle getFileHandle();

    /** @return type of the asset, used by AbstractAssetManager to filter assets that need to be loaded. */
    public AssetType getAssetType();

    /** @param withManager will schedule loading of this asset. */
    public void load(AssetManager withManager);

    /** @param fromManager has to contain the loaded asset.
     * @return asset represented by this container. */
    public Object get(AssetManager fromManager);

    /** @param withType class of the loaded asset.
     * @param fromManager has to contain the loaded asset.
     * @return asset represented by this container.
     * @param <Type> type of stored asset. */
    public <Type> Type get(Class<Type> withType, AssetManager fromManager);
}
