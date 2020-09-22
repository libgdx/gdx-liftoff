package com.github.czyzby.kiwi.util.gdx.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;

/** Although Assets are advised to be kept in enums (which cannot extend, since they already do), a sample
 * implementation is provided. It can be copied to the enum implementing the asset interface.
 *
 * @author MJ */
public class AbstractInternalAsset implements Asset {
    private final String path;
    private final Class<?> assetClass;
    private final AssetType assetType;

    public AbstractInternalAsset(final String path, final Class<?> assetClass, final AssetType assetType) {
        this.path = path;
        this.assetClass = assetClass;
        this.assetType = assetType;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Class<?> getAssetClass() {
        return assetClass;
    }

    @Override
    public AssetType getAssetType() {
        return assetType;
    }

    @Override
    public FileHandle getFileHandle() {
        return Gdx.files.internal(path);
    }

    @Override
    public void load(final AssetManager withManager) {
        withManager.load(path, assetClass);
    }

    @Override
    public Object get(final AssetManager fromManager) {
        return fromManager.get(path);
    }

    @Override
    public <Type> Type get(final Class<Type> withType, final AssetManager fromManager) {
        return fromManager.get(path, withType);
    }

    @Override
    public boolean equals(final Object object) {
        return object == this || object instanceof Asset && ((Asset) object).getPath().equals(path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }
}
