package com.github.czyzby.kiwi.util.gdx.collection.immutable;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;

/** An unordered set where the keys are objects. This implementation uses cuckoo hashing using 3 hashes, random walking,
 * and a small stash for problematic keys. Null keys are not allowed. No allocation is done except when growing the
 * table size. <br>
 * <br>
 * This set performs very fast contains and remove (typically O(1), worst case O(log(n))). Add may be a bit slower,
 * depending on hash collisions. Load factors greater than 0.91 greatly increase the chances the set will have to rehash
 * to the next higher POT size. <br>
 * <br>
 * Semi-immutable. Contrary to Array, ObjectSet is better encapsulated and ImmutableObjectSet is an (almost) immutable
 * collection. The only way to modify the collection is getting the iterator manually and using its removing methods -
 * original iterator implementation operates on private fields (rather than public getters/modifying methods) and could
 * not have been rewritten. Still, standard usage of for-each loops is safe. Deprecates and throws
 * UnsupportedOperationExceptions on all operations that mutate the set.
 *
 * @author Nathan Sweet
 * @author MJ */
public class ImmutableObjectSet<Type> extends ObjectSet<Type> {
    @SuppressWarnings("hiding") private final int size;

    /** Creates a new set identical to the specified set.
     *
     * @param set will be copied. */
    public ImmutableObjectSet(final ObjectSet<? extends Type> set) {
        super(set);
        this.size = super.size;
    }

    /** @param values will be copied.
     * @return a new ImmutableObjectSet with the passed values.
     * @param <Type> type of stored values. */
    public static <Type> ImmutableObjectSet<Type> of(final Type... values) {
        final ObjectSet<Type> set = new ObjectSet<Type>(values.length);
        for (final Type value : values) {
            set.add(value);
        }
        return copyOf(set);
    }

    /** @param values will be copied.
     * @return a new ImmutableObjectSet with the passed values.
     * @param <Type> type of stored values. */
    public static <Type> ImmutableObjectSet<Type> with(final Type... values) {
        return of(values);
    }

    /** @param objectSet will be copied.
     * @return a new ImmutableObjectSet with the passed values.
     * @param <Type> type of stored values. */
    public static <Type> ImmutableObjectSet<Type> copyOf(final ObjectSet<? extends Type> objectSet) {
        return new ImmutableObjectSet<Type>(objectSet);
    }

    @Override
    @Deprecated
    public boolean add(final Type key) {
        throw new UnsupportedOperationException("Cannot modify ImmutableObjectSet.");
    }

    @Override
    @Deprecated
    public void addAll(final Array<? extends Type> array) {
        throw new UnsupportedOperationException("Cannot modify ImmutableObjectSet.");
    }

    @Override
    @Deprecated
    public void addAll(final Array<? extends Type> array, final int offset, final int length) {
        throw new UnsupportedOperationException("Cannot modify ImmutableObjectSet.");
    }

    @Override
    @Deprecated
    public boolean addAll(final Type... array) {
        throw new UnsupportedOperationException("Cannot modify ImmutableObjectSet.");
    }

    @Override
    @Deprecated
    public void addAll(final ObjectSet<Type> set) {
        throw new UnsupportedOperationException("Cannot modify ImmutableObjectSet.");
    }

    @Override
    @Deprecated
    public boolean addAll(final Type[] array, final int offset, final int length) {
        throw new UnsupportedOperationException("Cannot modify ImmutableObjectSet.");
    }

    @Override
    @Deprecated
    public boolean remove(final Type key) {
        throw new UnsupportedOperationException("Cannot modify ImmutableObjectSet.");
    }

    @Override
    @Deprecated
    public void shrink(final int maximumCapacity) {
        throw new UnsupportedOperationException("Cannot modify ImmutableObjectSet.");
    }

    @Override
    @Deprecated
    public void clear() {
        throw new UnsupportedOperationException("Cannot modify ImmutableObjectSet.");
    }

    @Override
    @Deprecated
    public void clear(final int maximumCapacity) {
        throw new UnsupportedOperationException("Cannot modify ImmutableObjectSet.");
    }

    @Override
    @Deprecated
    public void ensureCapacity(final int additionalCapacity) {
        super.ensureCapacity(additionalCapacity);
    }

    /** @return amount of elements in the set. */
    public int size() {
        return size;
    }
}