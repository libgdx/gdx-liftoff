package com.github.czyzby.kiwi.util.gdx.collection;

import java.util.Iterator;

import com.badlogic.gdx.utils.PooledLinkedList;
import com.badlogic.gdx.utils.SortedIntList;
import com.github.czyzby.kiwi.util.common.UtilitiesClass;
import com.github.czyzby.kiwi.util.gdx.collection.pooled.PooledList;

/** Utilities for LibGDX list collections: {@link PooledLinkedList} and {@link SortedIntList}.
 *
 * @author MJ
 * @see GdxArrays
 * @see PooledList
 * @since 1.4 */
public class GdxLists extends UtilitiesClass {
    private GdxLists() {
    }

    /** @param maxPoolSize max node pool size. If you want to limit creation of new objects as much as possible, this
     *            value should match expected max size.
     * @return new empty instance of {@link PooledLinkedList}.
     * @param <Type> type of stored values. */
    public static <Type> PooledLinkedList<Type> newList(final int maxPoolSize) {
        return new PooledLinkedList<Type>(maxPoolSize);
    }

    /** @param maxPoolSize max node pool size. If you want to limit creation of new objects as much as possible, this
     *            value should match expected max size.
     * @param elements will be added to the list.
     * @return new instance of {@link PooledLinkedList}.
     * @param <Type> type of stored values. */
    public static <Type> PooledLinkedList<Type> newList(final int maxPoolSize, final Type... elements) {
        final PooledLinkedList<Type> list = new PooledLinkedList<Type>(maxPoolSize);
        for (final Type element : elements) {
            list.add(element);
        }
        return list;
    }

    /** @return new empty instance of {@link SortedIntList}.
     * @param <Type> type of stored values. */
    public static <Type> SortedIntList<Type> newSortedList() {
        return new SortedIntList<Type>();
    }

    /** @param elements will be added to the list with ascending indexes, starting from 0.
     * @return new instance of {@link SortedIntList}.
     * @param <Type> type of stored values. */
    public static <Type> SortedIntList<Type> newSortedList(final Type... elements) {
        final SortedIntList<Type> list = new SortedIntList<Type>();
        for (int index = 0, size = elements.length; index < size; index++) {
            list.insert(index, elements[index]);
        }
        return list;
    }

    /** @param maxPoolSize max node pool size. If you want to limit creation of new objects as much as possible, this
     *            value should match expected max size.
     * @param iterable will be copied.
     * @return a new pooled linked list the the passes iterable's values.
     * @param <Type> type of stored values. */
    public static <Type> PooledLinkedList<Type> toPooledLinkedList(final int maxPoolSize,
            final Iterable<? extends Type> iterable) {
        final PooledLinkedList<Type> list = new PooledLinkedList<Type>(maxPoolSize);
        for (final Type value : iterable) {
            list.add(value);
        }
        return list;
    }

    /** @param iterable will be copied.
     * @return a new sorted int list with iterable's values inserted with ascending indexes, starting with 0.
     * @param <Type> type of stored values. */
    public static <Type> SortedIntList<Type> toSortedIntList(final Iterable<? extends Type> iterable) {
        final SortedIntList<Type> list = new SortedIntList<Type>();
        int index = 0;
        for (final Type value : iterable) {
            list.insert(index++, value);
        }
        return list;
    }

    /** @param list can be null.
     * @return true if list is null or empty. */
    public static boolean isEmpty(final SortedIntList<?> list) {
        return list == null || list.size() == 0;
    }

    /** @param list can be null.
     * @return true if list is null or empty. */
    public static boolean isEmpty(final PooledLinkedList<?> list) {
        return list == null || list.size() == 0;
    }

    /** @param list can be null.
     * @return true if list is not null and has elements. */
    public static boolean isNotEmpty(final SortedIntList<?> list) {
        return list != null && list.size() > 0;
    }

    /** @param list can be null.
     * @return true if list is not null and has elements. */
    public static boolean isNotEmpty(final PooledLinkedList<?> list) {
        return list != null && list.size() > 0;
    }

    /** Null-safe lists clearing method.
     *
     * @param lists will all be cleared. Any of these lists can be null. */
    public static void clear(final SortedIntList<?>... lists) {
        for (final SortedIntList<?> list : lists) {
            if (list != null) {
                list.clear();
            }
        }
    }

    /** Null-safe lists clearing method.
     *
     * @param lists will all be cleared. Any of these lists can be null. */
    public static void clear(final PooledLinkedList<?>... lists) {
        for (final PooledLinkedList<?> list : lists) {
            if (list != null) {
                list.clear();
            }
        }
    }

    /** @param list cannot be null.
     * @param values will be added to the passed list.
     * @param <Type> type of stored values. */
    public static <Type> void addAll(final PooledLinkedList<Type> list, final Type... values) {
        for (final Type value : values) {
            list.add(value);
        }
    }

    /** Since {@link PooledLinkedList} does not implement {@link Iterable} interface, a specialized iterator
     * implementation is provided: {@link PooledLinkedListIterator}. This class implements {@link Iterable} for extra
     * utility - it can be safely used in for-each loops. It can be also reused thanks to reset method (which is
     * automatically called when used in for-each loops. Removal is supported.
     *
     * <p>
     * While not validated, lists cannot be iterated over in nested loops or concurrently. New elements should not be
     * added during iteration and while appending might work during iteration, it can cause problems and unexpected
     * behaviors.
     *
     * @param list cannot be null.
     * @return new reusable {@link PooledLinkedListIterator}.
     * @param <Type> type of stored values. */
    public static <Type> PooledLinkedListIterator<Type> iterate(final PooledLinkedList<Type> list) {
        return new PooledLinkedListIterator<Type>(list);
    }

    /** Since {@link PooledLinkedList} does not implement {@link Iterable} interface, a specialized iterator
     * implementation is provided: {@link PooledLinkedListReversedIterator}. This class implements {@link Iterable} for
     * extra utility - it can be safely used in for-each loops. It can be also reused thanks to reset method (which is
     * automatically called when used in for-each loops. Removal is supported. Instead of iterating from head (first
     * element) to tail (last element), iteration is reversed and started with tail.
     *
     * <p>
     * While not validated, lists cannot be iterated over in nested loops or concurrently. New elements should not be
     * added during iteration and while appending might work during iteration, it can cause problems and unexpected
     * behaviors.
     *
     * @param list cannot be null.
     * @return new reusable {@link PooledLinkedListReversedIterator}.
     * @param <Type> type of stored values. */
    public static <Type> PooledLinkedListReversedIterator<Type> iterateReversed(final PooledLinkedList<Type> list) {
        return new PooledLinkedListReversedIterator<Type>(list);
    }

    /** Default iterator for {@link PooledLinkedList}. Can be cached and reused thanks to {@link #reset()} method.
     * Implements {@link Iterable} for extra utility - you can safely use this iterator in for-each loops (without
     * having to reset it - {@link #iterator()} method already takes care of that).
     *
     * @author MJ
     *
     * @param <Type> type of stored values. */
    public static class PooledLinkedListIterator<Type> implements Iterator<Type>, Iterable<Type> {
        private final PooledLinkedList<Type> list;
        private int currentIndex;

        /** @param list will be iterated over. Cannot be null. */
        public PooledLinkedListIterator(final PooledLinkedList<Type> list) {
            this.list = list;
            reset();
        }

        @Override
        public boolean hasNext() {
            return currentIndex < list.size();
        }

        @Override
        public Type next() {
            currentIndex += 1;
            return list.next();
        }

        @Override
        public void remove() {
            currentIndex -= 1;
            list.remove();
        }

        /** Restarts iteration from the list's head (first element). */
        public void reset() {
            list.iter();
            currentIndex = 0;
        }

        @Override
        public Iterator<Type> iterator() {
            reset();
            return this;
        }
    }

    /** Reversed iterator for {@link PooledLinkedList}. Can be cached and reused thanks to {@link #reset()} method.
     * Implements {@link Iterable} for extra utility - you can safely use this iterator in for-each loops (without
     * having to reset it - {@link #iterator()} method already takes care of that).
     *
     * @author MJ
     *
     * @param <Type> type of stored values. */
    public static class PooledLinkedListReversedIterator<Type> implements Iterator<Type>, Iterable<Type> {
        private final PooledLinkedList<Type> list;
        private int currentIndex;

        /** @param list will be iterated over. Cannot be null. */
        public PooledLinkedListReversedIterator(final PooledLinkedList<Type> list) {
            this.list = list;
            reset();
        }

        @Override
        public boolean hasNext() {
            return currentIndex >= 0;
        }

        @Override
        public Type next() {
            currentIndex -= 1;
            return list.previous();
        }

        @Override
        public void remove() {
            list.remove();
        }

        /** Restarts iteration from the list's tail (last element). */
        public void reset() {
            list.iterReverse();
            currentIndex = list.size() - 1;
        }

        @Override
        public Iterator<Type> iterator() {
            reset();
            return this;
        }
    }
}
