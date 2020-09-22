package com.github.czyzby.kiwi.util.gdx.scene2d;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.czyzby.kiwi.util.common.UtilitiesClass;
import com.github.czyzby.kiwi.util.gdx.asset.Asset;
import com.github.czyzby.kiwi.util.gdx.asset.Disposables;

/** Allows for static access to application's main interface's {@link Skin}. Utility container.
 *
 * @author MJ */
public class InterfaceSkin extends UtilitiesClass {
    private InterfaceSkin() {
    }

    private static Skin INTERFACE_STYLE;

    /** @return application's main interface style. Defaults to null. */
    public static Skin get() {
        return INTERFACE_STYLE;
    }

    /** @param interfaceStyle will become application's main interface style. Can be accessed with {@link #get()}
     *            method. */
    public static void set(final Skin interfaceStyle) {
        INTERFACE_STYLE = interfaceStyle;
    }

    /** @param skinAsset will be used to load a {@link Skin} that will be set as the default interface style. */
    public static void load(final Asset skinAsset) {
        load(skinAsset.getFileHandle());
    }

    /** @param file will be used to load a {@link Skin} that will be set as the default interface style. */
    public static void load(final FileHandle file) {
        INTERFACE_STYLE = new Skin(file);
    }

    /** @param itemName name of the item as it appears in the interface style Skin.
     * @param itemClass desired class of the item.
     * @return item with selected name and class (if present).
     * @throws GdxRuntimeException if skin is not initiated.
     * @param <Item> expected type of item. */
    public static <Item> Item extractItem(final String itemName, final Class<Item> itemClass) {
        if (INTERFACE_STYLE == null) {
            throw new GdxRuntimeException("Interface skin is not set.");
        }
        return INTERFACE_STYLE.get(itemName, itemClass);
    }

    /** Destroys the currently set interface style skin. Null safe. */
    public static void dispose() {
        Disposables.disposeOf(INTERFACE_STYLE);
    }
}