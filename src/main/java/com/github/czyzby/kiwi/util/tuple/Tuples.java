package com.github.czyzby.kiwi.util.tuple;

import java.util.Iterator;

import com.github.czyzby.kiwi.util.common.UtilitiesClass;

/** Contains utilties for creating tuples. Note that factory methods are provided by classes that are implementation of
 * specific tuple interfaces.
 *
 * @author MJ */
public class Tuples extends UtilitiesClass {
    private Tuples() {
    }

    /** @param tuple will be iterated over.
     * @return a new instance of an iterator that iterates over tuple's values.
     * @param <Type> iterator type. */
    public static <Type> Iterator<Type> getTupleIterator(final Tuple tuple) {
        return new Iterator<Type>() {
            private int currentIndex;

            @Override
            public boolean hasNext() {
                return currentIndex < tuple.getSize();
            }

            @Override
            @SuppressWarnings("unchecked")
            public Type next() {
                return (Type) tuple.get(currentIndex++);
            }

            @Override
            public void remove() {
                if (tuple.isMutable()) {
                    tuple.set(currentIndex, null);
                } else {
                    throw new UnsupportedOperationException("Tuple is immutable.");
                }
            }
        };
    }
}
