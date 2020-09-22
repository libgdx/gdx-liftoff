package com.github.czyzby.kiwi.util.tuple;

import java.util.Map;

/** Interface shared by both mutable and immutable triple tuples. Ensures that tuples contain methods that allow to
 * obtain stored values.
 *
 * @author MJ */
public interface TripleTuple<First, Second, Third> extends Iterable<Object>, Map.Entry<First, Second>, Tuple {
    int SIZE = 3;

    /** @return first value stored in triple. Equivalent of getKey. */
    public First getFirst();

    /** @return true if first value in triple is not null. */
    public boolean isFirstPresent();

    /** @return second value stored in triple. Equivalent of getValue. */
    public Second getSecond();

    /** @return true if second value in triple is not null. */
    public boolean isSecondPresent();

    /** @return third value stored in triple. */
    public Third getThird();

    /** @return true if third value in triple is not null. */
    public boolean isThirdPresent();

    /** @return a new triple with inverted variables order. */
    public TripleTuple<Third, Second, First> invert();

    /** @return a new triple with values shifted left. */
    public TripleTuple<Second, Third, First> shiftLeft();

    /** @return a new triple with values shifted right. */
    public TripleTuple<Third, First, Second> shitfRight();
}
