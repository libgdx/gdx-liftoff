package com.github.czyzby.kiwi.util.tuple.immutable;

import java.util.Iterator;

import com.github.czyzby.kiwi.util.common.Nullables;
import com.github.czyzby.kiwi.util.tuple.SingleTuple;
import com.github.czyzby.kiwi.util.tuple.Tuples;
import com.github.czyzby.kiwi.util.tuple.mutable.MutableSingle;

/** Single tuple. Stores one variable. Immutable, iterable utility container for a single value - can be used to pass a
 * single variable when an iterable is required. Throws exceptions on mutating operations.
 *
 * @author MJ */
public class Single<Type> implements SingleTuple<Type> {
    private static final long serialVersionUID = 1L;

    private final Type value;

    public Single(final Type value) {
        this.value = value;
    }

    /** @param value will be wrapped. Can be null.
     * @return a new immutable Single holding the passed value.
     * @param <Type> type of stored value. */
    public static <Type> Single<Type> of(final Type value) {
        return new Single<Type>(value);
    }

    /** @param value cannot be null.
     * @return a new immutable Single holding the passed non-null value.
     * @throws NullPointerException if value is null.
     * @param <Type> type of stored value. */
    public static <Type> Single<Type> ofNonNull(final Type value) {
        if (value == null) {
            throw new NullPointerException("Cannot construct non-null single with null value.");
        }
        return new Single<Type>(value);
    }

    @Override
    public Object get(final int index) throws IndexOutOfBoundsException {
        if (index == 0) {
            return value;
        }
        throw new IndexOutOfBoundsException("Invalid index passed to single: " + index + ".");
    }

    @Override
    @Deprecated
    @SuppressWarnings("unchecked")
    public <ValueType> ValueType get(final int index, final Class<ValueType> asInstanceOf)
            throws IndexOutOfBoundsException {
        if (index == 0) {
            return (ValueType) value;
        }
        throw new IndexOutOfBoundsException("Invalid index passed to single: " + index + ".");
    }

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public boolean contains(final Object value) {
        return Nullables.areEqual(this.value, value);
    }

    @Override
    public boolean containsAll(final Object... values) {
        for (final Object value : values) {
            if (Nullables.areNotEqual(this.value, value)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAll(final Iterable<?> values) {
        for (final Object value : values) {
            if (Nullables.areNotEqual(this.value, value)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAny(final Object... values) {
        for (final Object value : values) {
            if (Nullables.areEqual(this.value, value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAny(final Iterable<?> values) {
        for (final Object value : values) {
            if (Nullables.areEqual(this.value, value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int indexOf(final Object value) {
        if (contains(value)) {
            return 0;
        }
        return INVALID_INDEX;
    }

    @Override
    public Object[] toArray() {
        return new Object[] { value };
    }

    @Override
    @SuppressWarnings("unchecked")
    public <ValueType> ValueType[] toArray(final ValueType[] array) {
        array[0] = (ValueType) value;
        return array;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    @Deprecated
    public void set(final int index, final Object value) {
        throw new UnsupportedOperationException("Single cannot be modified.");
    }

    @Override
    @Deprecated
    public <ValueType> Iterator<ValueType> iterator(final Class<ValueType> forClass) {
        return Tuples.getTupleIterator(this);
    }

    @Override
    public Iterator<Type> iterator() {
        return Tuples.getTupleIterator(this);
    }

    @Override
    public Type getFirst() {
        return value;
    }

    @Override
    public Type get() {
        return value;
    }

    @Override
    public boolean isFirstPresent() {
        return value != null;
    }

    @Override
    public boolean isPresent() {
        return value != null;
    }

    /** @return a MutableSingle holding the same value. */
    public MutableSingle<Type> toMutable() {
        return MutableSingle.of(value);
    }
}
