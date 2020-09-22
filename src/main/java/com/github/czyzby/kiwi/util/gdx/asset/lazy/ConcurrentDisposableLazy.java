package com.github.czyzby.kiwi.util.gdx.asset.lazy;

import com.badlogic.gdx.utils.Disposable;
import com.github.czyzby.kiwi.util.gdx.asset.lazy.provider.ObjectProvider;

/** Wraps around an object, allowing to have a final reference to a lazy-initialized object. Adds a very small overhead,
 * without the usual boilerplate that lazy objects require. Should be used for objects that are expensive to create and
 * rarely (or - at least - not always) needed to ensure that they are created only when necessary. Thread-safe - only
 * one object instance is extracted from provider, even in concurrent use. Holds a disposable object and implements
 * disposable interface for extra utility - dispose on the wrapped object will be called only if the object was created.
 * Note that this class extends DisposableLazy rather than ConcurrentLazy.
 *
 * @author MJ
 * @param <Type> wrapped object type. */
public class ConcurrentDisposableLazy<Type extends Disposable> extends DisposableLazy<Type> {
    /** Constructs an empty lazy object with no provider. Stored variable has to be set manually. */
    public ConcurrentDisposableLazy() {
        super();
    }

    /** @param provider will provide wrapped object on first call. */
    public ConcurrentDisposableLazy(final ObjectProvider<? extends Type> provider) {
        super(provider);
    }

    @Override
    protected Type getObjectInstance() {
        synchronized (this) {
            if (getObject() == null) {
                validateProvider();
                return getProvider().provide();
            }
            return getObject();
        }
    }

    @Override
    public void set(final Type object) throws IllegalStateException {
        if (getObject() == null) {
            synchronized (this) {
                if (getObject() == null) {
                    super.set(object);
                } else {
                    throw new IllegalStateException("Cannot set lazy variable - already initiated.");
                }
                return;
            }
        }
        throw new IllegalStateException("Cannot set lazy variable - already initiated.");
    }

    @Override
    public String toString() {
        return "ConcurrentDisposableLazy[" + getObject() + "]";
    }
}
