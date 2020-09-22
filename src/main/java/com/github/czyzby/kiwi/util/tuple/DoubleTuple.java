package com.github.czyzby.kiwi.util.tuple;

import java.util.Map;

/** Interface shared by both mutable and immutable double tuples. Ensures that tuples contain methods that allow to
 * obtain stored values.
 *
 * @author MJ */
public interface DoubleTuple<First, Second> extends Iterable<Object>, Map.Entry<First, Second>, Tuple {
    int SIZE = 2;

    /** @return first value stored in pair. Equivalent of getKey. */
    public First getFirst();

    /** @return true if first value in pair is not null. */
    public boolean isFirstPresent();

    /** @return second value stored in pair. Equivalent of getValue. */
    public Second getSecond();

    /** @return true if second value in pair is not null. */
    public boolean isSecondPresent();

    /** @return a new double tuple of the same type with inverted variables order. */
    public DoubleTuple<Second, First> invert();

    /** @param third will be set as third triplet's value.
     * @return a new TripleTuple constructed with this pair's values and another passed variable. It's mutability should
     *         match DoubleTuple implementation.
     * @param <Third> type of third value. */
    public <Third> TripleTuple<First, Second, Third> toTripleTuple(Third third);

}