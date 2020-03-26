package com.github.czyzby.autumn.context;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.autumn.provider.DependencyProvider;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.kiwi.util.gdx.collection.lazy.LazyObjectMap;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;

/** Temporary object that holds references to components. Should be cleared and garbage collected - do not keep
 * reference to this object after context creation, unless you need constant access by type.
 *
 * @author MJ */
public class Context {
    private final ObjectMap<Class<?>, Array<Object>> components = LazyObjectMap.newMapOfArrays();
    private final ObjectMap<Class<?>, Array<DependencyProvider<?>>> providers = LazyObjectMap.newMapOfArrays();
    private boolean createMissingDependencies = true;
    private boolean clear = true;

    /** Creates a new empty context. */
    public Context() {
        map(this);
    }

    /** Static factory method for {@code ContextInitializer}
     *
     * @return new instance of {@code ContextInitializer} with default annotation procesors. */
    public static ContextInitializer builder() {
        return new ContextInitializer();
    }

    /** @param clear if false, context will not be cleared upon {@link #clear()} call and it will be usable during
     *            application's lifecycle. Normally, context is cleared and garbage-collected after initiation, but if
     *            you need constant access to components mapped by their types, set this to false. */
    public void setClear(final boolean clear) {
        this.clear = clear;
    }

    /** @param createMissingDependencies if true and an instance of unknown class is required (not in context and no
     *            provider), it will be created using reflection by a no-arg constructor. Defaults to true. */
    public void setCreateMissingDependencies(final boolean createMissingDependencies) {
        this.createMissingDependencies = createMissingDependencies;
    }

    /** @return if true and an instance of unknown class is required (not in context and no provider), it will be
     *         created using reflection by a no-arg constructor. Defaults to true. */
    public boolean isCreatingMissingDependencies() {
        return createMissingDependencies;
    }

    /** @param type superclass or interface of the component.
     * @param component will be injectable through the selected class. */
    public void add(final Class<?> type, final Object component) {
        components.get(type).add(component);
    }

    /** @param component will be mapped to its class tree. Note that mapping by interfaces is NOT supported, as
     *            interfaces are not available on GWT. */
    public void map(final Object component) {
        Class<?> componentClass = component.getClass();
        while (componentClass != null && !componentClass.equals(Object.class)) {
            components.get(componentClass).add(component);
            componentClass = componentClass.getSuperclass();
        }
    }

    /** @param componentClass class of the component.
     * @return true if at least one component is mapped to the passed class. */
    public boolean isPresent(final Class<?> componentClass) {
        return GdxArrays.isNotEmpty(components.get(componentClass));
    }

    /** @param componentClass class of the component.
     * @return a single component mapped to the class.
     * @throws GdxRuntimeException if no components or multiple components mapped to the selected class.
     * @see #isPresent(Class) */
    public Object getComponent(final Class<?> componentClass) {
        final Array<Object> componentsForClass = components.get(componentClass);
        if (GdxArrays.isEmpty(componentsForClass)) {
            throw new GdxRuntimeException("Component for class: " + componentClass + " not available in context.");
        } else if (GdxArrays.sizeOf(componentsForClass) > 1) {
            throw new GdxRuntimeException("Multiple components mapped to: " + componentClass + ". Be more specific.");
        }
        return componentsForClass.first();
    }

    /** @param componentClass class of the components.
     * @return all components currently mapped to the selected class. Might be empty. */
    public Array<Object> getAll(final Class<?> componentClass) {
        return components.get(componentClass);
    }

    /** @param dependencyClass required class.
     * @return true if there is a provider present for the selected class. */
    public boolean isProviderPresentFor(final Class<?> dependencyClass) {
        return providers.containsKey(dependencyClass);
    }

    /** @param dependencyClass required class.
     * @return an instance of the class. If there is a component of this class in the context, component will be
     *         returned. If there is a provider that provides instances of this class, provider's result will be
     *         returned. If {@link #setCreateMissingDependencies(boolean)} is set to true, a new instance of the class
     *         will be created with no-arg constructor. Otherwise, an exception is thrown.
     * @throws GdxRuntimeException if there are multiple components or providers mapped to the same class, if unable to
     *             create a new instance with no-arg constructor or if unable to provide an instance at all.
     * @param <Type> type of the provided object. */
    @SuppressWarnings("unchecked")
    public <Type> Type provide(final Class<Type> dependencyClass) {
        if (isPresent(dependencyClass)) {
            // Components are mapped by their type, safe to cast.
            return (Type) getComponent(dependencyClass);
        } else if (isProviderPresentFor(dependencyClass)) {
            return getProvider(dependencyClass).provide();
        } else if (createMissingDependencies) {
            return Reflection.newInstance(dependencyClass);
        }
        throw new GdxRuntimeException("Unable to provide an instance of: " + dependencyClass
                + ". Not available in context and no provider selected.");
    }

    /** @param provider registers provider for the class true of the provided type. */
    public void addProvider(final DependencyProvider<?> provider) {
        Class<?> depedencyClass = provider.getDependencyType();
        while (depedencyClass != null && !depedencyClass.equals(Object.class)) {
            providers.get(depedencyClass).add(provider);
            depedencyClass = depedencyClass.getSuperclass();
        }
    }

    /** @param dependencyClass requested class.
     * @return provider that provides instances of the requested class.
     * @param <Type> type of requested class objects provided by the returned provider.
     * @see #isPresent(Class)
     * @throws GdxRuntimeException if unable to select 1 provider for class. */
    @SuppressWarnings("unchecked")
    public <Type> DependencyProvider<Type> getProvider(final Class<Type> dependencyClass) {
        final Array<DependencyProvider<?>> providersForClass = providers.get(dependencyClass);
        if (GdxArrays.isEmpty(providersForClass)) {
            throw new GdxRuntimeException(
                    "Unable to return provider of: " + dependencyClass + ". No providers available for this class.");
        } else if (GdxArrays.sizeOf(providersForClass) != 1) {
            throw new GdxRuntimeException("Unable to return a provider of: " + dependencyClass
                    + ". Multiple providers mapped to the same type. Pass more specific class.");
        }
        return (DependencyProvider<Type>) providersForClass.first();
    }

    /** Clears context meta-data. Context object might become unusable after this call.
     *
     * @see #setClear(boolean) */
    public void clear() {
        if (clear) {
            GdxMaps.clearAll(components);
        }
    }
}
