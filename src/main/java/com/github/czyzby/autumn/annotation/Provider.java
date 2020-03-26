package com.github.czyzby.autumn.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Should annotate classes that provide injection dependencies of non-component, unannotated classes. If
 * Provider-annotated class implements {@link com.github.czyzby.autumn.provider.DependencyProvider} interface, its
 * {@link com.github.czyzby.autumn.provider.DependencyProvider#provide()} method will be used to get instances of the
 * selected type; if the interface is not implemented, EVERY non-void method will be turned into a provider. Methods can
 * have dependencies themselves, which will be properly injected, but be careful not to include circular dependencies:
 * if, for example, a provider's method requires an instance of object that it provides, it will cause a nasty
 * exception. If no provider is given for the selected class and the is no component of that class, an instance of the
 * object will be created with default, no-arg constructor.
 *
 * <p>
 * Note that providers are meta-components and are initiated BEFORE regular components, as they are often used to
 * resolve constructor and field dependencies. While providers can have their fields injected, methods processed (etc),
 * they should reference only other meta-components to be properly initiated.
 *
 * @author MJ */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Provider {
}
