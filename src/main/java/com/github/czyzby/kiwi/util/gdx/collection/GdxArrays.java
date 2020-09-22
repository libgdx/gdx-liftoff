package com.github.czyzby.kiwi.util.gdx.collection;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.SnapshotArray;
import com.github.czyzby.kiwi.util.common.UtilitiesClass;
import com.github.czyzby.kiwi.util.gdx.asset.lazy.provider.ObjectProvider;
import com.github.czyzby.kiwi.util.gdx.collection.disposable.DisposableArray;
import com.github.czyzby.kiwi.util.gdx.collection.immutable.ImmutableArray;
import com.github.czyzby.kiwi.util.gdx.collection.lazy.LazyArray;

/** Common {@link Array} utilities, somewhat inspired by Guava.
 *
 * @author MJ */
public class GdxArrays extends UtilitiesClass {
    private GdxArrays() {
    }

    /** @return a new, empty Array.
     * @param <Type> type of stored elements. */
    public static <Type> Array<Type> newArray() {
        return new Array<Type>();
    }

    /** @param initialCapacity initial capacity of the array. Will be resized if needed.
     * @return a new, empty Array.
     * @param <Type> type of stored elements. */
    public static <Type> Array<Type> newArray(final int initialCapacity) {
        return new Array<Type>(initialCapacity);
    }

    /** @param forClass class of stored elements.
     * @return a new, empty typed Array.
     * @param <Type> type of stored elements. */
    public static <Type> Array<Type> newArray(final Class<Type> forClass) {
        return new Array<Type>(forClass);
    }

    /** @return a new, empty SnapshotArray.
     * @param <Type> type of stored elements. */
    public static <Type> SnapshotArray<Type> newSnapshotArray() {
        return new SnapshotArray<Type>();
    }

    /** @param forClass class of stored elements.
     * @return a new, empty typed SnapshotArray.
     * @param <Type> type of stored elements. */
    public static <Type> SnapshotArray<Type> newSnapshotArray(final Class<Type> forClass) {
        return new SnapshotArray<Type>(forClass);
    }

    /** @return a new, empty DelayedRemovalArray.
     * @param <Type> type of stored elements. */
    public static <Type> DelayedRemovalArray<Type> newDelayedRemovalArray() {
        return new DelayedRemovalArray<Type>();
    }

    /** @param forClass class of stored elements.
     * @return a new, empty typed DelayedRemovalArray.
     * @param <Type> type of stored elements. */
    public static <Type> DelayedRemovalArray<Type> newDelayedRemovalArray(final Class<Type> forClass) {
        return new DelayedRemovalArray<Type>(forClass);
    }

    /** @param values will be appended to the array.
     * @return a new Array with the passed values.
     * @param <Type> type of stored elements. */
    public static <Type> Array<Type> newArray(final Type... values) {
        return new Array<Type>(values);
    }

    /** @param values will be appended to the array.
     * @return a new Array with the passed values.
     * @param <Type> type of stored elements. */
    public static <Type> Array<Type> newArray(final Iterable<? extends Type> values) {
        final Array<Type> array = new Array<Type>();
        for (final Type value : values) {
            array.add(value);
        }
        return array;
    }

    /** @param forClass class of stored objects.
     * @param values will be appended to the array.
     * @return a new typed Array with the passed values.
     * @param <Type> type of stored elements. */
    public static <Type> Array<Type> newArray(final Class<Type> forClass, final Iterable<? extends Type> values) {
        final Array<Type> array = new Array<Type>(forClass);
        for (final Type value : values) {
            array.add(value);
        }
        return array;
    }

    /** @param values will be appended to the array.
     * @return a new SnapshotArray with the passed values.
     * @param <Type> type of stored elements. */
    public static <Type> SnapshotArray<Type> newSnapshotArray(final Type... values) {
        return new SnapshotArray<Type>(values);
    }

    /** @param values will be appended to the array.
     * @return a new SnapshotArray with the passed values.
     * @param <Type> type of stored elements. */
    public static <Type> SnapshotArray<Type> newSnapshotArray(final Iterable<? extends Type> values) {
        final SnapshotArray<Type> array = new SnapshotArray<Type>();
        for (final Type value : values) {
            array.add(value);
        }
        return array;
    }

    /** @param forClass class of stored elements.
     * @param values will be appended to the array.
     * @return a new typed SnapshotArray with the passed values.
     * @param <Type> type of stored elements. */
    public static <Type> SnapshotArray<Type> newSnapshotArray(final Class<Type> forClass,
            final Iterable<? extends Type> values) {
        final SnapshotArray<Type> array = new SnapshotArray<Type>(forClass);
        for (final Type value : values) {
            array.add(value);
        }
        return array;
    }

    /** @param values will be appended to the array.
     * @return a new DelayedRemovalArray with the passed values.
     * @param <Type> type of stored elements. */
    public static <Type> DelayedRemovalArray<Type> newDelayedRemovalArray(final Type... values) {
        return new DelayedRemovalArray<Type>(values);
    }

    /** @param values will be appended to the array.
     * @return a new DelayedRemovalArray with the passed values.
     * @param <Type> type of stored elements. */
    public static <Type> DelayedRemovalArray<Type> newDelayedRemovalArray(final Iterable<? extends Type> values) {
        final DelayedRemovalArray<Type> array = new DelayedRemovalArray<Type>();
        for (final Type value : values) {
            array.add(value);
        }
        return array;
    }

    /** @param forClass class of stored elements.
     * @param values will be appended to the array.
     * @return a new typed DelayedRemovalArray with the passed values.
     * @param <Type> type of stored elements. */
    public static <Type> DelayedRemovalArray<Type> newDelayedRemovalArray(final Class<Type> forClass,
            final Iterable<? extends Type> values) {
        final DelayedRemovalArray<Type> array = new DelayedRemovalArray<Type>(forClass);
        for (final Type value : values) {
            array.add(value);
        }
        return array;
    }

    /** @param array will be copied.
     * @return a new disposable array made using passed array values.
     * @param <Type> type of stored elements. */
    public static <Type extends Disposable> DisposableArray<Type> toDisposable(final Array<? extends Type> array) {
        return new DisposableArray<Type>(array);
    }

    /** @param array will be copied.
     * @return a new semi-immutable array made using passed array values.
     * @param <Type> type of stored elements. */
    public static <Type> ImmutableArray<Type> toImmutable(final Array<? extends Type> array) {
        return new ImmutableArray<Type>(array);
    }

    /** @param array will be copied.
     * @return a new snapshot array created with the passed array values.
     * @param <Type> type of stored elements. */
    public static <Type> SnapshotArray<Type> toSnapshot(final Array<? extends Type> array) {
        return new SnapshotArray<Type>(array);
    }

    /** @param array will be copied.
     * @return a new delayed removal array created with the passed array values.
     * @param <Type> type of stored elements. */
    public static <Type> DelayedRemovalArray<Type> toDelayedRemoval(final Array<? extends Type> array) {
        return new DelayedRemovalArray<Type>(array);
    }

    /** @param array will be copied.
     * @param provider creates new object on get(index) calls if the value on the selected index is null.
     * @return a new lazy array created with the passed array values.
     * @param <Type> type of stored elements. */
    public static <Type> LazyArray<Type> toLazy(final ObjectProvider<? extends Type> provider,
            final Array<? extends Type> array) {
        return new LazyArray<Type>(provider, array);
    }

    /** @param array can be null.
     * @return true if array is null or has no elements. */
    public static boolean isEmpty(final Array<?> array) {
        return array == null || array.size == 0;
    }

    /** @param array can be null.
     * @return true if array is not null and has at least one element. */
    public static boolean isNotEmpty(final Array<?> array) {
        return array != null && array.size > 0;
    }

    /** @param arrays will be checked. Can be null.
     * @return the biggest size among the passed arrays. 0 if all arrays are empty or null. */
    public static int getBiggestSize(final Array<?>... arrays) {
        int maxSize = 0;
        for (final Array<?> array : arrays) {
            if (array != null) {
                maxSize = Math.max(maxSize, array.size);
            }
        }
        return maxSize;
    }

    /** @param arrays will be checked. Can be null.
     * @return the biggest size among the passed arrays. 0 if all arrays are empty or null. */
    public static int getBiggestSize(final Iterable<Array<?>> arrays) {
        int maxSize = 0;
        for (final Array<?> array : arrays) {
            if (array != null) {
                maxSize = Math.max(maxSize, array.size);
            }
        }
        return maxSize;
    }

    /** @param array cannot be null.
     * @param index will be checked.
     * @return true if the given index is last in the passed array. */
    public static boolean isIndexLast(final Array<?> array, final int index) {
        return array.size - 1 == index;
    }

    /** @param array cannot be null.
     * @param index will be checked.
     * @return true if the given index is valid for the passed array. */
    public static boolean isIndexValid(final Array<?> array, final int index) {
        return index >= 0 && index < array.size;
    }

    /** @param array can be null.
     * @return null (if array is empty or last stored value was null) or last stored value.
     * @param <Type> type of stored values. */
    public static <Type> Type removeLast(final Array<? extends Type> array) {
        if (isEmpty(array)) {
            return null;
        }
        return array.removeIndex(array.size - 1);
    }

    /** @param array can be null.
     * @return last stored value in the array if it is not empty or null.
     * @param <Type> type of stored values. */
    public static <Type> Type getLast(final Array<? extends Type> array) {
        if (isEmpty(array)) {
            return null;
        }
        return array.get(array.size - 1);
    }

    /** @param intArray can be null.
     * @return last stored int in the array if it is not empty or 0. */
    public static int getLast(final IntArray intArray) {
        if (isEmpty(intArray)) {
            return 0;
        }
        return intArray.get(intArray.size - 1);
    }

    /** @param intArray can be null.*
     * @return true if the passed array is empty. */
    public static boolean isEmpty(final IntArray intArray) {
        return intArray == null || intArray.size == 0;
    }

    /** @param arrays will be joined.
     * @return a new array with values from all passed arrays. Duplicates are added multiple times.
     * @param <Type> type of stored elements. */
    public static <Type> Array<Type> union(final Array<Type>... arrays) {
        return unionTo(new Array<Type>(), arrays);
    }

    /** @param ofClass class of stored elements.
     * @param arrays will be joined.
     * @return a new typed array with values from all passed arrays. Duplicates are added multiple times.
     * @param <Type> type of stored elements. */
    public static <Type> Array<Type> union(final Class<Type> ofClass, final Array<Type>... arrays) {
        return unionTo(new Array<Type>(ofClass), arrays);
    }

    /** @param array will contain passed arrays.
     * @param arrays will be joined.
     * @return first argument array with values added from all passed arrays. Duplicates are added multiple times.
     * @param <Type> type of stored elements. */
    public static <Type> Array<Type> unionTo(final Array<Type> array, final Array<Type>... arrays) {
        if (arrays == null || arrays.length == 0) {
            return array;
        }
        for (final Array<Type> unionedArray : arrays) {
            array.addAll(unionedArray);
        }
        return array;
    }

    /** @param arrays will all be cleared, with an additional null-check before the clearing. */
    public static void clearAll(final Array<?>... arrays) {
        for (final Array<?> array : arrays) {
            if (array != null) {
                array.clear();
            }
        }
    }

    /** @param arrays all contained arrays will all be cleared, with an additional null-check before the clearing. */
    public static void clearAll(final Iterable<Array<?>> arrays) {
        for (final Array<?> array : arrays) {
            if (array != null) {
                array.clear();
            }
        }
    }

    /** Static utility for accessing {@link Array#size} variable (which is kind of ugly, since it allows to easily
     * modify and damage internal collection data). Performs null check.
     *
     * @param array its size will be checked. Can be null.
     * @return current size of the passed array. 0 if array is empty or null. */
    public static int sizeOf(final Array<?> array) {
        if (array == null) {
            return 0;
        }
        return array.size;
    }
}