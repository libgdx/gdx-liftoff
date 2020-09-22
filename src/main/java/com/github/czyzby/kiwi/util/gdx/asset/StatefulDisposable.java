package com.github.czyzby.kiwi.util.gdx.asset;

import com.badlogic.gdx.utils.Disposable;

/** Extends disposable interface to provide a utility check method if the resource was already disposed of.
 *
 * @author MJ */
public interface StatefulDisposable extends Disposable {
    /** @return true if the resource was already disposed of. If returns true and {@link #dispose()} is called, some
     *         resources might throw an exception or ignore the invocation. */
    boolean isDisposed();
}
