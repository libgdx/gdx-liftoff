package com.github.czyzby.kiwi.util.gdx.collection.disposable;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/** A resizable, ordered or unordered array of objects. If unordered, this class avoids a memory copy when removing
 * elements (the last element is moved to the removed element's position). <br>
 * <br>
 * Utility container for disposable objects.
 *
 * @author Nathan Sweet
 * @author MJ */
public class DisposableArray<Type extends Disposable> extends Array<Type>implements Disposable {
    /** Creates an ordered array with a capacity of 16. */
    public DisposableArray() {
        super();
    }

    /** Creates an ordered array with the specified capacity.
     *
     * @param capacity any elements added beyond this will cause the backing array to be grown. */
    public DisposableArray(final int capacity) {
        super(capacity);
    }

    /** @param ordered if false, methods that remove elements may change the order of other elements in the array, which
     *            avoids a memory copy.
     * @param capacity any elements added beyond this will cause the backing array to be grown. */
    public DisposableArray(final boolean ordered, final int capacity) {
        super(ordered, capacity);
    }

    /** Creates a new array with {@link #items} of the specified type.
     *
     * @param ordered If false, methods that remove elements may change the order of other elements in the array, which
     *            avoids a memory copy.
     * @param capacity Any elements added beyond this will cause the backing array to be grown.
     * @param arrayType type of stored values. */
    public DisposableArray(final boolean ordered, final int capacity, final Class<?> arrayType) {
        super(ordered, capacity, arrayType);
    }

    /** Creates an ordered array with {@link #items} of the specified type and a capacity of 16.
     *
     * @param arrayType type of stored values. */
    public DisposableArray(final Class<?> arrayType) {
        super(arrayType);
    }

    /** Creates a new array containing the elements in the specified array. The new array will have the same type of
     * backing array and will be ordered if the specified array is ordered. The capacity is set to the number of
     * elements, so any subsequent elements added will cause the backing array to be grown.
     *
     * @param array will be copied. */
    public DisposableArray(final Array<? extends Type> array) {
        super(array);
    }

    /** Creates a new ordered array containing the elements in the specified array. The new array will have the same
     * type of backing array. The capacity is set to the number of elements, so any subsequent elements added will cause
     * the backing array to be grown.
     *
     * @param array will be used. */
    public DisposableArray(final Type[] array) {
        super(array);
    }

    /** Creates a new array containing the elements in the specified array. The new array will have the same type of
     * backing array. The capacity is set to the number of elements, so any subsequent elements added will cause the
     * backing array to be grown.
     *
     * @param ordered If false, methods that remove elements may change the order of other elements in the array, which
     *            avoids a memory copy.
     * @param array will be used.
     * @param start starting index.
     * @param count elements amount. */
    public DisposableArray(final boolean ordered, final Type[] array, final int start, final int count) {
        super(ordered, array, start, count);
    }

    /** @return a new instance of disposable array.
     * @param <Type> type of stored values. */
    public static <Type extends Disposable> DisposableArray<Type> newArray() {
        return new DisposableArray<Type>();
    }

    /** @param values will be used.
     * @return a new DisposableArray containing the passed objects.
     * @param <Type> type of stored values. */
    public static <Type extends Disposable> DisposableArray<Type> of(final Type... values) {
        return new DisposableArray<Type>(values);
    }

    /** @param values will be used.
     * @return a new DisposableArray containing the passed objects.
     * @param <Type> type of stored values. */
    public static <Type extends Disposable> DisposableArray<Type> with(final Type... values) {
        return of(values);
    }

    /** @param array will be copied.
     * @return a new DisposableArray created using the passed array.
     * @param <Type> type of stored values. */
    public static <Type extends Disposable> DisposableArray<Type> copyOf(final Array<? extends Type> array) {
        return new DisposableArray<Type>(array);
    }

    /** @param values will be appended to the array.
     * @return a new DisposableArray with the passed values.
     * @param <Type> type of stored values. */
    public static <Type extends Disposable> DisposableArray<Type> with(final Iterable<? extends Type> values) {
        final DisposableArray<Type> array = new DisposableArray<Type>();
        for (final Type value : values) {
            array.add(value);
        }
        return array;
    }

    /** @param forClass type of stored values.
     * @param values will be appended to the array.
     * @return a new typed DisposableArray with the passed values.
     * @param <Type> type of stored values. */
    public static <Type extends Disposable> DisposableArray<Type> with(final Class<Type> forClass,
            final Iterable<? extends Type> values) {
        final DisposableArray<Type> array = new DisposableArray<Type>(forClass);
        for (final Type value : values) {
            array.add(value);
        }
        return array;
    }

    /** @return current amount of elements in the array. */
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