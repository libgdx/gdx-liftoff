package com.github.czyzby.autumn.scanner;

import java.lang.annotation.Annotation;

import com.badlogic.gdx.utils.Array;

/** Common interface for class scanners. Used to find classes annotated with a chosen set of annotations in selected
 * package.
 *
 * @author MJ */
public interface ClassScanner {
    /** @param root class scanning root. Found classes have to share its package.
     * @param annotations collection of annotations to look for. If the scanned class is annotated with one (or more) of
     *            these annotations, it should be returned in the result array.
     * @return all classes in root's package annotated with at least one of the selected annotations. */
    Array<Class<?>> findClassesAnnotatedWith(Class<?> root, Iterable<Class<? extends Annotation>> annotations);
}
