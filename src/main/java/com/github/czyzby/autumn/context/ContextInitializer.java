package com.github.czyzby.autumn.context;

import java.lang.annotation.Annotation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.annotation.Dispose;
import com.github.czyzby.autumn.annotation.OnEvent;
import com.github.czyzby.autumn.annotation.OnMessage;
import com.github.czyzby.autumn.annotation.Processor;
import com.github.czyzby.autumn.annotation.Provider;
import com.github.czyzby.autumn.context.error.ContextInitiationException;
import com.github.czyzby.autumn.context.impl.method.ContextConsumer;
import com.github.czyzby.autumn.context.impl.method.MethodInvocation;
import com.github.czyzby.autumn.processor.AnnotationProcessor;
import com.github.czyzby.autumn.processor.event.EventDispatcher;
import com.github.czyzby.autumn.processor.event.MessageDispatcher;
import com.github.czyzby.autumn.processor.impl.ComponentAnnotationProcessor;
import com.github.czyzby.autumn.processor.impl.DestroyAnnotationProcessor;
import com.github.czyzby.autumn.processor.impl.DisposeAnnotationProcessor;
import com.github.czyzby.autumn.processor.impl.InitiateAnnotationProcessor;
import com.github.czyzby.autumn.processor.impl.InjectAnnotationProcessor;
import com.github.czyzby.autumn.processor.impl.MetaAnnotationProcessor;
import com.github.czyzby.autumn.processor.impl.ProviderAnnotationProcessor;
import com.github.czyzby.autumn.scanner.ClassScanner;
import com.github.czyzby.kiwi.util.common.Exceptions;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.kiwi.util.gdx.collection.lazy.LazyObjectMap;

/** A single-use context initializer object. Scans the selected packages for annotated classes and initiates them, using
 * registered {@link AnnotationProcessor}s. After {@link #initiate()} call, clears the context meta-data (to allow
 * proper garbage collection) and becomes unusable.
 *
 * @author MJ */
public class ContextInitializer {
    /* Annotations. */
    /** Annotations that are scanned for before other components. Usually processors. */
    private final Array<Class<? extends Annotation>> scannedMetaAnnotations = GdxArrays.newArray();
    /** Annotations of regular components. */
    private final Array<Class<? extends Annotation>> scannedAnnotations = GdxArrays.newArray();

    /* Annotation processors. */
    /** Contains all annotation processors. */
    private final Array<AnnotationProcessor<?>> processors = GdxArrays.newArray();
    /** Contains all annotation processors that were added manually and require initiation. */
    private final Array<AnnotationProcessor<?>> manuallyAddedProcessors = GdxArrays.newArray();
    /** These handle annotated fields. */
    private final ObjectMap<Class<? extends Annotation>, Array<AnnotationProcessor<?>>> fieldProcessors = LazyObjectMap
            .newMapOfArrays();
    /** These handle annotated methods. */
    private final ObjectMap<Class<? extends Annotation>, Array<AnnotationProcessor<?>>> methodProcessors = LazyObjectMap
            .newMapOfArrays();
    /** These handle annotated classes. */
    private final ObjectMap<Class<? extends Annotation>, Array<AnnotationProcessor<?>>> typeProcessors = LazyObjectMap
            .newMapOfArrays();

    /* Scanners, components. */
    /** Contains roots and class scanners that process the actual scanning. */
    private final IdentityMap<Class<?>, ClassScanner> scanners = GdxMaps.newIdentityMap();
    /** Contains components added with {@link #addComponent(Object)}. */
    private final Array<Object> manuallyAddedComponents = GdxArrays.newArray();

    /* Control variables. */
    /** Contains constructors of components that contain unresolved constructor dependencies. */
    private Array<Constructor> delayedConstructions = GdxArrays.newArray();
    /** If unable to resolve dependencies after this amount of iterations, context fails to build. */
    private int maxInitiationIterations = 100;
    /** See {@link Context#setCreateMissingDependencies(boolean)}. */
    private boolean createMissingDependencies = true;
    /** If true, scanners and processors are cleared after initiation. */
    private boolean clearProcessors = true;
    /** If true, components are removed from the context after initiation. They still hold references to each other
     * (through dependency injection), but the {@link Context} object will be empty. */
    private boolean clearContextAfterInitiation = true;
    /** Consumes constructed {@link Context} instance. */
    private ContextConsumer doBeforeInitiation;
    /** Consume fully initiated {@link Context} instance. */
    private ContextConsumer doAfterInitiation;

    /** Creates a new context initializer with default annotation processors. */
    public ContextInitializer() {
        // Default annotations:
        scannedMetaAnnotations.add(Processor.class);
        scannedMetaAnnotations.add(Provider.class);
        scannedAnnotations.add(Component.class);
        scannedAnnotations.add(Dispose.class);
        scannedAnnotations.add(OnEvent.class);
        scannedAnnotations.add(OnMessage.class);
        // Default processors:
        addProcessor(new ComponentAnnotationProcessor()); // Maps components by interfaces.
        addProcessor(new MetaAnnotationProcessor()); // Registers annotation processors.
        addProcessor(new ProviderAnnotationProcessor()); // Registers dependency providers.
        addProcessor(new InjectAnnotationProcessor()); // Injects field dependencies.
        addProcessor(new InitiateAnnotationProcessor()); // Invokes initiation methods.
        addProcessor(new DestroyAnnotationProcessor()); // Schedules destruction methods upon disposing.
        addProcessor(new DisposeAnnotationProcessor()); // Schedules disposing of fields and components.
        addProcessor(new EventDispatcher()); // Registers event listeners. Allows to post events.
        addProcessor(new MessageDispatcher()); // Registers message listeners. Allows to post messages.
    }

    /** Static factory method. Creates a new context initializer with default, no-arg constuctor.
     *
     * @return a new instance of ContextInitializer.
     * @see ContextInitializer#initiate() */
    public static ContextInitializer newContext() {
        return new ContextInitializer();
    }

    /** @param annotation classes annotated with this annotation will be searched for.
     * @return this for chaining. */
    public ContextInitializer scanFor(final Class<? extends Annotation> annotation) {
        scannedAnnotations.add(annotation);
        return this;
    }

    /** @param annotations classes annotated with these annotations will be searched for.
     * @return this for chaining. */
    public ContextInitializer scanFor(final Class<? extends Annotation>... annotations) {
        scannedAnnotations.addAll(annotations);
        return this;
    }

    /** @param metaAnnnotation classes annotated with this annotation will be searched for and initiated before others.
     *            Meta annotations are usually reserved for annotation processors - without adding processors first,
     *            other components will not be initiated properly.
     * @return this for chaining. */
    public ContextInitializer scanForMeta(final Class<? extends Annotation> metaAnnnotation) {
        scannedMetaAnnotations.add(metaAnnnotation);
        return this;
    }

    /** @param processors process fields, methods and types annotated with specific annotations. Processors will be also
     *            available in context for injection. This method is reserved for fully initiated processors or
     *            processors during initiation; if you need the processors to be fully initiated, use
     *            {@link #addComponents(Object...)} instead.
     * @return this for chaining. */
    public ContextInitializer addProcessors(final AnnotationProcessor<?>... processors) {
        for (final AnnotationProcessor<?> processor : processors) {
            addProcessor(processor);
        }
        return this;
    }

    /** @param processor processes fields, methods and types annotated with a specific annotation. Processor will be
     *            also available in context for injection. This method is reserved for fully initiated processors or
     *            processors during initiation; if you need a processor to be fully initiated, use
     *            {@link #addComponent(Object)} instead.
     * @return this for chaining. */
    public ContextInitializer addProcessor(final AnnotationProcessor<?> processor) {
        processors.add(processor);
        if (processor.isSupportingFields()) {
            fieldProcessors.get(processor.getSupportedAnnotationType()).add(processor);
        }
        if (processor.isSupportingMethods()) {
            methodProcessors.get(processor.getSupportedAnnotationType()).add(processor);
        }
        if (processor.isSupportingTypes()) {
            typeProcessors.get(processor.getSupportedAnnotationType()).add(processor);
        }
        return this;
    }

    /** @param maxInitiationIterations some components need dependencies for their constructors. They initiation is
     *            basically delayed, until the requested components appear in the context. There is no specialized
     *            mechanism of detecting circular constructor dependencies; instead, if after a certain number of
     *            iterations some components are still missing, it is assumed that the context cannot be built. This
     *            value is the said limit. It defaults to 100, which is more than enough for most - even big - contexts,
     *            but if you are SURE there are no circular references in your context and it still fails to build after
     *            100 iterations, change this value to a higher one.
     * @return this for chaining. */
    public ContextInitializer maxInitiationIterationsAmount(final int maxInitiationIterations) {
        this.maxInitiationIterations = maxInitiationIterations;
        return this;
    }

    /** @param createMissingDependencies if true, field and method dependencies with no components or providers in the
     *            concept will be created with default no-arg constructor. Defaults to true.
     * @return this for chaining.
     * @see Context#setCreateMissingDependencies(boolean) */
    public ContextInitializer createMissingDependencies(final boolean createMissingDependencies) {
        this.createMissingDependencies = createMissingDependencies;
        return this;
    }

    /** @param clearProcessors if true, scanners and annotation processors will be cleared just after creating context
     *            with {@link #initiate()}. This generally prevents from keeping unnecessary references to context
     *            meta-data. Defaults to true; change to false only if you plan to use the initializer multiple times
     *            and using separate initializers is not an option for some reason.
     * @return this for chaining. */
    public ContextInitializer clearProcessors(final boolean clearProcessors) {
        this.clearProcessors = clearProcessors;
        return this;
    }

    /**
     * @param clearContextAfterInitiation if true, all components from the {@link Context} instance will be removed
     * after the context is fully initiated.
     * @return this for chaining.
     * @see Context#setClear(boolean)
     */
    public ContextInitializer clearContextAfterInitiation(final boolean clearContextAfterInitiation) {
        this.clearContextAfterInitiation = clearContextAfterInitiation;
        return this;
    }

    /**
     * @param doBeforeInitiation will be invoked right after the {@link Context} is created. The consumed context
     * instance should be empty, but will never be null.
     * @return this for chaining.
     * @see ContextConsumer
     */
    public ContextInitializer doBeforeInitiation(final ContextConsumer doBeforeInitiation) {
        this.doBeforeInitiation = doBeforeInitiation;
        return this;
    }

    /**
     * @param doAfterInitiation will be invoked right after the {@link Context} is fully initiated, but before the
     * processors and components meta-data is cleared. The consumed context instance will never be null.
     * @return this for chaining.
     * @see ContextConsumer
     */
    public ContextInitializer doAfterInitiation(final ContextConsumer doAfterInitiation) {
        this.doAfterInitiation = doAfterInitiation;
        return this;
    }

    /** @param root scanning root. Will look for classes sharing the same root package.
     * @param scanner will process the actual class scanning.
     * @return this for chaining. */
    public ContextInitializer scan(final Class<?> root, final ClassScanner scanner) {
        scanners.put(root, scanner);
        return this;
    }

    /** @param component will be added to the context manually, rather than scanned for. Note that this method has any
     *            effect only BEFORE scanning, not DURING component processing: annotation processors should not invoke
     *            this method. If the added component is an annotation processor, it will be also added and will
     *            properly process annotations.
     * @return this for chaining. */
    public ContextInitializer addComponent(final Object component) {
        if (component instanceof AnnotationProcessor<?>) {
            final AnnotationProcessor<?> processor = (AnnotationProcessor<?>) component;
            manuallyAddedProcessors.add(processor);
            addProcessor(processor);
        } else {
            manuallyAddedComponents.add(component);
        }
        return this;
    }

    /** @param components will be added to the context manually, rather than scanned for. Note that this method has any
     *            effect only BEFORE scanning, not DURING component processing: annotation processors should not invoke
     *            this method. If any of the added components is an annotation processor, it will be also added and will
     *            properly process annotations.
     * @return this for chaining. */
    public ContextInitializer addComponents(final Object... components) {
        for (final Object component : components) {
            addComponent(component);
        }
        return this;
    }

    /** This is finalizing method that actually scans and initiates the context. After this method invocation, context
     * initialized becomes unusable; if - for some reason - you need to create multiple contexts, use separate
     * initializers. Make sure to add some class scanners and scanning roots before invoking this method - otherwise
     * there will be no classes to scan for.
     *
     * @return {@link ContextDestroyer} instance, implementing {@link com.badlogic.gdx.utils.Disposable} interface. On
     *         {@link ContextDestroyer#dispose()}, this object will invoke registered destruction methods (that might
     *         include invoking finalizing component methods or disposing of heavy objects). Make sure to dispose of
     *         this object before closing the application - or as soon as the context is no longer needed.
     * @see #scan(Class, ClassScanner) */
    public ContextDestroyer initiate() {
        validateScanners(); // Making sure scanners are properly defined.
        final Context context = createContext(); // Creating new instance, applying user's settings.
        final ContextDestroyer contextDestroyer = new ContextDestroyer();
        mapInContext(context, processors); // Now context contains default processors. They are injectable.
        mapInContext(context, manuallyAddedComponents); // Now manually added components are in the context.
        initiateMetaComponents(context, contextDestroyer); // Now context contains custom annotation processors.
        invokeProcessorActionsBeforeInitiation(); // Processors are ready to process!
        initiateRegularComponents(context, contextDestroyer);// Now context contains all regular components.
        invokeProcessorActionsAfterInitiation(context, contextDestroyer); // Processors finish up their work.
        finishContext(context); // Clearing processors and components.
        return contextDestroyer;
    }

    private Context createContext() {
        final Context context = new Context();
        context.setClear(clearContextAfterInitiation);
        context.setCreateMissingDependencies(createMissingDependencies);
        if (doBeforeInitiation != null) {
            doBeforeInitiation.handleContext(context);
            doBeforeInitiation = null;
        }
        return context;
    }

    private void finishContext(Context context) {
        if (doAfterInitiation != null) {
            doAfterInitiation.handleContext(context);
            doAfterInitiation = null;
        }
        context.clear(); // Removing all components from context (if not disabled)
        if (clearProcessors) {
            destroyInitializer(); // Clearing meta-data. Processors are no longer available.
        }
    }

    /** Throws exception if user did not specify any scanners. */
    private void validateScanners() {
        if (scanners.size == 0) {
            throw new ContextInitiationException(
                    "Cannot initiate context without any class scanners and scanning roots.");
        }
    }

    /** @param context will contain instances of passed components.
     * @param components will be mapped by their class tree. */
    private static void mapInContext(final Context context, final Iterable<?> components) {
        for (final Object component : components) {
            context.map(component);
        }
    }

    /** Calls {@link AnnotationProcessor#doBeforeScanning(ContextInitializer)} with "this" argument on each
     * processor. */
    private void invokeProcessorActionsBeforeInitiation() {
        for (final AnnotationProcessor<?> processor : processors) {
            processor.doBeforeScanning(this);
        }
    }

    /** Calls {@link AnnotationProcessor#doAfterScanning(ContextInitializer, Context, ContextDestroyer)} with "this"
     * argument on each processor.
     * @param context might be required by some processors to finish up.
     * @param destroyer used to register destruction callbacks. */
    private void invokeProcessorActionsAfterInitiation(final Context context, final ContextDestroyer destroyer) {
        for (final AnnotationProcessor<?> processor : processors) {
            processor.doAfterScanning(this, context, destroyer);
        }
    }

    /** @param context will contain instances of scanned annotation procesors.
     * @param contextDestroyer used to register destruction callbacks. */
    private void initiateMetaComponents(final Context context, final ContextDestroyer contextDestroyer) {
        final Array<Class<?>> metaComponentTypes = GdxArrays.newArray();
        for (final Entry<Class<?>, ClassScanner> scannerData : scanners) {
            metaComponentTypes
                    .addAll(scannerData.value.findClassesAnnotatedWith(scannerData.key, scannedMetaAnnotations));
        }
        final Array<Object> metaComponents = createComponents(metaComponentTypes, context);
        metaComponents.addAll(manuallyAddedProcessors);
        manuallyAddedProcessors.clear();
        initiateComponents(metaComponents, context, contextDestroyer);
    }

    /** @param context will contain instances of scanned components. This method will clear scanners, as they are no
     *            longer expected to be needed.
     * @param contextDestroyer used to register destruction callbacks. */
    private void initiateRegularComponents(final Context context, final ContextDestroyer contextDestroyer) {
        final Array<Class<?>> componentTypes = GdxArrays.newArray();
        for (final Entry<Class<?>, ClassScanner> scannerData : scanners) {
            componentTypes.addAll(scannerData.value.findClassesAnnotatedWith(scannerData.key, scannedAnnotations));
        }
        final Array<Object> components = createComponents(componentTypes, context);
        // Manually added components are already mapped. Now they will be initiated:
        components.addAll(manuallyAddedComponents);
        manuallyAddedComponents.clear();
        initiateComponents(components, context, contextDestroyer);
        // Scanners no longer needed, all components found:
        if (clearProcessors) {
            scanners.clear();
        }
    }

    /** Clears meta-data collections. */
    private void destroyInitializer() {
        GdxMaps.clearAll(fieldProcessors, methodProcessors, typeProcessors);
        GdxArrays.clearAll(scannedMetaAnnotations, scannedAnnotations, processors, delayedConstructions,
                manuallyAddedComponents, manuallyAddedProcessors);
    }

    /* COMPONENTS CREATIONS. Constructor management, resolving dependencies. */

    /** @param types classes of components to initiate.
     * @param context will contain components mapped by their classes tree.
     * @return an array containing all created components. */
    private Array<Object> createComponents(final Array<Class<?>> types, final Context context) {
        final Array<Object> components = GdxArrays.newArray();
        for (final Class<?> type : types) {
            final Constructor[] constructors = ClassReflection.getConstructors(type);
            if (constructors == null || constructors.length == 0) {
                throw new ContextInitiationException(
                        type + " has no available public constructors. Unable to create component.");
            }
            final Constructor constructor  = constructors.length == 1 ?
                    // Single constructor - trying to invoke it:
                    constructors[0] :
                    // Multiple constructors - trying to find a suitable one:
                    findSuitableConstructor(constructors);
            final Object component = processConstructor(constructor, context);
            if (component != null) {
                components.add(component);
            } else {
                delayedConstructions.add(constructor);
            }
        }
        int initiationIterations = 0;
        while (GdxArrays.isNotEmpty(delayedConstructions)) {
            validateIterationsAmount(initiationIterations);
            processDelayedConstructions(context, components);
            initiationIterations++;
        }
        return components;
    }

    /** @param initiationIterations if greater than {@link #maxInitiationIterations}, throws an an exception. */
    private void validateIterationsAmount(final int initiationIterations) {
        if (initiationIterations >= maxInitiationIterations) {
            throw new ContextInitiationException("Context could not have been built after " + initiationIterations
                    + " iterations. Some constructor dependencies are missing (not annotated, not available in the context) or some components have circular dependencies. Unable to invoke constructors: "
                    + GdxArrays.newArray(delayedConstructions));
        }
    }

    /** @param constructors an array of at least 2 constructors.
     * @return no-arg constructor or the first found constructor. */
    private static Constructor findSuitableConstructor(final Constructor[] constructors) {
        for (final Constructor constructor : constructors) {
            if (constructor.getParameterTypes().length == 0) {
                // Using no-arg constructor by default.
                return constructor;
            }
        }
        // Constructor annotations not available, so we can't just select a constructor with @Inject or whatever.
        final Constructor constructor = constructors[0];
        Gdx.app.error("WARN", constructor.getDeclaringClass()
                + " has multiple public constructors, but no public no-arg constructor. Using first found constructor to initiate component.");
        return constructor;
    }

    /** @param constructor if all its parameter types are available in context, will be invoked and the instance will be
     *            added to context.
     * @param context will be used to retrieve constructor parameters.
     * @return an instance of the component or null if it cannot be constructed yet. */
    private Object processConstructor(final Constructor constructor, final Context context) {
        Object component;
        if (constructor.getParameterTypes().length == 0) {
            // Passing any empty object array avoid unnecessary array allocation.
            component = invokeConstructor(constructor, Strings.EMPTY_ARRAY);
        } else {
            if (areContructorParametersAvailable(constructor, context)) {
                final Object[] parameters = MethodInvocation.getParametersFromContext(constructor.getParameterTypes(),
                        context);
                component = invokeConstructor(constructor, parameters);
            } else {
                return null;
            }
        }
        context.map(component);
        return component;
    }

    /** @param constructor its parameters will be validated.
     * @param context contains components.
     * @return true if all constructor parameters can be injected. */
    private static boolean areContructorParametersAvailable(final Constructor constructor, final Context context) {
        for (final Class<?> parameterType : constructor.getParameterTypes()) {
            if (!context.isPresent(parameterType) && !context.isProviderPresentFor(parameterType)) {
                return false;
            }
        }
        return true;
    }

    /** @param constructor will be invoked.
     * @param parameters will be used to invoke the constructor.
     * @return a new instance of the class.
     * @throws ContextInitiationException if unable to invoke the constructor. */
    private static Object invokeConstructor(final Constructor constructor, final Object[] parameters) {
        try {
            return constructor.newInstance(parameters);
        } catch (final ReflectionException exception) {
            throw new ContextInitiationException("Unable to create a new instance of class: "
                    + constructor.getDeclaringClass() + " with constructor: " + constructor + " with parameters: "
                    + GdxArrays.newArray(parameters), exception);
        }
    }

    /** @param context used to get constructor parameters.
     * @param components will contain created components. */
    private void processDelayedConstructions(final Context context, final Array<Object> components) {
        final Array<Constructor> stillMissingConstructions = GdxArrays.newArray();
        for (final Constructor constructor : delayedConstructions) {
            final Object component = processConstructor(constructor, context);
            if (component != null) {
                components.add(component);
            } else {
                stillMissingConstructions.add(constructor);
            }
        }
        delayedConstructions = stillMissingConstructions;
    }

    /* CONTEXT INITIATION. Processing types', methods' and fields' annotations. */

    /** @param components will be initiated.
     * @param context contains components. Used to resolve dependencies.
     * @param contextDestroyer used to register destruction callbacks. */
    private void initiateComponents(final Array<Object> components, final Context context,
            final ContextDestroyer contextDestroyer) {
        // Class annotations are usually the most important. They can change the way component is mapped or processed.
        for (final Object component : components) {
            processType(component, context, contextDestroyer);
        }
        // Fields annotations might be used to inject component's dependencies and change the state of the component, so
        // they should be processed before methods - which might actually depend on the fields.
        for (final Object component : components) {
            processFields(component, context, contextDestroyer);
        }
        // Methods are processed last, when the component is (almost) fully initiated and all method dependencies should
        // be present.
        for (final Object component : components) {
            processMethods(component, context, contextDestroyer);
        }
    }

    /** Processes components' class annotations.
     *
     * @param component its class annotations will be processed.
     * @param context used to resolve dependencies
     * @param contextDestroyer will register destruction callbacks. */
    @SuppressWarnings({ "rawtypes", "unchecked" }) // Using correct types, but wildcards fail to see that.
    private void processType(final Object component, final Context context, final ContextDestroyer contextDestroyer) {
        final com.badlogic.gdx.utils.reflect.Annotation[] annotations = getAnnotations(component.getClass());
        if (annotations == null || annotations.length == 0) {
            return;
        }
        for (final com.badlogic.gdx.utils.reflect.Annotation annotation : annotations) {
            if (typeProcessors.containsKey(annotation.getAnnotationType())) {
                final Array<AnnotationProcessor<?>> typeProcessorsForAnnotation = typeProcessors
                        .get(annotation.getAnnotationType());
                // This might get resized in the process, so we're not using an iterator or assigning size.
                for (int index = 0; index < typeProcessorsForAnnotation.size; index++) {
                    final AnnotationProcessor processor = typeProcessorsForAnnotation.get(index);
                    processor.processType(component.getClass(),
                            annotation.getAnnotation(annotation.getAnnotationType()), component, context, this,
                            contextDestroyer);
                }
            }
        }
    }

    /** @param type will return an array of its annotations.
     * @return array of annotations or null. GWT utility. */
    private static com.badlogic.gdx.utils.reflect.Annotation[] getAnnotations(final Class<?> type) {
        try {
            return ClassReflection.getAnnotations(type);
        } catch (final Exception exception) {
            Exceptions.ignore(exception);
            return null;
        }
    }

    /** Scans class tree of component to process all its methods.
     *
     * @param component all methods of its class tree will be processed.
     * @param context used to resolve dependencies.
     * @param contextDestroyer used to register destruction callbacks. */
    private void processMethods(final Object component, final Context context,
            final ContextDestroyer contextDestroyer) {
        Class<?> componentClass = component.getClass();
        while (componentClass != null && !componentClass.equals(Object.class)) {
            final Method[] methods = ClassReflection.getDeclaredMethods(componentClass);
            if (methods != null && methods.length > 0) {
                processMethods(component, methods, context, contextDestroyer);
            }
            componentClass = componentClass.getSuperclass();
        }
    }

    /** Does the actual processing of found methods.
     *
     * @param component owner of the methods.
     * @param methods present in one of superclasses of the component.
     * @param context used to resolve dependencies.
     * @param contextDestroyer used to register destruction callbacks. */
    @SuppressWarnings({ "rawtypes", "unchecked" }) // Using correct types, but wildcards fail to see that.
    private void processMethods(final Object component, final Method[] methods, final Context context,
            final ContextDestroyer contextDestroyer) {
        for (final Method method : methods) {
            final com.badlogic.gdx.utils.reflect.Annotation[] annotations = getAnnotations(method);
            if (annotations == null || annotations.length == 0) {
                continue;
            }
            for (final com.badlogic.gdx.utils.reflect.Annotation annotation : annotations) {
                if (methodProcessors.containsKey(annotation.getAnnotationType())) {
                    for (final AnnotationProcessor processor : methodProcessors.get(annotation.getAnnotationType())) {
                        processor.processMethod(method, annotation.getAnnotation(annotation.getAnnotationType()),
                                component, context, this, contextDestroyer);
                    }
                }
            }
        }
    }

    /** @param method will return an array of its annotations.
     * @return array of annotations or null. GWT utility. */
    private static com.badlogic.gdx.utils.reflect.Annotation[] getAnnotations(final Method method) {
        try {
            return method.getDeclaredAnnotations();
        } catch (final Exception exception) {
            Exceptions.ignore(exception);
            return null;
        }
    }

    /** Scans class tree of component to process all its fields.
     *
     * @param component all fields of its class tree will be processed.
     * @param context used to resolve dependencies.
     * @param contextDestroyer used to register destruction callbacks. */
    private void processFields(final Object component, final Context context, final ContextDestroyer contextDestroyer) {
        Class<?> componentClass = component.getClass();
        while (componentClass != null && !componentClass.equals(Object.class)) {
            final Field[] fields = ClassReflection.getDeclaredFields(componentClass);
            if (fields != null && fields.length > 0) {
                processFields(component, fields, context, contextDestroyer);
            }
            componentClass = componentClass.getSuperclass();
        }
    }

    /** Does the actual processing of found fields.
     *
     * @param component owner of the fields.
     * @param fields present in one of superclasses of the component.
     * @param context used to resolve dependencies.
     * @param contextDestroyer used to register destruction callbacks. */
    @SuppressWarnings({ "rawtypes", "unchecked" }) // Using correct types, but wildcards fail to see that.
    private void processFields(final Object component, final Field[] fields, final Context context,
            final ContextDestroyer contextDestroyer) {
        for (final Field field : fields) {
            final com.badlogic.gdx.utils.reflect.Annotation[] annotations = getAnnotations(field);
            if (annotations == null || annotations.length == 0) {
                continue;
            }
            for (final com.badlogic.gdx.utils.reflect.Annotation annotation : annotations) {
                if (fieldProcessors.containsKey(annotation.getAnnotationType())) {
                    for (final AnnotationProcessor processor : fieldProcessors.get(annotation.getAnnotationType())) {
                        processor.processField(field, annotation.getAnnotation(annotation.getAnnotationType()),
                                component, context, this, contextDestroyer);
                    }
                }
            }
        }
    }

    /** @param field will return an array of its annotations.
     * @return array of annotations or null. GWT utility. */
    private static com.badlogic.gdx.utils.reflect.Annotation[] getAnnotations(final Field field) {
        try {
            return field.getDeclaredAnnotations();
        } catch (final Exception exception) {
            Exceptions.ignore(exception);
            return null;
        }
    }
}
