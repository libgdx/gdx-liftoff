package com.github.czyzby.kiwi.util.gdx.asset.lazy.provider;

/** Utility funtional interface. Provides an object. Mimics Java 8 Supplier interface.
 *
 * @author MJ
 * @param <Type> type of produced objects. */
public interface ObjectProvider<Type> {
    /** @return an instance of an object with the selected type. */
    Type provide();
}
