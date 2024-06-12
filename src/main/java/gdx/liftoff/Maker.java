package gdx.liftoff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * Utility methods for more easily constructing data structures, particularly those in Java's standard library.
 * All static methods; meant to be imported with {@code import static gdx.liftoff.Maker.*;}.
 */
public class Maker {

    /**
     * Stores any information relating to non-fatal issues, such as caught and handled Exceptions that still change the
     * behavior of methods. Typically, this shouldn't be cleared while debugging, since it could be useful later on, and
     * hopefully won't need to be written to in a release build.
     */
    public static final StringBuilder issueLog = new StringBuilder(1024);

    /**
     * Makes a LinkedHashMap (LHM) with key and value types inferred from the types of k0 and v0, and considers all
     * parameters key-value pairs, casting the Objects at positions 0, 2, 4... etc. to K and the objects at positions
     * 1, 3, 5... etc. to V. If rest has an odd-number length, then it discards the last item. If any pair of items in
     * rest cannot be cast to the correct type of K or V, then this inserts nothing for that pair and logs information
     * on the problematic pair to the static Maker.issueLog field.
     *
     * @param k0   the first key; used to infer the types of other keys if generic parameters aren't specified.
     * @param v0   the first value; used to infer the types of other values if generic parameters aren't specified.
     * @param rest an array or vararg of keys and values in pairs; should contain alternating K, V, K, V... elements
     * @param <K>  the type of keys in the returned LinkedHashMap; if not specified, will be inferred from k0
     * @param <V>  the type of values in the returned LinkedHashMap; if not specified, will be inferred from v0
     * @return a freshly-made LinkedHashMap with K keys and V values, using k0, v0, and the contents of rest to fill it
     */
    @SuppressWarnings("unchecked")
    public static <K, V> LinkedHashMap<K, V> makeLHM(K k0, V v0, Object... rest) {
        if (rest == null || rest.length == 0) {
            LinkedHashMap<K, V> lhm = new LinkedHashMap<>(2);
            lhm.put(k0, v0);
            return lhm;
        }
        LinkedHashMap<K, V> lhm = new LinkedHashMap<>(1 + (rest.length / 2));
        lhm.put(k0, v0);

        for (int i = 0; i < rest.length - 1; i += 2) {
            try {
                lhm.put((K) rest[i], (V) rest[i + 1]);
            } catch (ClassCastException cce) {
                issueLog.append("makeLHM call had a casting problem with pair at rest[")
                    .append(i)
                    .append("] and/or rest[")
                    .append(i + 1)
                    .append("], with contents: ")
                    .append(rest[i])
                    .append(", ")
                    .append(rest[i + 1])
                    .append(".\n\nException messages:\n")
                    .append(cce);
                String msg = cce.getMessage();
                if (msg != null) {
                    issueLog.append('\n').append(msg);
                }
                issueLog.append('\n');
            }
        }
        return lhm;
    }

    /**
     * Makes an empty LinkedHashMap (LHM); needs key and value types to be specified in order to work. For an empty
     * LinkedHashMap with String keys and Coord values, you could use {@code Maker.<String, Coord>makeLHM();}. Using
     * the new keyword is probably just as easy in this case; this method is provided for completeness relative to
     * makeLHM() with 2 or more parameters.
     *
     * @param <K> the type of keys in the returned LinkedHashMap; cannot be inferred and must be specified
     * @param <V> the type of values in the returned LinkedHashMap; cannot be inferred and must be specified
     * @return an empty LinkedHashMap with the given key and value types.
     */
    public static <K, V> LinkedHashMap<K, V> makeLHM() {
        return new LinkedHashMap<>();
    }

    /**
     * Makes a HashMap (HM) with key and value types inferred from the types of k0 and v0, and considers all
     * parameters key-value pairs, casting the Objects at positions 0, 2, 4... etc. to K and the objects at positions
     * 1, 3, 5... etc. to V. If rest has an odd-number length, then it discards the last item. If any pair of items in
     * rest cannot be cast to the correct type of K or V, then this inserts nothing for that pair and logs information
     * on the problematic pair to the static Maker.issueLog field.
     *
     * @param k0   the first key; used to infer the types of other keys if generic parameters aren't specified.
     * @param v0   the first value; used to infer the types of other values if generic parameters aren't specified.
     * @param rest an array or vararg of keys and values in pairs; should contain alternating K, V, K, V... elements
     * @param <K>  the type of keys in the returned HashMap; if not specified, will be inferred from k0
     * @param <V>  the type of values in the returned HashMap; if not specified, will be inferred from v0
     * @return a freshly-made HashMap with K keys and V values, using k0, v0, and the contents of rest to fill it
     */
    @SuppressWarnings("unchecked")
    public static <K, V> HashMap<K, V> makeHM(K k0, V v0, Object... rest) {
        if (rest == null || rest.length == 0) {
            HashMap<K, V> hm = new HashMap<>(2);
            hm.put(k0, v0);
            return hm;
        }
        HashMap<K, V> hm = new HashMap<>(1 + (rest.length / 2));
        hm.put(k0, v0);

        for (int i = 0; i < rest.length - 1; i += 2) {
            try {
                hm.put((K) rest[i], (V) rest[i + 1]);
            } catch (ClassCastException cce) {
                issueLog.append("makeHM call had a casting problem with pair at rest[")
                    .append(i).append("] and/or rest[")
                    .append(i + 1).append("], with contents: ")
                    .append(rest[i]).append(", ")
                    .append(rest[i + 1]).append(".\n\nException messages:\n")
                    .append(cce);
                String msg = cce.getMessage();
                if (msg != null) {
                    issueLog.append('\n').append(msg);
                }
                issueLog.append('\n');
            }
        }
        return hm;
    }

    /**
     * Makes an empty HashMap (HM); needs key and value types to be specified in order to work. For an empty
     * HashMap with String keys and Coord values, you could use {@code Maker.<String, Coord>makeHM();}. Using
     * the new keyword is probably just as easy in this case; this method is provided for completeness relative to
     * makeHM() with 2 or more parameters.
     *
     * @param <K> the type of keys in the returned HashMap; cannot be inferred and must be specified
     * @param <V> the type of values in the returned HashMap; cannot be inferred and must be specified
     * @return an empty HashMap with the given key and value types.
     */
    public static <K, V> HashMap<K, V> makeHM() {
        return new HashMap<>();
    }

    /**
     * Makes an ArrayList of T given an array or vararg of T elements.
     *
     * @param elements an array or vararg of T
     * @param <T>      just about any non-primitive type
     * @return a newly-allocated ArrayList containing all the elements, in order
     */
    @SuppressWarnings("unchecked")
    public static <T> ArrayList<T> makeList(T... elements) {
        if (elements == null) return null;
        ArrayList<T> list = new ArrayList<>(elements.length);
        Collections.addAll(list, elements);
        return list;
    }

    /**
     * Makes an ArrayList of T given a single T element; avoids creating an array for varargs as
     * {@link #makeList(Object[])} would do, but only allows one item.
     *
     * @param element an array or vararg of T
     * @param <T>     just about any non-primitive, non-array type (arrays would cause confusion with the vararg method)
     * @return a newly-allocated ArrayList containing only element
     */
    public static <T> ArrayList<T> makeList(T element) {
        ArrayList<T> list = new ArrayList<>(1);
        list.add(element);
        return list;
    }

    /**
     * Makes a LinkedHashSet (LHS) of T given an array or vararg of T elements. Duplicate items in elements will have
     * all but one item discarded, using the later item in elements.
     *
     * @param elements an array or vararg of T
     * @param <T>      just about any non-primitive type
     * @return a newly-allocated LinkedHashSet containing all the non-duplicate items in elements, in order
     */
    @SuppressWarnings("unchecked")
    public static <T> LinkedHashSet<T> makeLHS(T... elements) {
        if (elements == null) return null;
        LinkedHashSet<T> set = new LinkedHashSet<>(elements.length);
        Collections.addAll(set, elements);
        return set;
    }

    /**
     * Makes a LinkedHashSet (LHS) of T given a single T element.
     *
     * @param element a single T
     * @param <T>     just about any non-primitive type
     * @return a newly-allocated LinkedHashSet containing only {@code element}
     */
    public static <T> LinkedHashSet<T> makeLHS(T element) {
        LinkedHashSet<T> set = new LinkedHashSet<>(1);
        set.add(element);
        return set;
    }

    /**
     * Makes a HashSet (HS) of T given an array or vararg of T elements. Duplicate items in elements will have
     * all but one item discarded, using the later item in elements.
     *
     * @param elements an array or vararg of T
     * @param <T>      just about any non-primitive type
     * @return a newly-allocated HashSet containing all the non-duplicate items in elements, in order
     */
    @SuppressWarnings("unchecked")
    public static <T> HashSet<T> makeHS(T... elements) {
        if (elements == null) return null;
        HashSet<T> set = new HashSet<>(elements.length);
        Collections.addAll(set, elements);
        return set;
    }

    /**
     * Makes a HashSet (HS) of T given a single T element.
     *
     * @param element a single T
     * @param <T>     just about any non-primitive type
     * @return a newly-allocated HashSet containing only {@code element}
     */
    public static <T> HashSet<T> makeHS(T element) {
        HashSet<T> set = new HashSet<>(1);
        set.add(element);
        return set;
    }
}
