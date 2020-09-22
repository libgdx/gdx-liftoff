package com.github.czyzby.kiwi.util.gdx.collection.pooled;

import java.util.Iterator;

import com.badlogic.gdx.utils.Pool;
import com.github.czyzby.kiwi.util.common.Nullables;

/** {@link java.util.LinkedList} equivalent for LibGDX applications. As opposed to
 * {@link com.badlogic.gdx.utils.PooledLinkedList}, this list allows to use custom node pools to share them among
 * multiple list instances and implements {@link Iterable} interface.
 * <p>
 * List's nodes are pooled to limit object creation (hence the name). Iterator is stored and reused - every
 * {@link #iterator()} call returns the same object. All provided methods are constant-time operations - costly
 * operations, like removal by index, were simply not included.
 * <p>
 * This list does NOT implement any collection-related interfaces, because LibGDX (sadly) does not provide any, and
 * standard Java collection interfaces contain operations that would be highly inefficient in case of a linked list
 * (like accessing or removing an element by its numeric index) and non-generic methods for backwards compatibility. It
 * was assumed that no common collection interface is better than misleading API, especially since LibGDX collections do
 * not share any interface either.
 * <p>
 * Usage examples: FIFO queue:<blockquote>
 *
 * <pre>
 * list.add(element);
 * list.addLast(element); // add(T) alias.
 * while (list.isNotEmpty()) {
 *     E element = list.removeLast();
 * }
 * </pre>
 *
 * </blockquote>LIFO queue: <blockquote>
 *
 * <pre>
 * while (list.isNotEmpty()) {
 *     E element = list.removeFirst();
 * }
 * </pre>
 *
 * </blockquote>List modifications during iteration: <blockquote>
 *
 * <pre>
 * for (E element : list) {
 *     // Inserting a value after the element:
 *     list.insertAfter(value);
 *     // Removing element:
 *     list.remove();
 * }
 * </pre>
 *
 * </blockquote>See {@link PooledListIterator} docs for more informations about modifying the collection during
 * iteration.
 *
 * @author MJ
 * @param <T> type of stored values.
 * @since 1.4 */
public class PooledList<T> implements Iterable<T> {
    /** Used by the default constructor and factory methods. Has no limit. NOT thread-safe - in multi-thread
     * applications, a custom node pool should be used (either a thread-safe pool or a separate pool for each
     * thread). */
    @SuppressWarnings({ "rawtypes", "unchecked" }) public static final Pool<Node<?>> DEFAULT_POOL = new NodePool();
    private final Pool<Node<T>> pool;
    private final PooledListIterator<T> iterator = new PooledListIterator<T>(this);
    private int size;
    // Both head and tail are never null. If head == tail, list is empty.
    private final Node<T> head;
    private Node<T> tail;

    /** Creates a new {@link PooledList} using the {@link #DEFAULT_POOL}. */
    public PooledList() {
        this(DEFAULT_POOL);
    }

    /** @param pool will be used as the custom node pool.
     * @see #newNodePool(int, int) */
    @SuppressWarnings("unchecked")
    public PooledList(final Pool<Node<?>> pool) {
        this.pool = (Pool<Node<T>>) (Object) pool;
        head = tail = this.pool.obtain();
    }

    /** @return a new, empty {@link PooledList} instance using the {@link #DEFAULT_POOL}.
     * @param <Type> type of stored values. */
    public static <Type> PooledList<Type> newList() {
        return new PooledList<Type>();
    }

    /** @param pool custom node pool.
     * @return a new, empty {@link PooledList} instance using the passed node pool.
     * @param <Type> type of stored values. */
    public static <Type> PooledList<Type> newList(final Pool<Node<?>> pool) {
        return new PooledList<Type>(pool);
    }

    /** @param elements will be added to the list. None of them can be null.
     * @return a new {@link PooledList} instance using the {@link #DEFAULT_POOL}.
     * @param <Type> type of stored values. */
    public static <Type> PooledList<Type> of(final Type... elements) {
        return new PooledList<Type>().addAll(elements);
    }

    /** @param pool custom node pool.
     * @param elements will be added to the list. None of them can be null.
     * @return a new {@link PooledList} instance using the passed node pool.
     * @param <Type> type of stored values. */
    public static <Type> PooledList<Type> of(final Pool<Node<?>> pool, final Type... elements) {
        return new PooledList<Type>(pool).addAll(elements);
    }

    /** @param elements will be iterated over and added to the list.
     * @return a new {@link PooledList} instance using the {@link #DEFAULT_POOL}.
     * @param <Type> type of stored values. */
    public static <Type> PooledList<Type> copyOf(final Iterable<Type> elements) {
        return new PooledList<Type>().addAll(elements);
    }

    /** @param pool custom node pool.
     * @param elements will be iterated over and added to the list.
     * @return a new {@link PooledList} instance using the passed node pool.
     * @param <Type> type of stored values. */
    public static <Type> PooledList<Type> copyOf(final Pool<Node<?>> pool, final Iterable<Type> elements) {
        return new PooledList<Type>(pool).addAll(elements);
    }

    /** @param initialCapacity initial size of the free objects array. Will be resized if needed.
     * @param max the maximum number of free objects to store in this pool. If free objects array size matches max,
     *            freed objects are rejected and garbage collected.
     * @return a new node pool of the selected size. */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Pool<Node<?>> newNodePool(final int initialCapacity, final int max) {
        return new NodePool(initialCapacity, max);
    }

    /** @param list can be null.
     * @return true if list is null or has no elements. */
    public static boolean isEmpty(final PooledList<?> list) {
        return list == null || list.isEmpty();
    }

    /** @param list can be null.
     * @return true if list is not null and has any elements. */
    public static boolean isNotEmpty(final PooledList<?> list) {
        return list != null && list.isNotEmpty();
    }

    /** @return amount of elements in the list. */
    public int size() {
        return size;
    }

    /** @return true if list has no elements. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** @return true if list has any elements. */
    public boolean isNotEmpty() {
        return size > 0;
    }

    /** @return head (first element) of the list. Null if list is empty. */
    public T getFirst() {
        return isNotEmpty() ? head.next.element : null;
    }

    /** @return tail (last element) of the list. Null if list is empty. */
    public T getLast() {
        return tail.element;
    }

    /** @param element will replace the current head value. Previous head value will be removed. Cannot be null.
     * @return previous head value.
     * @throws IllegalStateException if list is empty. */
    public T setFirst(final T element) {
        Nullables.requireNotNull(element);
        if (isEmpty()) {
            throw new IllegalStateException("List is empty. Cannot change head.");
        }
        final T previous = head.next.element;
        head.next.element = element;
        return previous;
    }

    /** @param element will replace current tail value. Previous tail value will be removed. Cannot be null.
     * @return previous tail value.
     * @throws IllegalStateException if list is empty. */
    public T setLast(final T element) {
        Nullables.requireNotNull(element);
        if (isEmpty()) {
            throw new IllegalStateException("List is empty. Cannot change tail.");
        }
        final T previous = tail.element;
        tail.element = element;
        return previous;
    }

    /** @param element will be added as the last element in the list. Cannot be null.
     * @return this, for chaining. */
    public PooledList<T> add(final T element) {
        addLast(element);
        return this;
    }

    /** @param elements will be added to the list. None of them can be null.
     * @return this, for chaining. */
    public PooledList<T> addAll(final T... elements) {
        for (final T element : elements) {
            addLast(element);
        }
        return this;
    }

    /** @param elements will be added to the list. None of them can be null.
     * @return this, for chaining. */
    public PooledList<T> addAll(final Iterable<T> elements) {
        for (final T element : elements) {
            addLast(element);
        }
        return this;
    }

    /** @param element will be added as the last element in the list. Cannot be null.
     * @see #add(Object) */
    public void addLast(final T element) {
        insertAfter(tail, element);
    }

    /** @param element will be added as the first element in the list. Cannot be null. */
    public void addFirst(final T element) {
        insertAfter(head, element);
    }

    /** @return value of the first element that got removed. Null if list is empty. */
    public T removeFirst() {
        return isEmpty() ? null : remove(head.next);
    }

    /** @return value of last element that got removed. Null if list is empty. */
    public T removeLast() {
        return isEmpty() ? null : remove(tail);
    }

    /** @return internally stored reusable {@link PooledListIterator}. */
    @Override
    public PooledListIterator<T> iterator() {
        return iterator.reset();
    }

    /** @return a new instance of {@link PooledListIterator}. As opposed to {@link #iterator()} method, this method
     *         returns a new instance of the iterator, allowing to (for example) iterate over the list in a nested
     *         for-each loop. However, keep in mind that removal or insertion during nested iteration is not advised. */
    public PooledListIterator<T> iterate() {
        return new PooledListIterator<T>(this).reset();
    }

    /** Operation valid ONLY during iteration over the list using default, cached iterator ({@link #iterator()} method).
     *
     * @return value of the removed element. */
    public T remove() {
        final Node<T> previous = iterator.currentNode.previous;
        final T element = remove(iterator.currentNode);
        iterator.currentNode = previous;
        return element;
    }

    private T remove(final Node<T> node) {
        final T element = node.element;
        node.previous.next = node.next;
        if (node.next != null) {
            node.next.previous = node.previous;
        } else {
            tail = node.previous;
        }
        node.free(pool);
        size--;
        return element;
    }

    /** Operation valid ONLY during iteration over the list using default, cached iterator ({@link #iterator()} method).
     * Note that this operation might cause an infinite loop - be careful when inserting values during iteration.
     *
     * @param element will be inserted after the element that is currently processed by the iterator. Will be processed
     *            as the next element during current iteration.
     * @see #skip() */
    public void insertAfter(final T element) {
        insertAfter(iterator.currentNode, element);
    }

    protected void insertAfter(final Node<T> after, final T element) {
        Nullables.requireNotNull(element);
        final Node<T> node = pool.obtain();
        node.element = element;
        node.next = after.next;
        node.previous = after;
        after.next = node;
        if (node.next != null) {
            node.next.previous = node;
        } else {
            tail = node;
        }
        size++;
    }

    /** Operation valid ONLY during iteration over the list using default, cached iterator ({@link #iterator()} method).
     *
     * @param element will be inserted before the element that is currently processed by the iterator. Will be ignored
     *            in the current iteration. */
    public void insertBefore(final T element) {
        insertBefore(iterator.currentNode, element);
    }

    protected void insertBefore(final Node<T> before, final T element) {
        Nullables.requireNotNull(element);
        final Node<T> node = pool.obtain();
        node.element = element;
        node.next = before;
        node.previous = before.previous;
        before.previous = node;
        node.previous.next = node;
        size++;
    }

    /** Operation valid ONLY during iteration over the list using default, cached iterator ({@link #iterator()} method).
     * Changes current iteration pointer.
     *
     * @see PooledListIterator#skip() */
    public void skip() {
        iterator.skip();
    }

    /** Operation valid ONLY during iteration over the list using default, cached iterator ({@link #iterator()} method).
     *
     * @param element will replace the value of current node.
     * @return previous element value. */
    public T replace(final T element) {
        return iterator.replace(element);
    }

    /** @return direct reference to list's pool. */
    public Pool<Node<T>> getPool() {
        return pool;
    }

    /** Removes all elements of the list. Frees all nodes to the pool. */
    public void clear() {
        Node<T> node = head.next, next;
        size = 0;
        head.reset(); // Clearing references.
        tail = head;
        iterator.currentNode = head;
        while (node != null) {
            next = node.next;
            node.free(pool);
            node = next;
        }
    }

    /** Clears the list. Instead of returning the nodes to the pool, it simply clears references to them, allowing them
     * to be garbage-collected. Invoke this method only if you don't plan on using {@link PooledList} with the selected
     * pool anymore or if the pool has a max value that is already achieved (or lesser than total amount of nodes in the
     * list), in which case nodes would be garbage-collected as well.
     *
     * @see #clear() */
    public void purge() {
        head.reset();
        tail = head;
        size = 0;
        iterator.currentNode = head;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (final T element : this) {
            builder.append(element);
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        builder.append("]");
        return builder.toString();
    }

    /** Represents a single node in the {@link PooledList}. Stores an element and references to its node neighbors.
     *
     * @author MJ
     *
     * @param <T> type of stored element. */
    public static final class Node<T> {
        private T element;
        private Node<T> previous;
        private Node<T> next;

        /** Clears node data. */
        public void reset() {
            element = null;
            previous = null;
            next = null;
        }

        /** @param pool returns node to this pool. */
        public void free(final Pool<Node<T>> pool) {
            reset();
            pool.free(this);
        }

        /** @return stored element. */
        public T getElement() {
            return element;
        }

        protected void setElement(final T element) {
            this.element = element;
        }

        /** @return previous node in the list. */
        public Node<T> getPrevious() {
            return previous;
        }

        protected void setPrevious(final Node<T> previous) {
            this.previous = previous;
        }

        /** @return next node in the list. */
        public Node<T> getNext() {
            return next;
        }

        protected void setNext(final Node<T> next) {
            this.next = next;
        }
    }

    /** Allows to iterate over a {@link PooledList}. Provides operations that allow to modify the list:
     * {@link PooledListIterator#remove()}, {@link PooledListIterator#insert(Object)},
     * {@link PooledListIterator#insertBefore(Object)} and {@link PooledListIterator#replace(Object)}. Implements
     * {@link Iterable} (resetting and returning itself on {@link PooledListIterator#iterator()} call) for extra
     * utility.
     *
     * @author MJ
     *
     * @param <T> type of stored values. */
    public static class PooledListIterator<T> implements Iterator<T>, Iterable<T> {
        private final PooledList<T> list;
        private Node<T> currentNode;

        /** @param list will be iterated over. */
        public PooledListIterator(final PooledList<T> list) {
            this.list = list;
        }

        /** Resets the iterator (starting iteration from head) and returns it.
         *
         * @return this. */
        @Override
        public Iterator<T> iterator() {
            return reset();
        }

        @Override
        public boolean hasNext() {
            return currentNode.next != null;
        }

        @Override
        public T next() {
            currentNode = currentNode.next;
            return currentNode.element;
        }

        /** A null-safe method that skips the current iteration element. As this changes pointer to the current value,
         * modifying operations (like {@link #remove()}) are generally not safe to use after invoking this method.
         * <p>
         * This method should be called after {@link #insert(Object)} if you want to omit the next inserted value during
         * iteration. */
        public void skip() {
            if (hasNext()) {
                currentNode = currentNode.next;
            }
        }

        @Override
        public void remove() {
            final Node<T> previous = currentNode.previous;
            if (previous == null) {
                throw new IllegalStateException("next() has to be called before removing a value.");
            }
            list.remove(currentNode);
            currentNode = previous;
        }

        /** @param element will replace current element value. Cannot be null.
         * @return previous element value. */
        public T replace(final T element) {
            Nullables.requireNotNull(element);
            final T previous = currentNode.element;
            currentNode.element = element;
            return previous;
        }

        /** If this is the main, cached iterator of the list, {@link PooledList#insertAfter(Object)} can be used
         * instead. This method should not be invoked before {@link #next()}.
         *
         * @param element will be inserted after the current element. Will be the next element during this iteration.
         * @see #skip() */
        public void insert(final T element) {
            list.insertAfter(currentNode, element);
        }

        /** If this is the main, cached iterator of the list, {@link PooledList#insertBefore(Object)} can be used
         * instead. This method cannot not be invoked before {@link #next()}.
         *
         * @param element will be inserted before the current element. Will be ignored during current iteration. */
        public void insertBefore(final T element) {
            list.insertBefore(currentNode, element);
        }

        /** Starts iteration from list's head (first element).
         *
         * @return this, for chaining. */
        public PooledListIterator<T> reset() {
            currentNode = list.head;
            return this;
        }
    }

    /** Default implementation of a {@link Pool} storing {@link PooledList}'s {@link Node nodes}.
     *
     * @author MJ
     *
     * @param <T> type of stored values. */
    public static class NodePool<T> extends Pool<Node<T>> {
        /** Creates a new pool with default initial size and no max value. */
        public NodePool() {
            this(16, Integer.MAX_VALUE);
        }

        /** @param initialCapacity initial size of the free objects array. Will be resized if needed.
         * @param max the maximum number of free objects to store in this pool. If free objects array size matches max,
         *            freed objects are rejected and garbage collected. */
        public NodePool(final int initialCapacity, final int max) {
            super(initialCapacity, max);
        }

        /** @return a new pool with default size.
         * @param <T> type of stored nodes. */
        public static <T> Pool<Node<T>> newPool() {
            return new NodePool<T>();
        }

        @Override
        @SuppressWarnings({ "rawtypes", "unchecked" })
        protected Node<T> newObject() {
            return new Node();
        }
    }
}
