package com.github.czyzby.kiwi.util.gdx.asset.lazy;

import com.badlogic.gdx.utils.Disposable;
import com.github.czyzby.kiwi.util.gdx.asset.lazy.provider.ObjectProvider;

/** Wraps around an object, allowing to have a final reference to a lazy-initialized object. Adds a very small overhead,
 * without the usual boilerplate that lazy objects require. Should be used for objects that are expensive to create and
 * rarely (or - at least - not always) needed to ensure that they are created only when necessary. Concurrent use might
 * result in multiple provider method calls. Holds a disposable object and implements disposable interface for extra
 * utility - dispose on the wrapped object will be called only if the object was created.
 *
 * @author MJ
 * @param <Type> wrapped object type. */
public class DisposableLazy<Type extends Disposable> extends Lazy<Type>implements Disposable {
    /** Constructs an empty lazy object with no provider. Stored variable has to be set manually. */
    public DisposableLazy() {
        super();
    }

    /** @param provider will provide wrapped object on first call. */
    public DisposableLazy(final ObjectProvider<? extends Type> provider) {
        super(provider);
    }

    @Override
    public void dispose() {
        if (getObject() != null) {
            getObject().dispose();
        }
    }

    @Override
    public String toString() {
        return "DisposableLazy[" + getObject() + "]";
    }
}
