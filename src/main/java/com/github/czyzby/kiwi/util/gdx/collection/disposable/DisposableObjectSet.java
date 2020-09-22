package com.github.czyzby.kiwi.util.gdx.collection.disposable;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectSet;

/** An unordered set where the keys are objects. This implementation uses cuckoo hashing using 3 hashes, random walking,
 * and a small stash for problematic keys. Null keys are not allowed. No allocation is done except when growing the
 * table size. <br>
 * <br>
 * This set performs very fast contains and remove (typically O(1), worst case O(log(n))). Add may be a bit slower,
 * depending on hash collisions. Load factors greater than 0.91 greatly increase the chances the set will have to rehash
 * to the next higher POT size. <br>
 * <br>
 * Utility container for disposable objects.
 *
 * @author Nathan Sweet
 * @author MJ */
public class DisposableObjectSet<Type extends Disposable> extends ObjectSet<Type>implements Disposable {
    /** Creates a new set with an initial capacity of 32 and a load factor of 0.8. This set will hold 25 items before
     * growing the backing table. */
    public DisposableObjectSet() {
        super();
    }

    /** Creates a new set with a load factor of 0.8. This set will hold initialCapacity * 0.8 items before growing the
     * backing table.
     *
     * @param initialCapacity initial expected amount of elements. */
    public DisposableObjectSet(final int initialCapacity) {
        super(initialCapacity);
    }

    /** Creates a new set with the specified initial capacity and load factor. This set will hold initialCapacity *
     * loadFactor items before growing the backing table.
     *
     * @param initialCapacity initial expected amount of elements.
     * @param loadFactor determines how fast set is grown. */
    public DisposableObjectSet(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /** Creates a new set identical to the specified set.
     *
     * @param set will be copied. */
    public DisposableObjectSet(final ObjectSet<? extends Type> set) {
        super(set);
    }

    /** @return a new instance of disposable set.
     * @param <Type> type of stored values. */
    public static <Type extends Disposable> DisposableObjectSet<Type> newSet() {
        return new DisposableObjectSet<Type>();
    }

    /** @param disposables will be copied.
     * @return a new DisposableObjectSet with the passed values.
     * @param <Type> type of stored values. */
    public static <Type extends Disposable> DisposableObjectSet<Type> of(final Type... disposables) {
        final DisposableObjectSet<Type> set = new DisposableObjectSet<Type>(disposables.length);
        for (final Type disposable : disposables) {
            set.add(disposable);
        }
        return set;
    }

    /** @param disposables will be copied.
     * @return a new DisposableObjectSet with the passed values.
     * @param <Type> type of stored values. */
    public static <Type extends Disposable> DisposableObjectSet<Type> with(final Type... disposables) {
        return of(disposables);
    }

    /** @param objectSet will be copied.
     * @return a new DisposableObjectSet with the values specified in the passed set.
     * @param <Type> type of stored values. */
    public static <Type extends Disposable> DisposableObjectSet<Type> copyOf(
            final ObjectSet<? extends Type> objectSet) {
        return new DisposableObjectSet<Type>(objectSet);
    }

    /** @return amount of elements in the set. */
    public int size() {
        return super.size;
    }

    @Override
    public void dispose() {
        for (final Disposable disposable : this) {
            if (disposable != null) {
                disposable.dispose();
            }
        }
    }
}