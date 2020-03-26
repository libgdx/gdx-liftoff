package com.github.czyzby.lml.util.collection;

import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.kiwi.util.common.Strings;

/** {@link ObjectMap} which uses Strings as keys. Keys are compared ignoring their case - for example, value mapped to
 * "String" will be returned also for "STRING", "string" or "sTrInG" (etc).
 *
 * @author MJ
 *
 * @param <Value> type of values stored in the map. */
public class IgnoreCaseStringMap<Value> extends KeyNormalizingObjectMap<String, Value> {
    public IgnoreCaseStringMap() {
        super();
    }

    /** @param map will copy its values, using ignore-case keys. */
    public IgnoreCaseStringMap(final ObjectMap<String, Value> map) {
        super(map);
    }

    /** @param key will be normalized, ensuring that all strings are equal, ignoring their case.
     * @return normalized key.
     * @throws NullPointerException if key is null. */
    @Override
    protected String normalizeKey(final String key) {
        return Strings.toLowerCase(key);
    }
}
