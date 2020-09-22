package com.github.czyzby.kiwi.util.tuple;

/** Interface shared by all single tuples. Allows singles to be strongly-typed iterables.
 *
 * @author MJ */
public interface SingleTuple<Type> extends Tuple, Iterable<Type> {
    int SIZE = 1;

    /** @return value stored in tuple. */
    public Type getFirst();

    /** @return value stored in tuple. */
    public Type get();

    /** @return true if value stored in tuple is not null. */
    public boolean isFirstPresent();

    /** @return true if value stored in tuple is not null. */
    public boolean isPresent();
}
