package com.github.czyzby.kiwi.util.tuple.mutable;

import java.util.Iterator;

import com.github.czyzby.kiwi.util.common.Nullables;
import com.github.czyzby.kiwi.util.tuple.DoubleTuple;
import com.github.czyzby.kiwi.util.tuple.TripleTuple;
import com.github.czyzby.kiwi.util.tuple.Tuples;
import com.github.czyzby.kiwi.util.tuple.immutable.Pair;
import com.github.czyzby.kiwi.util.tuple.immutable.Triple;

/** Double tuple. Stores two variables. Mutable utility container for all these cases that two variables have to be
 * returned by a function or otherwise passed and stored together. Implements Map.Entry using first variable as key and
 * second as value for additional utility.
 *
 * @author MJ */
public class MutablePair<First, Second> implements DoubleTuple<First, Second> {
    private static final long serialVersionUID = 1L;

    private First first;
    private Second second;

    public MutablePair(final First first, final Second second) {
        this.first = first;
        this.second = second;
    }

    /** @param first can be null.
     * @param second can be null.
     * @return a new pair with the passed variables. Accepts nulls.
     * @param <First> type of first value.
     * @param <Second> type of second value. */
    public static <First, Second> MutablePair<First, Second> of(final First first, final Second second) {
        return new MutablePair<First, Second>(first, second);
    }

    /** @param first cannot be null.
     * @param second cannot be null.
     * @return a new pair with the passed variables.
     * @throws NullPointerException if any of the variables are null.
     * @param <First> type of first value.
     * @param <Second> type of second value. */
    public static <First, Second> MutablePair<First, Second> ofNonNull(final First first, final Second second)
            throws NullPointerException {
        if (first == null || second == null) {
            throw new NullPointerException("Tried to construct non-nullable pair with null value.");
        }
        return new MutablePair<First, Second>(first, second);
    }

    /** @param pair will be inverted.
     * @return a new pair with inverted values.
     * @param <First> type of first value.
     * @param <Second> type of second value. */
    public static <First, Second> MutablePair<Second, First> invert(final MutablePair<First, Second> pair) {
        return new MutablePair<Second, First>(pair.getSecond(), pair.getFirst());
    }

    @Override
    public First getFirst() {
        return first;
    }

    /** @param first will become first variable stored in pair. */
    public void setFirst(final First first) {
        this.first = first;
    }

    @Override
    public boolean isFirstPresent() {
        return first != null;
    }

    @Override
    public Second getSecond() {
        return second;
    }

    /** @param second will become second variable stored in pair. Equivalent of setValue. */
    public void setSecond(final Second second) {
        this.second = second;
    }

    @Override
    public boolean isSecondPresent() {
        return second != null;
    }

    @Override
    public MutablePair<Second, First> invert() {
        return of(second, first);
    }

    /** @return new immutable Pair constructed with this pair. */
    public Pair<First, Second> toImmutable() {
        return Pair.of(first, second);
    }

    @Override
    public First getKey() {
        return first;
    }

    @Override
    public Second getValue() {
        return second;
    }

    @Override
    public Second setValue(final Second value) {
        second = value;
        return second;
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
    public <Third> Triple<First, Second, Third> toImmutableTriplet(final Third third) {
        return Triple.of(first, second, third);
    }

    /** @param third will be set as third triplet's value.
     * @return a new MutableTriplet constructed with this pair's values and another passed variable.
     * @param <Third> type of third value. */
    public <Third> MutableTriple<First, Second, Third> toTriplet(final Third third) {
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
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void set(final int index, final Object value) {
        if (index == 0) {
            first = (First) value;
        } else if (index == 1) {
            second = (Second) value;
        } else {
            throw new IndexOutOfBoundsException("Invalid index passed to pair: " + index + ".");
        }
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
