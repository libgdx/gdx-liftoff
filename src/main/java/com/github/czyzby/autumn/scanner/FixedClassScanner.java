package com.github.czyzby.autumn.scanner;

import java.lang.annotation.Annotation;

import com.badlogic.gdx.utils.Array;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;

/** Rather than scanning through the whole application, this scanner uses a limited pool of registered classes. This
 * might be significantly faster than other scanners, but it does require "manual" registration of scanned classes in
 * most cases, so it defeats the whole purpose of automatic class scanning. Use if you have a good reason to do so.
 *
 * @author MJ */
public class FixedClassScanner extends AbstractClassScanner {
    private final Array<Class<?>> pool;

    /** @param availableClasses will be available in scanner's pool. */
    public FixedClassScanner(final Class<?>... availableClasses) {
        pool = GdxArrays.newArray(availableClasses);
    }

    /** @param availableClass will be available in scanner's pool. */
    public void addClass(final Class<?> availableClass) {
        pool.add(availableClass);
    }

    /** @param availableClasses will be available in scanner's pool. */
    public void addClasses(final Class<?>... availableClasses) {
        pool.addAll(availableClasses);
    }

    @Override
    public Array<Class<?>> findClassesAnnotatedWith(final Class<?> root,
            final Iterable<Class<? extends Annotation>> annotations) {
        final String packageName = extractPackageName(root);
        final Array<Class<?>> result = GdxArrays.newArray();
        for (final Class<?> possibleMatch : pool) {
            if (isInPackage(possibleMatch, packageName) && isAnnotatedWithAny(possibleMatch, annotations)) {
                result.add(possibleMatch);
            }
        }
        return result;
    }
}
