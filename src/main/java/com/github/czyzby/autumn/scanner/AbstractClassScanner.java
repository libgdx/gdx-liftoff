package com.github.czyzby.autumn.scanner;

import java.lang.annotation.Annotation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.ClassReflection;

/** Contains utility methods common for vanilla Java + libGDX scanners that could support GWT.
 *
 * @author MJ */
public abstract class AbstractClassScanner implements ClassScanner {
    /** GWT-friendly method that extracts name of the package from class.
     *
     * @param root its package will be extracted.
     * @return name of root's package. */
    protected String extractPackageName(final Class<?> root) {
        return root.getName().substring(0, root.getName().length() - root.getSimpleName().length() - 1);
    }

    /** @param possibleMatch might be in the selected package.
     * @param packageName name of the package.
     * @return true if the class is in the chosen package. */
    protected boolean isInPackage(final Class<?> possibleMatch, final String packageName) {
        return possibleMatch.getName().startsWith(packageName);
    }

    /** @param possibleMatch might be annotated.
     * @param annotations annotations to scan for.
     * @return true if the class is annotated with any of the passed annotations. */
    protected boolean isAnnotatedWithAny(final Class<?> possibleMatch,
            final Iterable<Class<? extends Annotation>> annotations) {
        try {
            if (hasAnnotations(possibleMatch)) {
                for (final Class<? extends Annotation> annotation : annotations) {
                    if (ClassReflection.isAnnotationPresent(possibleMatch, annotation)) {
                        return true;
                    }
                }
            }
        } catch (final Exception exception) {
            // Not reflected classes might throw an exception on GWT, so we're expecting an error here.
            Gdx.app.error("WARN", "Exception thrown during annotation check: " + exception.getMessage());
        }
        return false;
    }

    /** @param classToProcess will be checked.
     * @return true if the class has any annotations.
     * @throws RuntimeException thrown on GWT if the class or its necessary dependencies are not reflected. */
    protected boolean hasAnnotations(final Class<?> classToProcess) {
        final com.badlogic.gdx.utils.reflect.Annotation[] annotations = ClassReflection.getAnnotations(classToProcess);
        return annotations != null && annotations.length > 0;
    }
}
