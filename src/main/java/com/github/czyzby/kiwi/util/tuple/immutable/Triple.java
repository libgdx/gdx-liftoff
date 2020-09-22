package com.github.czyzby.kiwi.util.tuple.immutable;

import java.util.Iterator;

import com.github.czyzby.kiwi.util.common.Nullables;
import com.github.czyzby.kiwi.util.tuple.TripleTuple;
import com.github.czyzby.kiwi.util.tuple.Tuples;
import com.github.czyzby.kiwi.util.tuple.mutable.MutableTriple;

/** Triple tuple. Stores three variables. Immutable utility container for all these cases that three variables have to
 * be returned by a function or otherwise passed and stored together. Implements Map.Entry using first variable as key
 * and second as value for additional utility, although it obviously doesn't store just 2 values. Additionally, it
 * doesn't allow changing its value with setValue (throws AssertionError).
 *
 * @author MJ */
public class Triple<First, Second, Third> implements TripleTuple<First, Second, Third> {
    private static final long serialVersionUID = 1L;

    private final First first;
    private final Second second;
    private final Third third;

    public Triple(final First first, final Second second, final Third third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    /** @param first can be null.
     * @param second can be null.
     * @param third can be null.
     * @return a new triplet with the passed variables. Accepts nulls.
     * @param <First> type of first value.
     * @param <Second> type of second value.
     * @param <Third> type of third value. */
    public static <First, Second, Third> Triple<First, Second, Third> of(final First first, final Second second,
            final Third third) {
        return new Triple<First, Second, Third>(first, second, third);
    }

    /** @param first cannot be null.
     * @param second cannot be null.
     * @param third cannot be null.
     * @return a new triplet with the passed variables.
     * @throws NullPointerException if any of the variables are null.
     * @param <First> type of first value.
     * @param <Second> type of second value.
     * @param <Third> type of third value. */
    public static <First, Second, Third> Triple<First, Second, Third> ofNonNull(final First first, final Second second,
            final Third third) throws NullPointerException {
        if (first == null || second == null || third == null) {
            throw new NullPointerException("Tried to construct non-nullable triplet with null value.");
        }
        return new Triple<First, Second, Third>(first, second, third);
    }

    /** @param triplet will be inverted.
     * @return a new triplet with inverted first and third values.
     * @param <First> type of first value.
     * @param <Second> type of second value.
     * @param <Third> type of third value. */
    public static <First, Second, Third> Triple<Third, Second, First> invert(
            final Triple<First, Second, Third> triplet) {
        return new Triple<Third, Second, First>(triplet.getThird(), triplet.getSecond(), triplet.getFirst());
    }

    /** @param triplet will be shifted left.
     * @return a new triplet with values shifted left.
     * @param <First> type of first value.
     * @param <Second> type of second value.
     * @param <Third> type of third value. */
    public static <First, Second, Third> Triple<Second, Third, First> shiftLeft(
            final Triple<First, Second, Third> triplet) {
        return new Triple<Second, Third, First>(triplet.getSecond(), triplet.getThird(), triplet.getFirst());
    }

    /** @param triplet will be shifted right.
     * @return a new triplet with values shifted right.
     * @param <First> type of first value.
     * @param <Second> type of second value.
     * @param <Third> type of third value. */
    public static <First, Second, Third> Triple<Third, First, Second> shiftRight(
            final Triple<First, Second, Third> triplet) {
        return new Triple<Third, First, Second>(triplet.getThird(), triplet.getFirst(), triplet.getSecond());
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
    public Third getThird() {
        return third;
    }

    @Override
    public boolean isThirdPresent() {
        return second != null;
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
    @Deprecated
    public Second setValue(final Second value) {
        throw new UnsupportedOperationException("Triple cannot be modified.");
    }

    @Override
    public Object get(final int index) throws IndexOutOfBoundsException {
        // Generally a little bit faster than a switch.
        if (index == 0) {
            return first;
        }
        if (index == 1) {
            return second;
        }
        if (index == 2) {
            return third;
        }
        throw new IndexOutOfBoundsException("Invalid index passed to triplet: " + index + ".");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Type> Type get(final int index, final Class<Type> asInstanceOf) throws IndexOutOfBoundsException {
        // Generally a little bit faster than switch.
        if (index == 0) {
            return (Type) first;
        }
        if (index == 1) {
            return (Type) second;
        }
        if (index == 2) {
            return (Type) third;
        }
        throw new IndexOutOfBoundsException("Invalid index passed to triplet: " + index + ".");
    }

    @Override
    public Triple<Third, Second, First> invert() {
        return of(third, second, first);
    }

    @Override
    public Triple<Second, Third, First> shiftLeft() {
        return of(second, third, first);
    }

    @Override
    public Triple<Third, First, Second> shitfRight() {
        return of(third, first, second);
    }

    /** @return a new instance of MutableTriplet constructed with this triplet's values. */
    public MutableTriple<First, Second, Third> toMutable() {
        return MutableTriple.of(first, second, third);
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
        if (third != null) {
            hash ^= third.hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(final Object object) {
        return object == this || object instanceof TripleTuple
                && Nullables.areEqual(first, ((TripleTuple<?, ?, ?>) object).getFirst())
                && Nullables.areEqual(second, ((TripleTuple<?, ?, ?>) object).getSecond())
                && Nullables.areEqual(third, ((TripleTuple<?, ?, ?>) object).getThird());
    }

    @Override
    public String toString() {
        return first + COMMA_WITH_SPACE_SEPARATOR + second + COMMA_WITH_SPACE_SEPARATOR + third;
    }

    @Override
    public Iterator<Object> iterator() {
        return Tuples.getTupleIterator(this);
    }

    @Override
    public boolean contains(final Object value) {
        return Nullables.areEqual(value, first) || Nullables.areEqual(value, second)
                || Nullables.areEqual(value, third);
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
        return Nullables.areEqual(value, first) ? 0
                : Nullables.areEqual(value, second) ? 1 : Nullables.areEqual(value, third) ? 2 : INVALID_INDEX;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    @Deprecated
    public void set(final int index, final Object value) {
        throw new UnsupportedOperationException("Triple cannot be modified.");
    }

    @Override
    public Object[] toArray() {
        return new Object[] { first, second, third };
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Type> Type[] toArray(final Type[] array) {
        array[0] = (Type) first;
        array[1] = (Type) second;
        array[2] = (Type) third;
        return array;
    }

    @Override
    public <Type> Iterator<Type> iterator(final Class<Type> forClass) {
        return Tuples.getTupleIterator(this);
    }
}