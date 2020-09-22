package com.github.czyzby.kiwi.util.tuple.mutable;

import java.util.Iterator;

import com.github.czyzby.kiwi.util.common.Nullables;
import com.github.czyzby.kiwi.util.tuple.SingleTuple;
import com.github.czyzby.kiwi.util.tuple.Tuples;
import com.github.czyzby.kiwi.util.tuple.immutable.Single;

/** Single tuple. Stores one variable. Mutable, iterable utility container for a single value - can be used to pass a
 * single variable when an iterable is required and expected to be somehow modified.
 *
 * @author MJ */
public class MutableSingle<Type> implements SingleTuple<Type> {
    private static final long serialVersionUID = 1L;

    private Type value;

    public MutableSingle(final Type value) {
        this.value = value;
    }

    /** @param value can be null.
     * @return a new MutableSingle holding the passed value.
     * @param <Type> type of stored value. */
    public static <Type> MutableSingle<Type> of(final Type value) {
        return new MutableSingle<Type>(value);
    }

    /** @param value cannot be null.
     * @return a new MutableSingle holding the passed non-null value.
     * @param <Type> type of stored value.
     * @throws NullPointerException if value is null. */
    public static <Type> MutableSingle<Type> ofNonNull(final Type value) {
        if (value == null) {
            throw new NullPointerException("Cannot construct non-null single with null value.");
        }
        return new MutableSingle<Type>(value);
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
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void set(final int index, final Object value) {
        if (index == 0) {
            this.value = (Type) value;
            return;
        }
        throw new IndexOutOfBoundsException("Invalid index passed to single: " + index + ".");
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

    /** @param value will be wrapped by the single object. */
    public void setFirst(final Type value) {
        this.value = value;
    }

    @Override
    public Type get() {
        return value;
    }

    /** @param value will be wrapped by the single object. */
    public void set(final Type value) {
        this.value = value;
    }

    @Override
    public boolean isFirstPresent() {
        return value != null;
    }

    @Override
    public boolean isPresent() {
        return value != null;
    }

    /** @return an immutable Single holding the same value. */
    public Single<Type> toImmutable() {
        return Single.of(value);
    }
}
