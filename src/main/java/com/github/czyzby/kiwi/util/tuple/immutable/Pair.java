package com.github.czyzby.kiwi.util.tuple.immutable;

import java.util.Iterator;

import com.github.czyzby.kiwi.util.common.Nullables;
import com.github.czyzby.kiwi.util.tuple.DoubleTuple;
import com.github.czyzby.kiwi.util.tuple.TripleTuple;
import com.github.czyzby.kiwi.util.tuple.Tuples;
import com.github.czyzby.kiwi.util.tuple.mutable.MutablePair;
import com.github.czyzby.kiwi.util.tuple.mutable.MutableTriple;

/** Double tuple. Stores two variables. Immutable utility container for all these cases that two variables have to be
 * returned by a function or otherwise passed and stored together. Implements Map.Entry using first variable as key and
 * second as value for additional utility, although it doesn't allow changing its value with setValue (throws
 * AssertionError).
 *
 * @author MJ */
public class Pair<First, Second> implements DoubleTuple<First, Second> {
    private static final long serialVersionUID = 1L;

    private final First first;
    private final Second second;

    public Pair(final First first, final Second second) {
        this.first = first;
        this.second = second;
    }

    /** @param first first tuple value. Can be null.
     * @param second second tuple value. Can be null.
     * @return a new pair with the passed variables. Accepts nulls.
     * @param <First> type of first value.
     * @param <Second> type of second value. */
    public static <First, Second> Pair<First, Second> of(final First first, final Second second) {
        return new Pair<First, Second>(first, second);
    }

    /** @param first first tuple value.
     * @param second second tuple value.
     * @return a new pair with the passed variables.
     * @throws NullPointerException if any of the variables are null.
     * @param <First> type of first value.
     * @param <Second> type of second value. */
    public static <First, Second> Pair<First, Second> ofNonNull(final First first, final Second second)
            throws NullPointerException {
        if (first == null || second == null) {
            throw new NullPointerException("Tried to construct non-nullable pair with null value.");
        }
        return new Pair<First, Second>(first, second);
    }

    /** @param pair will be inverted.
     * @return a new pair with inverted values.
     * @param <First> type of first value.
     * @param <Second> type of second value. */
    public static <First, Second> Pair<Second, First> invert(final Pair<First, Second> pair) {
        return new Pair<Second, First>(pair.getSecond(), pair.getFirst());
    }

    @Override
    public First getFirst() {
        return first;
    }

    @Override
    public boolean isFirstPresent() {
        return first != null;
    }

    @Override
    public Second getSecond() {
        return second;
    }

    @Override
    public boolean isSecondPresent() {
        return second != null;
    }

    @Override
    public Pair<Second, First> invert() {
        return of(second, first);
    }

    /** @return new mutable pair constructed with this pair. */
    public MutablePair<First, Second> toMutable() {
        return MutablePair.of(first, second);
    }

    @Override
    public First getKey() {
        return first;
    }

    @Override
    public Second getValue() {
        return second;
    }

    /** @param value ignored.
     * @throws UnsupportedOperationException on each call. Pair is immutable. */
    @Override
    @Deprecated
    public Second setValue(final Second value) {
        throw new UnsupportedOperationException("Pair cannot be modified.");
    }

    @Override
    public Object get(final int index) throws IndexOutOfBoundsException {
        if (index == 0) {
            return first;
        }
        if (index == 1) {
            return second;
        }
        throw new IndexOutOfBoundsException("Invalid index passed to pair: " + index + ".");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Type> Type get(final int index, final Class<Type> asInstanceOf) throws IndexOutOfBoundsException {
        // Generally a little bit faster than a switch.
        if (index == 0) {
            return (Type) first;
        }
        if (index == 1) {
            return (Type) second;
        }
        throw new IndexOutOfBoundsException("Invalid index passed to pair: " + index + ".");
    }

    /** @param third will be set as third triplet's value.
     * @return a new immutable Triplet constructed with this pair's values and another passed variable.
     * @param <Third> type of third value. */
    public <Third> Triple<First, Second, Third> toTriplet(final Third third) {
        return Triple.of(first, second, third);
    }

    /** @param third will be set as third triplet's value.
     * @return a new MutableTriplet constructed with this pair's values and another passed variable.
     * @param <Third> type of third value. */
    public <Third> MutableTriple<First, Second, Third> toMutableTriplet(final Third third) {
        return MutableTriple.of(first, second, third);
    }

    @Override
    public <Third> TripleTuple<First, Second, Third> toTripleTuple(final Third third) {
        return toTriplet(third);
    }

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        if (first != null) {
            hash = first.hashCode();
        }
        if (second != null) {
            hash ^= second.hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(final Object object) {
        return object == this
                || object instanceof DoubleTuple && Nullables.areEqual(first, ((DoubleTuple<?, ?>) object).getFirst())
                        && Nullables.areEqual(second, ((DoubleTuple<?, ?>) object).getSecond());
    }

    @Override
    public String toString() {
        return first + COMMA_WITH_SPACE_SEPARATOR + second;
    }

    @Override
    public Iterator<Object> iterator() {
        return Tuples.getTupleIterator(this);
    }

    @Override
    public boolean contains(final Object value) {
        return Nullables.areEqual(value, first) || Nullables.areEqual(value, second);
    }

    @Override
    public boolean containsAll(final Object... values) {
        for (final Object value : values) {
            if (!contains(value)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAll(final Iterable<?> values) {
        for (final Object value : values) {
            if (!contains(value)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAny(final Object... values) {
        for (final Object value : values) {
            if (contains(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAny(final Iterable<?> values) {
        for (final Object value : values) {
            if (contains(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int indexOf(final Object value) {
        return Nullables.areEqual(value, first) ? 0 : Nullables.areEqual(value, second) ? 1 : INVALID_INDEX;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    /** @param index ignored.
     * @param value ignored.
     * @throws UnsupportedOperationException on each call. Pair is immutable. */
    @Override
    @Deprecated
    public void set(final int index, final Object value) {
        throw new UnsupportedOperationException("Pair cannot be modified.");
    }

    @Override
    public Object[] toArray() {
        return new Object[] { first, second };
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Type> Type[] toArray(final Type[] array) {
        array[0] = (Type) first;
        array[1] = (Type) second;
        return array;
    }

    @Override
    public <Type> Iterator<Type> iterator(final Class<Type> forClass) {
        return Tuples.getTupleIterator(this);
    }
}
