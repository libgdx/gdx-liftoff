package com.github.czyzby.kiwi.util.gdx.collection.immutable;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Collections;

import java.util.Comparator;

/** An ordered or unordered array of objects. Semi-immutable. Extends LibGDX Array class, deprecating and throwing
 * UnsupportedOperationExceptions on all operations that mutate the array. It is not truly immutable, because the
 * original API is not properly encapsulated; although it does overshadow Array public variables to make them
 * unaccessible without casting. Use for constants and arrays that are not supposed to be changed. Also, avoiding Array
 * public fields is advised.
 *
 * @author Nathan Sweet
 * @author MJ */
@SuppressWarnings("hiding")
public class ImmutableArray<Type> extends Array<Type> {
    // Hiding public Array variables, making it harder to manually modify array values.
    private final int size;
    private final boolean ordered;
    @SuppressWarnings("unused") private final Type[] items;

    private ArrayIterable<Type> iterable;

    /** Creates a new ordered, immutable array containing the elements in the specified array. The new array will have
     * the same type of backing array.
     *
     * @param array will be used. */
    public ImmutableArray(final Type[] array) {
        super(array);
    }

    /** Creates a new immutable array containing the elements in the specified array. The new array will have the same
     * type of backing array and will be ordered if the specified array is ordered.
     *
     * @param array will be copied. */
    public ImmutableArray(final Array<? extends Type> array) {
        super(array);
    }

    /** Creates a new array containing the elements in the specified array. The new array will have the same type of
     * backing array.
     *
     * @param ordered true if should be ordered.
     * @param array will be used.
     * @param start starting index.
     * @param count elements amount. */
    public ImmutableArray(final boolean ordered, final Type[] array, final int start, final int count) {
        super(ordered, array, start, count);
    }

    {
        this.size = super.size;
        this.ordered = super.ordered;
        this.items = super.items;
    }

    /** @param values will be wrapped.
     * @return a new ImmutableArray containing the passed objects.
     * @param <Type> type of stored values. */
    public static <Type> ImmutableArray<Type> of(final Type... values) {
        return new ImmutableArray<Type>(values);
    }

    /** @param values will be wrapped.
     * @return a new ImmutableArray containing the passed objects.
     * @param <Type> type of stored values. */
    public static <Type> ImmutableArray<Type> with(final Type... values) {
        return of(values);
    }

    /** @param array will be copied.
     * @return a new ImmutableArray created using the passed array.
     * @param <Type> type of stored values. */
    public static <Type> ImmutableArray<Type> copyOf(final Array<? extends Type> array) {
        return new ImmutableArray<Type>(array);
    }

    /** @param values will be sorted and copied.
     * @return a new ImmutableArray containing the sorted passed objects.
     * @param <Type> type of stored values. */
    public static <Type extends Comparable<?>> ImmutableArray<Type> ofSorted(final Type... values) {
        return copyOfSorted(new Array<Type>(values));
    }

    /** @param array will be sorted and copied.
     * @return a new ImmutableArray created using the sorted passed array.
     * @param <Type> type of stored values. */
    public static <Type extends Comparable<?>> ImmutableArray<Type> copyOfSorted(final Array<? extends Type> array) {
        array.sort();
        return new ImmutableArray<Type>(array);
    }

    /** @param values will be appended to the array.
     * @return a new ImmutableArray with the passed values.
     * @param <Type> type of stored values. */
    public static <Type> ImmutableArray<Type> with(final Iterable<? extends Type> values) {
        final Array<Type> array = new Array<Type>();
        for (final Type value : values) {
            array.add(value);
        }
        return copyOf(array);
    }

    @Override
    @Deprecated
    public void add(final Type value) {
        throw new UnsupportedOperationException("Cannot modify ImmutableArray.");
    }

    @Override
    public void add(Type value1, Type value2) {
        throw new UnsupportedOperationException("Cannot modify ImmutableArray.");
    }

    @Override
    public void add(Type value1, Type value2, Type value3) {
        throw new UnsupportedOperationException("Cannot modify ImmutableArray.");
    }

    @Override
    public void add(Type value1, Type value2, Type value3, Type value4) {
        throw new UnsupportedOperationException("Cannot modify ImmutableArray.");
    }

    @Override
    @Deprecated
    public void addAll(final Array<? extends Type> array) {
        throw new UnsupportedOperationException("Cannot modify ImmutableArray.");
    }

    @Override
    @Deprecated
    public void addAll(final Type... array) {
        throw new UnsupportedOperationException("Cannot modify ImmutableArray.");
    }

    @Override
    @Deprecated
    public void addAll(final Array<? extends Type> array, final int start, final int count) {
        throw new UnsupportedOperationException("Cannot modify ImmutableArray.");
    }

    @Override
    @Deprecated
    public void addAll(final Type[] array, final int start, final int count) {
        throw new UnsupportedOperationException("Cannot modify ImmutableArray.");
    }

    @Override
    @Deprecated
    public void set(final int index, final Type value) {
        throw new UnsupportedOperationException("Cannot modify ImmutableArray.");
    }

    @Override
    @Deprecated
    public void insert(final int index, final Type value) {
        throw new UnsupportedOperationException("Cannot modify ImmutableArray.");
    }

    @Override
    @Deprecated
    public void swap(final int first, final int second) {
        throw new UnsupportedOperationException("Cannot modify ImmutableArray.");
    }

    @Override
    @Deprecated
    public boolean removeValue(final Type value, final boolean identity) {
        throw new UnsupportedOperationException("Cannot modify ImmutableArray.");
    }

    @Override
    @Deprecated
    public Type removeIndex(final int index) {
        throw new UnsupportedOperationException("Cannot modify ImmutableArray.");
    }

    @Override
    @Deprecated
    public void removeRange(final int start, final int end) {
        throw new UnsupportedOperationException("Cannot modify ImmutableArray.");
    }

    @Override
    @Deprecated
    public boolean removeAll(final Array<? extends Type> array, final boolean identity) {
        throw new UnsupportedOperationException("Cannot modify ImmutableArray.");
    }

    @Override
    @Deprecated
    public Type pop() {
        throw new UnsupportedOperationException("Cannot modify ImmutableArray.");
    }

    @Override
    @Deprecated
    public void clear() {
        throw new UnsupportedOperationException("Cannot modify ImmutableArray.");
    }

    @Override
    @Deprecated
    public Type[] shrink() {
        throw new UnsupportedOperationException("Cannot modify ImmutableArray.");
    }

    @Override
    @Deprecated
    public Type[] ensureCapacity(final int additionalCapacity) {
        return super.ensureCapacity(additionalCapacity);
    }

    @Override
    @Deprecated
    public void sort() {
        throw new UnsupportedOperationException("Cannot modify ImmutableArray.");
    }

    @Override
    @Deprecated
    public void sort(final Comparator<? super Type> comparator) {
        throw new UnsupportedOperationException("Cannot modify ImmutableArray.");
    }

    @Override
    @Deprecated
    public Type selectRanked(final Comparator<Type> comparator, final int kthLowest) {
        // Might partially sort the array.
        throw new UnsupportedOperationException("Cannot modify ImmutableArray.");
    }

    @Override
    @Deprecated
    public int selectRankedIndex(final Comparator<Type> comparator, final int kthLowest) {
        // Might partially sort the array.
        throw new UnsupportedOperationException("Cannot modify ImmutableArray.");
    }

    @Override
    @Deprecated
    public void reverse() {
        throw new UnsupportedOperationException("Cannot modify ImmutableArray.");
    }

    @Override
    @Deprecated
    public void shuffle() {
        throw new UnsupportedOperationException("Cannot modify ImmutableArray.");
    }

    @Override
    @Deprecated
    public void truncate(final int arg0) {
        throw new UnsupportedOperationException("Cannot modify ImmutableArray.");
    }

    /** @return original amount of elements in the array. */
    public int size() {
        return size;
    }

    /** @return true if the array was ordered when created. */
    public boolean isOrdered() {
        return ordered;
    }

    @Override
    public ArrayIterator<Type> iterator() {
        if (Collections.allocateIterators) return new ArrayIterator<Type>(this, false);
        if (iterable == null) iterable = new ArrayIterable<Type>(this, false);
        return iterable.iterator();
    }
}