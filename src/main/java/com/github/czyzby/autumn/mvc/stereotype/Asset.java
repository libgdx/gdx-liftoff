package com.github.czyzby.autumn.mvc.stereotype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Marks an internal asset that should be loaded by the
 * {@link com.github.czyzby.autumn.mvc.component.asset.AssetService}.
 *
 * @author MJ */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Asset {
    /** @return internal paths to the asset. Cannot be empty. If multiple assets are given, field type has to be
     *         {@link com.badlogic.gdx.utils.ObjectMap}, {@link com.badlogic.gdx.utils.Array} or
     *         {@link com.badlogic.gdx.utils.ObjectSet}. */
    String[]value();

    /** @return asset IDs. Used only if the assets are stored in a {@link com.badlogic.gdx.utils.ObjectMap}. If used,
     *         keys array size has to match {@link #value()} array size. If not specified, paths will be used as map
     *         keys instead. */
    String[]keys() default {};

    /** @return class of the asset. Ignored and taken from the variable type if asset is not wrapped in
     *         {@link com.github.czyzby.kiwi.util.gdx.asset.lazy.Lazy} or libGDX collection container. */
    Class<?>type() default void.class;

    /** Determines the behavior of asset loading. 4 states are possible:
     *
     * <ul>
     * <li>false: asset will be scheduled to be loaded by the
     * {@link com.github.czyzby.autumn.mvc.component.asset.AssetService}. As soon as it is loaded, it will be injected
     * to the variable. Note that if it is accessed before the loading is done, it might be null.</li>
     *
     * <li>true: asset will be loaded immediately and injected to the field when the component is initiated by the
     * context. Should be generally avoided for big assets.</li>
     *
     * <li>false, asset wrapped in {@link com.github.czyzby.kiwi.util.gdx.asset.lazy.Lazy}: asset will be scheduled to
     * be loaded and injected to the lazy variable as soon as its done. If
     * {@link com.github.czyzby.kiwi.util.gdx.asset.lazy.Lazy#get()} is called before the asset is loaded, it will wait
     * until the asset is loaded and extract it from the
     * {@link com.github.czyzby.autumn.mvc.component.asset.AssetService}. This is a safer variant of the first option.
     * </li>
     *
     * <li>true, asset wrapped in {@link com.github.czyzby.kiwi.util.gdx.asset.lazy.Lazy}: asset will be loaded
     * immediately after the first {@link com.github.czyzby.kiwi.util.gdx.asset.lazy.Lazy#get()} call. Should be avoided
     * for heavy assets that might block the thread for too long.</li>
     * </ul>
     *
     * For even more complex asset loading behaviors, use
     * {@link com.github.czyzby.autumn.mvc.component.asset.AssetService} directly.
     *
     * @return defaults to false. */
    boolean loadOnDemand() default false;

    /** @return type of collection stored in a {@link com.github.czyzby.kiwi.util.gdx.asset.lazy.Lazy} wrapper. This
     *         setting is needed only if the annotated field is a Lazy that stores a libGDX collection of assets. Only
     *         {@link com.badlogic.gdx.utils.ObjectMap}, {@link com.badlogic.gdx.utils.Array} or
     *         {@link com.badlogic.gdx.utils.ObjectSet} are accepted. */
    Class<?>lazyCollection() default void.class;
}
