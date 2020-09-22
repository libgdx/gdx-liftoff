package com.github.czyzby.kiwi.util.gdx.collection.lazy;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.github.czyzby.kiwi.util.gdx.asset.lazy.provider.ArrayObjectProvider;
import com.github.czyzby.kiwi.util.gdx.asset.lazy.provider.MapObjectProvider;
import com.github.czyzby.kiwi.util.gdx.asset.lazy.provider.ObjectProvider;
import com.github.czyzby.kiwi.util.gdx.asset.lazy.provider.SetObjectProvider;

/** A resizable, ordered or unordered array of objects. If unordered, this class avoids a memory copy when removing
 * elements (the last element is moved to the removed element's position). Thanks to the provider, if the value
 * connected with the given index is null, a new object is created, put into the array and returned. This is especially
 * useful for arrays of other collections, that are often created in a very similar way and without the need of varying
 * constructor parameters.
 *
 * @author Nathan Sweet
 * @author MJ */
public class LazyArray<Type> extends Array<Type> {
    private ObjectProvider<? extends Type> provider;

    /** Creates an ordered array with a capacity of 16.
     *
     * @param provider creates new object on get(index) calls if the value on the selected index is null. */
    public LazyArray(final ObjectProvider<? extends Type> provider) {
        super();
        this.provider = provider;
    }

    /** Creates an ordered array with the specified capacity.
     *
     * @param provider creates new object on get(index) calls if the value on the selected index is null.
     * @param capacity initial expected amount of elements. */
    public LazyArray(final ObjectProvider<? extends Type> provider, final int capacity) {
        super(capacity);
        this.provider = provider;
    }

    /** @param ordered If false, methods that remove elements may change the order of other elements in the array, which
     *            avoids a memory copy.
     * @param capacity Any elements added beyond this will cause the backing array to be grown.
     * @param provider creates new object on get(index) calls if the value on the selected index is null. */
    public LazyArray(final ObjectProvider<? extends Type> provider, final boolean ordered, final int capacity) {
        super(ordered, capacity);
        this.provider = provider;
    }

    /** Creates a new array with {@link #items} of the specified type.
     *
     * @param ordered If false, methods that remove elements may change the order of other elements in the array, which
     *            avoids a memory copy.
     * @param capacity Any elements added beyond this will cause the backing array to be grown.
     * @param provider creates new object on get(index) calls if the value on the selected index is null.
     * @param arrayType type of stored values. */
    public LazyArray(final ObjectProvider<? extends Type> provider, final boolean ordered, final int capacity,
            final Class<Type> arrayType) {
        super(ordered, capacity, arrayType);
        this.provider = provider;
    }

    /** Creates an ordered array with {@link #items} of the specified type and a capacity of 16.
     *
     * @param provider creates new object on get(index) calls if the value on the selected index is null.
     * @param arrayType type of stored values. */
    public LazyArray(final ObjectProvider<? extends Type> provider, final Class<Type> arrayType) {
        super(arrayType);
        this.provider = provider;
    }

    /** Creates a new array containing the elements in the specified array. The new array will have the same type of
     * backing array and will be ordered if the specified array is ordered. The capacity is set to the number of
     * elements, so any subsequent elements added will cause the backing array to be grown.
     *
     * @param provider creates new object on get(index) calls if the value on the selected index is null.
     * @param array will be copied. */
    public LazyArray(final ObjectProvider<? extends Type> provider, final Array<? extends Type> array) {
        super(array);
        this.provider = provider;
    }

    /** Creates a new array containing the elements in the specified array. The new array will have the same type of
     * backing array and will be ordered if the specified array is ordered. The capacity is set to the number of
     * elements, so any subsequent elements added will cause the backing array to be grown.
     *
     * @param array will be copied. */
    public LazyArray(final LazyArray<? extends Type> array) {
        super(array);
        this.provider = array.provider;
    }

    /** Creates a new ordered array containing the elements in the specified array. The new array will have the same
     * type of backing array. The capacity is set to the number of elements, so any subsequent elements added will cause
     * the backing array to be grown.
     *
     * @param provider creates new object on get(index) calls if the value on the selected index is null.
     * @param array will be used. */
    public LazyArray(final ObjectProvider<? extends Type> provider, final Type[] array) {
        super(array);
        this.provider = provider;
    }

    /** Creates a new array containing the elements in the specified array. The new array will have the same type of
     * backing array. The capacity is set to the number of elements, so any subsequent elements added will cause the
     * backing array to be grown.
     *
     * @param ordered If false, methods that remove elements may change the order of other elements in the array, which
     *            avoids a memory copy.
     * @param provider creates new object on get(index) calls if the value on the selected index is null.
     * @param array will be used.
     * @param start starting index.
     * @param count elements amount. */
    public LazyArray(final ObjectProvider<? extends Type> provider, final boolean ordered, final Type[] array,
            final int start, final int count) {
        super(ordered, array, start, count);
        this.provider = provider;
    }

    /** @param provider creates new object on get(index) calls if the value on the selected index is null.
     * @return a new instance of lazy array.
     * @param <Type> type of stored values. */
    public static <Type> LazyArray<Type> newArray(final ObjectProvider<? extends Type> provider) {
        return new LazyArray<Type>(provider);
    }

    /** @return a new lazy array that provides empty, non-typed arrays.
     * @param <Type> type of stored values. */
    public static <Type> LazyArray<Array<Type>> newArrayOfArrays() {
        final ObjectProvider<Array<Type>> provider = ArrayObjectProvider.getProvider();
        return newArray(provider);
    }

    /** @return a new lazy array that provides empty object sets.
     * @param <Type> type of stored values. */
    public static <Type> LazyArray<ObjectSet<Type>> newArrayOfSets() {
        final ObjectProvider<ObjectSet<Type>> provider = SetObjectProvider.getProvider();
        return newArray(provider);
    }

    /** @return a new lazy array that provides empty object maps.
     * @param <Key> type of map keys.
     * @param <Value> type of set values. */
    public static <Key, Value> LazyArray<ObjectMap<Key, Value>> newArrayOfMaps() {
        final ObjectProvider<ObjectMap<Key, Value>> provider = MapObjectProvider.getProvider();
        return newArray(provider);
    }

    /** @param provider creates new object on get(index) calls if the value on the selected index is null.
     * @param values will be used.
     * @return a new LazyArray containing the passed objects.
     * @param <Type> type of stored values. */
    public static <Type> LazyArray<Type> of(final ObjectProvider<? extends Type> provider, final Type... values) {
        return new LazyArray<Type>(provider, values);
    }

    /** @param provider creates new object on get(index) calls if the value on the selected index is null.
     * @param array will be copied.
     * @return a new LazyArray created using the passed array.
     * @param <Type> type of stored values. */
    public static <Type> LazyArray<Type> copyOf(final ObjectProvider<? extends Type> provider,
            final Array<? extends Type> array) {
        return new LazyArray<Type>(provider, array);
    }

    /** @param array will be copied.
     * @return a new LazyArray created using the passed array.
     * @param <Type> type of stored values. */
    public static <Type> LazyArray<Type> copyOf(final LazyArray<? extends Type> array) {
        return new LazyArray<Type>(array);
    }

    /** @param provider creates new object on get(index) calls if the value on the selected index is null.
     * @param values will be appended to the array.
     * @return a new LazyArray with the passed values.
     * @param <Type> type of stored values. */
    public static <Type> LazyArray<Type> with(final ObjectProvider<? extends Type> provider,
            final Iterable<? extends Type> values) {
        final LazyArray<Type> array = new LazyArray<Type>(provider);
        for (final Type value : values) {
            array.add(value);
        }
        return array;
    }

    /** @param values will be appended to the array.
     * @param forClass type of stored values.
     * @param provider used to create elements when getter methods are called for empty indexes.
     * @return a new typed LazyArray with the passed values.
     * @param <Type> type of stored values. */
    public static <Type> LazyArray<Type> with(final ObjectProvider<? extends Type> provider, final Class<Type> forClass,
            final Iterable<? extends Type> values) {
        final LazyArray<Type> array = new LazyArray<Type>(provider, forClass);
        for (final Type value : values) {
            array.add(value);
        }
        return array;
    }

    @Override
    public Type get(final int index) {
        ensureCapacity(Math.max(index - size + 1, 0));
        Type value = items[index];
        if (value == null) {
            value = provider.provide();
            items[index] = value;
        }
        return value;
    }

    /** @return provider that produces new instances of objects on getter calls with unknown indexes */
    public ObjectProvider<? extends Type> getProvider() {
        return provider;
    }

    /** @param provider produces new instances of objects on getter calls with unknown indexes. */
    public void setProvider(final ObjectProvider<? extends Type> provider) {
        this.provider = provider;
    }
}