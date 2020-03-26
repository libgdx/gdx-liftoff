package com.github.czyzby.autumn.fcs.scanner;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.github.czyzby.autumn.AutumnRoot;
import com.github.czyzby.autumn.scanner.ClassScanner;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.GdxSets;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.ClassAnnotationMatchProcessor;

import java.lang.annotation.Annotation;

/** Default, efficient class scanner for desktop. Does not rely on reflection and does not load scanned classes. Uses
 * {@link FastClasspathScanner} wrapped with and adapted to {@link ClassScanner} interface to serve as default class
 * scanner for desktop LibGDX applications using Autumn. If for some reason this scanner does not work for you, try
 * {@link com.github.czyzby.autumn.nongwt.scanner.FallbackDesktopClassScanner} (which is much slower, as it depends on
 * reflection) or {@link com.github.czyzby.autumn.scanner.FixedClassScanner} (which will force you to register class
 * pool to scan, sacrificing true component scanning).
 *
 * @author MJ
 * @see FastClasspathScanner */
public class DesktopClassScanner implements ClassScanner {
    @Override
    public Array<Class<?>> findClassesAnnotatedWith(final Class<?> root,
            final Iterable<Class<? extends Annotation>> annotations) {
        final ObjectSet<Class<?>> result = GdxSets.newSet(); // Using set to remove duplicates.
        final FastClasspathScanner scanner = new FastClasspathScanner(root.getPackage().getName(),
                AutumnRoot.class.getPackage().getName());
        for (final Class<? extends Annotation> annotation : annotations) {
            scanner.matchClassesWithAnnotation(annotation, new ClassAnnotationMatchProcessor() {
                @Override
                public void processMatch(final Class<?> matchingClass) {
                    result.add(matchingClass);
                }
            });
        }
        scanner.scan();
        return GdxArrays.newArray(result);
    }
}
