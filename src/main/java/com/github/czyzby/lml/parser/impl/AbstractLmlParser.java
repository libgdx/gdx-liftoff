package com.github.czyzby.lml.parser.impl;

import java.lang.annotation.Annotation;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.czyzby.kiwi.util.common.Nullables;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.pooled.PooledList;
import com.github.czyzby.kiwi.util.gdx.reflection.Annotations;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.annotation.LmlAfter;
import com.github.czyzby.lml.annotation.LmlBefore;
import com.github.czyzby.lml.annotation.LmlInject;
import com.github.czyzby.lml.annotation.OnChange;
import com.github.czyzby.lml.annotation.processor.OnChangeProcessor;
import com.github.czyzby.lml.parser.LmlData;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.LmlParserListener;
import com.github.czyzby.lml.parser.LmlStyleSheet;
import com.github.czyzby.lml.parser.LmlSyntax;
import com.github.czyzby.lml.parser.LmlTemplateReader;
import com.github.czyzby.lml.parser.LmlView;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.github.czyzby.lml.parser.action.ActionContainerWrapper;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.impl.action.FieldActorConsumer;
import com.github.czyzby.lml.parser.impl.action.MethodActorConsumer;
import com.github.czyzby.lml.util.Lml;
import com.github.czyzby.lml.util.LmlParsingException;
import com.github.czyzby.lml.util.LmlUtilities;
import com.github.czyzby.lml.util.collection.IgnoreCaseStringMap;

/** Abstract base for all LML parsers. Manages required getters and setters. Allows to focus on the actual parsing
 * implementation by providing abstract method {@link #parseTemplate()} and implementing all specific parsing methods
 * with the expected behavior.
 *
 * @author MJ */
public abstract class AbstractLmlParser implements LmlParser {
    // LML containers, processors and utilities:
    protected LmlData data;
    protected LmlSyntax syntax;
    protected LmlTemplateReader templateReader;
    protected LmlStyleSheet styleSheet;

    // Settings:
    protected boolean strict;
    protected boolean nestedComments;
    private int debugLines = 3;

    // Listeners:
    private final PooledList<LmlParserListener> preListeners = PooledList.newList();
    private final PooledList<LmlParserListener> postListeners = PooledList.newList();

    // Cached parsing results:
    protected final ObjectMap<String, Actor> actorsByIds = createActorsByIdsMap();

    /** @param data contains skin, actions, i18n bundles and other data needed to parse LML templates.
     * @param syntax determines syntax of LML templates.
     * @param templateReader reads and buffers templates and their files.
     * @param styleSheet contains default values of attributes.
     * @param strict if false, will ignore some unexpected errors, like unknown attributes, invalid referenced method
     *            names etc. Set to true for more HTML-like feel or quick prototyping. */
    public AbstractLmlParser(final LmlData data, final LmlSyntax syntax, final LmlTemplateReader templateReader,
            final LmlStyleSheet styleSheet, final boolean strict) {
        this.data = data;
        this.syntax = syntax;
        this.templateReader = templateReader;
        this.styleSheet = styleSheet;
        this.strict = strict;
    }

    /** @return a new instance of map that will hold actors mapped by their IDs. Returns an ignoring case map by
     *         default. Warning: invoked during construction. */
    protected ObjectMap<String, Actor> createActorsByIdsMap() {
        return new IgnoreCaseStringMap<Actor>();
    }

    @Override
    public void setStrict(final boolean strict) {
        this.strict = strict;
    }

    @Override
    public boolean isStrict() {
        return strict;
    }

    @Override
    public void setNestedComments(final boolean nestedComments) {
        this.nestedComments = nestedComments;
    }

    @Override
    public void setData(final LmlData lmlData) {
        data = lmlData;
    }

    @Override
    public LmlData getData() {
        return data;
    }

    @Override
    public void setTemplateReader(final LmlTemplateReader templateReader) {
        this.templateReader = templateReader;
    }

    @Override
    public LmlTemplateReader getTemplateReader() {
        return templateReader;
    }

    @Override
    public void setSyntax(final LmlSyntax lmlSyntax) {
        syntax = lmlSyntax;
    }

    @Override
    public LmlSyntax getSyntax() {
        return syntax;
    }

    @Override
    public void setStyleSheet(final LmlStyleSheet styleSheet) {
        this.styleSheet = styleSheet;
    }

    @Override
    public LmlStyleSheet getStyleSheet() {
        return styleSheet;
    }

    @Override
    public void doBeforeParsing(final LmlParserListener listener) {
        preListeners.add(listener);
    }

    @Override
    public void doAfterParsing(final LmlParserListener listener) {
        postListeners.add(listener);
    }

    /** This method must be invoked before template parsing.
     *
     * @param result empty result array that will be returned by parsing method. */
    protected void invokePreListeners(final Array<Actor> result) {
        invokeListeners(preListeners, result);
    }

    /** This method must be invoked after template parsing.
     *
     * @param result contains the parsed root actors. */
    protected void invokePortListeners(final Array<Actor> result) {
        invokeListeners(postListeners, result);
    }

    /** @param listeners will be invoked. Listeners returning false on
     *            {@link LmlParserListener#onEvent(LmlParser, Array)} will be removed.
     * @param result parsed actors array in its current state. */
    protected void invokeListeners(final PooledList<LmlParserListener> listeners, final Array<Actor> result) {
        for (final LmlParserListener listener : listeners) {
            if (!listener.onEvent(this, result)) {
                listeners.remove();
            }
        }
    }

    /** Actual implementation of LML template parsing. Template is already passed to the template reader and is ready to
     * be read and processed.
     *
     * @return parsed actors. */
    protected abstract Array<Actor> parseTemplate();

    @Override
    public Array<Actor> parseTemplate(final String lmlTemplate) {
        templateReader.append(lmlTemplate, "original template passed as string");
        return parseTemplate();
    }

    @Override
    public Array<Actor> parseTemplate(final FileHandle lmlTemplateFile) {
        templateReader.append(lmlTemplateFile);
        return parseTemplate();
    }

    @Override
    public void parseStyleSheet(final FileHandle styleSheetFile) {
        parseStyleSheet(styleSheetFile.readString("UTF-8"));
    }

    @Override
    public void parseStyleSheet(final String styleSheet) {
        new LssParser(this).parse(styleSheet);
    }

    @Override
    public void fillStage(final Stage stage, final String lmlTemplate) {
        LmlUtilities.appendActorsToStage(stage, parseTemplate(lmlTemplate));
    }

    @Override
    public void fillStage(final Stage stage, final FileHandle lmlTemplateFile) {
        LmlUtilities.appendActorsToStage(stage, parseTemplate(lmlTemplateFile));
    }

    @Override
    public <View> Array<Actor> createView(final View view, final String lmlTemplate) {
        doBeforeViewTemplateParsing(view);
        final Array<Actor> actors = parseTemplate(lmlTemplate);
        fillView(view, actors);
        doAfterViewTemplateParsing(view);
        return actors;
    }

    @Override
    public <View> Array<Actor> createView(final View view, final FileHandle lmlTemplateFile) {
        doBeforeViewTemplateParsing(view);
        final Array<Actor> actors = parseTemplate(lmlTemplateFile);
        fillView(view, actors);
        doAfterViewTemplateParsing(view);
        return actors;
    }

    @Override
    public <View> View createView(final Class<View> viewClass, final String lmlTemplate) {
        final View view = Reflection.newInstance(viewClass);
        createView(view, lmlTemplate);
        return view;
    }

    @Override
    public <View> View createView(final Class<View> viewClass, final FileHandle lmlTemplateFile) {
        final View view = Reflection.newInstance(viewClass);
        createView(view, lmlTemplateFile);
        return view;
    }

    /** @param view by default, registers the view as an {@link ActionContainer} and {@link ActorConsumer} if it
     *            implements any of these interfaces. Will have {@link LmlBefore}-annotated methods invoked.
     * @param <View> class of the managed view. */
    protected <View> void doBeforeViewTemplateParsing(final View view) {
        if (view instanceof LmlView) {
            final String containerId = ((LmlView) view).getViewId();
            if (view instanceof ActionContainer) {
                data.addActionContainer(containerId, (ActionContainer) view);
            }
            if (view instanceof ActorConsumer<?, ?>) {
                data.addActorConsumer(containerId, (ActorConsumer<?, ?>) view);
            }
        } else {
            if (view instanceof ActionContainer) {
                data.addActionContainer(view.getClass().getSimpleName(), (ActionContainer) view);
            }
            if (view instanceof ActorConsumer<?, ?>) {
                data.addActorConsumer(view.getClass().getSimpleName(), (ActorConsumer<?, ?>) view);
            }
        }
        invokeAnnotatedViewMethods(view, LmlBefore.class);
    }

    /** @param view if implements {@link LmlView}, actors will be added to its stage. Its field annotations will be
     *            processed.
     * @param actors result of template parsing.
     * @param <View> class of the filled view. */
    protected <View> void fillView(final View view, final Array<Actor> actors) {
        if (view instanceof LmlView) {
            final Stage stage = ((LmlView) view).getStage();
            if (stage != null) {
                LmlUtilities.appendActorsToStage(stage, actors);
            }
        }
        processViewFieldAnnotations(view);
    }

    /** @param view by default, unregisters the view as an {@link ActionContainer} and {@link ActorConsumer} if it
     *            implements these interfaces. Invokes {@link LmlAfter}-annotated methods.
     * @param <View> class of the managed view. */
    protected <View> void doAfterViewTemplateParsing(final View view) {
        if (view instanceof LmlView) {
            final String containerId = ((LmlView) view).getViewId();
            if (view instanceof ActionContainer) {
                data.removeActionContainer(containerId);
            }
            if (view instanceof ActorConsumer<?, ?>) {
                data.removeActorConsumer(containerId);
            }
        } else {
            if (view instanceof ActionContainer) {
                data.removeActionContainer(view.getClass().getSimpleName());
            }
            if (view instanceof ActorConsumer<?, ?>) {
                data.removeActorConsumer(view.getClass().getSimpleName());
            }
        }
        invokeAnnotatedViewMethods(view, LmlAfter.class);
    }

    // LmlBefore + LmlAfter support:

    protected <View> void invokeAnnotatedViewMethods(final View view, final Class<? extends Annotation> annotation) {
        Class<?> handledClass = view.getClass();
        try {
            while (handledClass != null && !handledClass.equals(Object.class)) {
                for (final Method method : ClassReflection.getDeclaredMethods(handledClass)) {
                    if (Reflection.isAnnotationPresent(method, annotation)) {
                        invokeAnnotatedViewMethod(view, method);
                    }
                }
                handledClass = handledClass.getSuperclass();
            }
        } catch (final Exception exception) {
            throw new GdxRuntimeException("Unable to invoke method annotated with: " + annotation, exception);
        }
    }

    protected <View> void invokeAnnotatedViewMethod(final View view, final Method method) throws ReflectionException {
        final Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes == null || parameterTypes.length == 0) {
            Reflection.invokeMethod(method, view, (Object[]) Strings.EMPTY_ARRAY);
        } else if (parameterTypes.length == 1 && LmlParser.class.equals(parameterTypes[0])) {
            Reflection.invokeMethod(method, view, new Object[] { this });
        } else {
            throw new GdxRuntimeException(
                    "Only no-arg or single-arg methods consuming LmlParser can be annotated. Found invalid args on annotated method: "
                            + method);
        }
    }

    // LmlActor + OnChange + LmlInject support:

    protected <View> void processViewFieldAnnotations(final View view) {
        Class<?> handledClass = view.getClass();
        while (handledClass != null && !handledClass.equals(Object.class)) {
            for (final Field field : ClassReflection.getDeclaredFields(handledClass)) {
                processLmlActorAnnotation(view, field);
                processOnChangeAnnotation(view, field);
                processLmlInjectAnnotation(view, field);
            }
            handledClass = handledClass.getSuperclass();
        }
    }

    protected <View> void processLmlActorAnnotation(final View view, final Field field) {
        if (Reflection.isAnnotationPresent(field, LmlActor.class)) {
            final LmlActor actorData = Reflection.getAnnotation(field, LmlActor.class);
            String[] actorIds = actorData.value();
            if (actorIds.length == 0) {
                actorIds = new String[] { field.getName() };
            }
            if (Reflection.isExtending(field.getType(), Array.class)) {
                injectArrayOfActors(view, field, actorIds);
            } else if (Reflection.isExtending(field.getType(), ObjectSet.class)) {
                injectSetOfActors(view, field, actorIds);
            } else if (Reflection.isExtending(field.getType(), ObjectMap.class)) {
                injectMapOfActors(view, field, actorIds);
            } else {
                injectSingleActor(view, field, actorIds);
            }
        }
    }

    private <View> void injectSingleActor(final View view, final Field field, final String[] actorIds) {
        // Converting, as the ID might be a bundle line, preference or argument.
        final Array<String> ids = convertActorIds(actorIds);
        if (GdxArrays.sizeOf(ids) != 1) {
            // Found too many IDs.
            throwErrorIfStrict(
                    "Invalid amount of actors passed to LmlActor annotation. If you want to inject multiple actors, use Array, ObjectSet or ObjectMap field type. Found: \""
                            + ids + "\" after parsing on field: " + field + " of view: " + view);
        }
        injectFieldValueGracefully(field, view, actorsByIds.get(ids.first()));
    }

    protected <View> void injectMapOfActors(final View view, final Field field, final String[] actorIds) {
        final ObjectMap<String, Actor> actorContainer = new IgnoreCaseStringMap<Actor>();
        for (final String actorId : convertActorIds(actorIds)) {
            actorContainer.put(actorId, actorsByIds.get(actorId));
        }
        injectFieldValueGracefully(field, view, actorContainer);
    }

    protected <View> void injectSetOfActors(final View view, final Field field, final String[] actorIds) {
        final ObjectSet<Actor> actorContainer = new ObjectSet<Actor>();
        for (final String actorId : convertActorIds(actorIds)) {
            final Actor actor = actorsByIds.get(actorId);
            if (actor != null) {
                actorContainer.add(actor);
            }
        }
        injectFieldValueGracefully(field, view, actorContainer);
    }

    protected <View> void injectArrayOfActors(final View view, final Field field, final String[] actorIds) {
        final Array<Actor> actorContainer = new Array<Actor>();
        for (final String actorId : convertActorIds(actorIds)) {
            actorContainer.add(actorsByIds.get(actorId));
        }
        injectFieldValueGracefully(field, view, actorContainer);
    }

    /** @param actorIds each element of this array might be a LML array which needs to be processed.
     * @return parsed array. */
    protected Array<String> convertActorIds(final String[] actorIds) {
        final Array<String> ids = GdxArrays.newArray(actorIds.length);
        for (final String actorId : actorIds) {
            // This will parse the probable array and append all its elements into "ids". If the ID is a plain string,
            // it will be just added to the "ids" array, no harm done:
            parseArrayElements(ids, Strings.split(actorId, syntax.getArrayElementSeparator()), null);
        }
        return ids;
    }

    /** @param field field to be filed.
     * @param fieldOwner instance of class containing the field.
     * @param value value to be injected.
     * @throws GdxRuntimeException if unable to set. */
    protected static void injectFieldValueGracefully(final Field field, final Object fieldOwner, final Object value) {
        try {
            Reflection.setFieldValue(field, fieldOwner, value);
        } catch (final ReflectionException exception) {
            throw new GdxRuntimeException(
                    "Unable to set field value: " + value + " of field: " + field + " in object: " + fieldOwner,
                    exception);
        }
    }

    protected <View> void processOnChangeAnnotation(final View view, final Field field) {
        if (Reflection.isAnnotationPresent(field, OnChange.class)) {
            final Actor actor = actorsByIds.get(Reflection.getAnnotation(field, OnChange.class).value());
            for (final OnChangeProcessor onChangeProcessor : data.getOnChangeProcessors()) {
                if (onChangeProcessor.canProcess(field, actor)) {
                    onChangeProcessor.process(field, view, actor);
                    return;
                }
            }
        }
    }

    protected <View> void processLmlInjectAnnotation(final View view, final Field field) {
        if (Reflection.isAnnotationPresent(field, LmlInject.class)) {
            try {
                final LmlInject injectionData = Reflection.getAnnotation(field, LmlInject.class);
                final Class<?> injectedValueType = getLmlInjectedValueType(field, injectionData);
                if (LmlParser.class.equals(injectedValueType)) {
                    // Injected type equals LmlParser - parser injection was requested:
                    Reflection.setFieldValue(field, view, this);
                    return;
                }
                Object value = Reflection.getFieldValue(field, view);
                if (value == null || injectionData.newInstance()) {
                    value = Reflection.newInstance(injectedValueType);
                    Reflection.setFieldValue(field, view, value);
                }
                // Processing field's value annotations:
                processViewFieldAnnotations(value);
            } catch (final ReflectionException exception) {
                throw new GdxRuntimeException(
                        "Unable to inject value of LmlInject-annotated field: " + field + " of view: " + view,
                        exception);
            }
        }
    }

    protected Class<?> getLmlInjectedValueType(final Field field, final LmlInject injectionData) {
        if (Annotations.isNotVoid(injectionData.value())) {
            return injectionData.value();
        }
        return field.getType();
    }

    @Override
    public boolean parseBoolean(final String rawLmlData) {
        return parseBoolean(rawLmlData, null);
    }

    @Override
    public boolean parseBoolean(final String rawLmlData, final Object forActor) {
        final String parsedData = parseString(rawLmlData, forActor).trim();
        try {
            if (strict) {
                // If parser is strict, value has to match exactly "true" or "false".
                if (Boolean.TRUE.toString().equalsIgnoreCase(parsedData)) {
                    return true;
                } else if (Boolean.FALSE.toString().equalsIgnoreCase(parsedData)) {
                    return false;
                }
                throwError("Boolean values are expected to be equal to true or false, ignoring case. Received: "
                        + parsedData);
            }
            // Returns true for ignore-case "true"; any other string is false. Non-strict behavior.
            return Boolean.parseBoolean(parsedData);
        } catch (final Exception exception) {
            throwError("Cannot parse boolean.", exception);
            return false; // Never happens.
        }
    }

    @Override
    public float parseFloat(final String rawLmlData) {
        return parseFloat(rawLmlData, null);
    }

    @Override
    public float parseFloat(final String rawLmlData, final Object forActor) {
        final String parsedData = parseString(rawLmlData, forActor);
        try {
            return Float.parseFloat(parsedData.trim());
        } catch (final Exception exception) {
            throwError("Cannot parse float.", exception);
            return 0f; // Never happens.
        }
    }

    @Override
    public int parseInt(final String rawLmlData) {
        return parseInt(rawLmlData, null);
    }

    @Override
    public int parseInt(final String rawLmlData, final Object forActor) {
        final String parsedData = parseString(rawLmlData, forActor);
        try {
            return Integer.parseInt(parsedData.trim());
        } catch (final Exception exception) {
            throwError("Cannot parse int.", exception);
            return 0; // Never happens.
        }
    }

    @Override
    public String parseString(final String rawLmlData) {
        return parseString(rawLmlData, null);
    }

    @Override
    public String parseString(final String rawLmlData, final Object forActor) {
        if (Strings.isShortherThan(rawLmlData, 2)) {
            return rawLmlData; // At least 2 characters are needed for bundles, preferences, etc.
        } else if (Strings.startsWith(rawLmlData, syntax.getBundleLineMarker())) {
            return parseBundleLine(rawLmlData, forActor);
        } else if (Strings.startsWith(rawLmlData, syntax.getPreferenceMarker())) {
            return parsePreference(rawLmlData);
        } else if (Strings.startsWith(rawLmlData, syntax.getMethodInvocationMarker())) {
            final ActorConsumer<?, Object> action = parseAction(rawLmlData, forActor);
            if (action == null) {
                throwError("Value: " + rawLmlData + " references an action, but none was found.");
            }
            return Nullables.toString(action.consume(forActor));
        }
        return rawLmlData;
    }

    /** @param rawLmlData unparsed LML data that might or might not start with bundle line key (which will be stripped).
     * @param actor might be required to parse some of the bundle line arguments.
     * @return formatted bundle line. */
    protected String parseBundleLine(final String rawLmlData, final Object actor) {
        String bundleKey = LmlUtilities.stripMarker(rawLmlData, syntax.getBundleLineMarker());
        final I18NBundle bundle;
        if (Strings.contains(rawLmlData, syntax.getIdSeparatorMarker())) {
            // Bundle name is given, as bundle key contains separator. Extracting specific bundle.
            final int separatorIndex = bundleKey.indexOf(syntax.getIdSeparatorMarker());
            final String bundleName = bundleKey.substring(0, separatorIndex);
            bundle = data.getI18nBundle(bundleName);
            bundleKey = bundleKey.substring(separatorIndex + 1, bundleKey.length());
        } else {
            // No specific bundle name. Using default bundle.
            bundle = data.getDefaultI18nBundle();
        }
        if (bundle == null) {
            throwError("I18N bundle not found for bundle line: " + rawLmlData);
        }
        try {
            if (Strings.contains(rawLmlData, syntax.getBundleLineArgumentMarker())) {
                return parseBundleLineWithArguments(bundle, bundleKey, actor);
            }
            return Nullables.toString(bundle.get(bundleKey));
        } catch (final Exception exception) {
            throwErrorIfStrict("Unable to find bundle line for data: " + rawLmlData, exception);
            return Nullables.DEFAULT_NULL_STRING;
        }
    }

    /** @param bundle should contain the passed key.
     * @param bundleKey contains at least one bundle argument marker and should be properly separated and parsed. Cannot
     *            begin with bundle key marker.
     * @param actor might be required to parse some of the bundle arguments.
     * @return formatted bundle line with arguments. */
    protected String parseBundleLineWithArguments(final I18NBundle bundle, final String bundleKey, final Object actor) {
        final String[] keyAndArguments = Strings.split(bundleKey, syntax.getBundleLineArgumentMarker());
        final String key = keyAndArguments[0];
        final String[] arguments = new String[keyAndArguments.length - 1];
        for (int index = 1, length = keyAndArguments.length; index < length; index++) {
            arguments[index - 1] = parseString(keyAndArguments[index], actor);
        }
        return Nullables.toString(bundle.format(key, (Object[]) arguments));
    }

    /** @param rawLmlData unparsed LML data that might or might not start with preference key (which will be stripped).
     * @return parsed preference value. */
    protected String parsePreference(final String rawLmlData) {
        String preferenceKey = LmlUtilities.stripMarker(rawLmlData, syntax.getPreferenceMarker());
        final Preferences preferences;
        if (Strings.contains(rawLmlData, syntax.getIdSeparatorMarker())) {
            // Preferences name is given, as preference key contains separator. Extracting specific preferences.
            final int separatorIndex = preferenceKey.indexOf(syntax.getIdSeparatorMarker());
            final String preferencesName = preferenceKey.substring(0, separatorIndex);
            preferences = data.getPreferences(preferencesName);
            preferenceKey = preferenceKey.substring(separatorIndex + 1, preferenceKey.length());
        } else {
            // No specific preferences name. Using default preferences.
            preferences = data.getDefaultPreferences();
        }
        if (preferences == null) {
            throwError("Preferences container not found for preference: " + rawLmlData);
        }
        return preferences.getString(preferenceKey, Nullables.DEFAULT_NULL_STRING);
    }

    @Override
    public ActorConsumer<?, Object> parseAction(final String rawLmlData) {
        return parseAction(rawLmlData, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <ActorType> ActorConsumer<?, ActorType> parseAction(final String rawLmlData, final ActorType forActor) {
        String actionId = LmlUtilities.stripMarker(rawLmlData, syntax.getMethodInvocationMarker());
        final ActorConsumer<?, ?> namedActorConsumer = data.getActorConsumer(actionId);
        if (namedActorConsumer != null) {
            return (ActorConsumer<?, ActorType>) namedActorConsumer;
        }
        if (Strings.contains(rawLmlData, syntax.getIdSeparatorMarker())) {
            // Data contains an action container ID. Getting specific action container.
            final int separatorIndex = actionId.indexOf(syntax.getIdSeparatorMarker());
            final String containerName = actionId.substring(0, separatorIndex);
            final ActionContainerWrapper actionContainer = data.getActionContainer(containerName);
            actionId = actionId.substring(separatorIndex + 1, actionId.length());
            return (ActorConsumer<?, ActorType>) extractActionFromContainer(actionContainer, actionId, forActor);
        }
        // No action container ID. Searching through all action containers.
        for (final ActionContainerWrapper actionContainer : data.getActionContainers()) {
            final ActorConsumer<?, ?> action = extractActionFromContainer(actionContainer, actionId, forActor);
            if (action != null) {
                return (ActorConsumer<?, ActorType>) action;
            }
        }
        return null;
    }

    /** @param actionContainer action container that might contain the referenced method.
     * @param actionId name of the requested action.
     * @param forActor will be used as potential action argument.
     * @return actor consumer constructed with container's method (or field) or null if action not found. */
    protected ActorConsumer<?, ?> extractActionFromContainer(final ActionContainerWrapper actionContainer,
            final String actionId, final Object forActor) {
        Method method = actionContainer.getNamedMethod(actionId);
        if (method == null && Lml.EXTRACT_UNANNOTATED_METHODS) {
            method = findUnnamedMethod(actionContainer, actionId, forActor);
        }
        if (method != null) {
            return new MethodActorConsumer(method, actionContainer.getActionContainer());
        } else if (Lml.EXTRACT_FIELDS_AS_METHODS) {
            Field field = actionContainer.getNamedField(actionId);
            if (field == null && Lml.EXTRACT_UNANNOTATED_METHODS) {
                field = actionContainer.getField(actionId);
            }
            if (field != null) {
                return new FieldActorConsumer(field, actionContainer.getActionContainer());
            }
        }
        return null;
    }

    /** @param actionContainer may contain the action with the passed ID, but it was not annotated and assigned to a
     *            specific ID.
     * @param actionId name of the method.
     * @param forActor will search through its class tree to look for methods that might consume an argument of this
     *            type. Can be null - will look only for no-arg method.
     * @return method with the selected name or null if none found. */
    protected Method findUnnamedMethod(final ActionContainerWrapper actionContainer, final String actionId,
            final Object forActor) {
        Method method = null;
        Class<?> parameterType = forActor == null ? null : forActor.getClass();
        while (method == null) {
            if (parameterType == null) {
                method = actionContainer.getMethod(actionId, null);
                break;
            }
            method = actionContainer.getMethod(actionId, parameterType);
            parameterType = parameterType.getSuperclass();
        }
        return method;
    }

    @Override
    public String[] parseArray(final String rawLmlData) {
        return parseArray(rawLmlData, null);
    }

    @Override
    public String[] fullyParseArray(final String rawLmlData) {
        return fullyParseArray(rawLmlData, null);
    }

    @Override
    public String[] parseArray(final String rawLmlData, final Object forActor) {
        if (Strings.isEmpty(rawLmlData)) {
            return Strings.EMPTY_ARRAY;
        }
        final Array<String> array = GdxArrays.newArray(String.class);
        final String[] arrayElements = Strings.split(rawLmlData, syntax.getArrayElementSeparator());
        parseArrayElements(array, arrayElements, forActor);
        return array.toArray();
    }

    @Override
    public String[] fullyParseArray(final String rawLmlData, final Object forActor) {
        final String[] values = parseArray(rawLmlData, forActor);
        for (int index = 0, length = values.length; index < length; index++) {
            values[index] = parseString(values[index], forActor);
        }
        return values;
    }

    /** @param resultArray will contain parsed elements.
     * @param unparsedArrayElements unparsed elements, ready to be processed.
     * @param forActor requests array parsing. */
    protected void parseArrayElements(final Array<String> resultArray, final String[] unparsedArrayElements,
            final Object forActor) {
        for (final String element : unparsedArrayElements) {
            final String[] range = parseArrayRange(element, forActor); // Used instead of a "isRange" as the parsing is
            // pretty complex and we want to do look-ups only once. Returns null if the element is not a range.
            if (range != null) {
                // Element is a range. Adding all range elements:
                parseArrayElements(resultArray, range, forActor);
                continue;
            } else if (Strings.startsWith(element, syntax.getMethodInvocationMarker())) {
                // Element is an action. Invoking it:
                final ActorConsumer<?, Object> action = parseAction(element, forActor);
                if (action != null) {
                    final Object result = action.consume(forActor);
                    parseArrayActionResult(resultArray, result);
                    continue;
                }
            } else if (Strings.startsWith(element, syntax.getArgumentOpening())
                    && Strings.endsWith(element, syntax.getArgumentClosing())) {
                // Element is an argument. This normally does not happen during LML parsing, as arguments are
                // immediately replaced, but it might be an array parsed for annotation, for example. We want to convert
                // this to the actual argument's value.
                final String argumentValue = data.getArgument(element.substring(1, element.length() - 1).trim());
                // Treating argument as a possible array and adding all its elements:
                parseArrayElements(resultArray, Strings.split(argumentValue, syntax.getArrayElementSeparator()),
                        forActor);
                continue;
            }
            resultArray.add(element);
        }
    }

    /** @param array will contain the parsed values.
     * @param result result of an invoked method. Will be appended to the array. */
    protected void parseArrayActionResult(final Array<String> array, final Object result) {
        if (result instanceof Object[]) {
            for (final Object object : (Object[]) result) {
                array.add(Nullables.toString(object));
            }
        } else if (result instanceof Iterable<?>) {
            for (final Object object : (Iterable<?>) result) {
                array.add(Nullables.toString(object));
            }
        } else {
            array.add(Nullables.toString(result));
        }
    }

    /** @param rawLmlData unparsed data that might be an array range.
     * @param forActor requested to parse a range.
     * @return array range elements if the data is a range or null if it is not. */
    protected String[] parseArrayRange(final String rawLmlData, final Object forActor) {
        final int openingIndex = rawLmlData.indexOf(syntax.getRangeArrayOpening());
        if (Strings.isCharacterAbsent(openingIndex)) {
            return null;
        }
        final int separatorIndex = rawLmlData.indexOf(syntax.getRangeArraySeparator());
        if (Strings.isCharacterAbsent(separatorIndex) || separatorIndex < openingIndex) {
            return null;
        }
        final int closingIndex = rawLmlData.indexOf(syntax.getRangeArrayClosing());
        if (Strings.isCharacterAbsent(closingIndex) || closingIndex < separatorIndex || closingIndex < openingIndex) {
            return null;
        }
        final String rangeBase = rawLmlData.substring(0, openingIndex);
        final int rangeStart = getRangeValue(rawLmlData.substring(openingIndex + 1, separatorIndex), forActor);
        final int rangeEnd = getRangeValue(rawLmlData.substring(separatorIndex + 1, closingIndex), forActor);
        if (rangeStart < rangeEnd) {
            // Range going up.
            final String[] range = new String[rangeEnd - rangeStart + 1];
            for (int index = rangeStart; index <= rangeEnd; index++) {
                range[index - rangeStart] = rangeBase + index;
            }
            return range;
        }
        // Range going down.
        final String[] range = new String[rangeStart - rangeEnd + 1];
        for (int index = rangeStart; index >= rangeEnd; index--) {
            range[rangeStart - index] = rangeBase + index;
        }
        return range;
    }

    /** @param rawData raw string data of range start or end. Expected to be a number (or a value that can be parsed
     *            into an int).
     * @param forActor requested to parse a range.
     * @return parsed value of the range. */
    protected int getRangeValue(final String rawData, final Object forActor) {
        if (Strings.isInt(rawData)) {
            return Integer.parseInt(rawData);
        }
        return parseInt(rawData, forActor);
    }

    @Override
    public ObjectMap<String, Actor> getActorsMappedByIds() {
        return actorsByIds;
    }

    /** @param actor if its ID was specified in the template, it will be mapped to its ID by the parser. */
    protected void mapActorById(final Actor actor) {
        if (actor != null) {
            final String id = LmlUtilities.getActorId(actor);
            if (id != null) {
                actorsByIds.put(id, actor);
            }
        }
    }

    /** @return true if the next peeked character is a comment opening. */
    protected boolean isNextCharacterCommentOpening() {
        return templateReader.hasNextCharacter() && isCommentOpeningMarker(templateReader.peekCharacter());
    }

    /** @param character possible comment opening.
     * @return true if the character is a standard or schema comment opening. */
    protected boolean isCommentOpeningMarker(final char character) {
        return syntax.getCommentOpening() == character || syntax.getSchemaCommentMarker() == character;
    }

    /** @param character possible comment closing.
     * @return true if the character is a standard or schema comment closing. */
    protected boolean isCommentClosingMarker(final char character) {
        return syntax.getCommentClosing() == character || syntax.getSchemaCommentMarker() == character;
    }

    @Override
    public void throwError(final String message) {
        throwError(message, null);
    }

    @Override
    public void throwError(final String message, final Throwable optionalCause) {
        throw new LmlParsingException(constructExceptionMessage(message), optionalCause);
    }

    @Override
    public void throwErrorIfStrict(final String message) {
        throwErrorIfStrict(message, null);
    }

    @Override
    public void throwErrorIfStrict(final String message, final Throwable optionalCause) {
        if (strict) {
            throw new LmlParsingException(
                    constructExceptionMessage(message)
                            + "\n\tNote that this exception would not have been thrown if the parser was not strict.",
                    optionalCause);
        }
    }

    private String constructExceptionMessage(final String message) {
        final StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Error occurred during parsing near line ");
        errorMessage.append(templateReader.getCurrentLine());
        errorMessage.append(" of the original file: \"");
        errorMessage.append(templateReader.getCurrentTemplateName());
        errorMessage.append("\"");
        final boolean originalTemplate = templateReader.isParsingOriginalTemplate();
        if (originalTemplate) {
            errorMessage.append(".");
        } else {
            errorMessage.append(" during parsing template part: \"");
            errorMessage.append(templateReader.getCurrentSequenceName());
            errorMessage.append("\" on line: ");
            errorMessage.append(templateReader.getCurrentSequenceLine());
            errorMessage.append(
                    " of its content. Template part is most likely an evaluated macro or argument result, extracted from the original template.");
        }
        errorMessage.append("\nREASON: ");
        errorMessage.append(message);
        errorMessage.append(
                "\n\tDue implementation of the parser, the real problematic line might SLIGHTLY vary from the given line number(s), but exception message should be clear enough to find the actual error.\n\tCurrently parsed template part:");
        appendLastLines(errorMessage, templateReader.getCurrentSequence(), templateReader.getCurrentSequenceLine());
        if (!originalTemplate) {
            errorMessage.append(
                    "\n\n\tThis template part was most likely evaluated during parsing this original template part (if a macro or an argument ends on this line, its content most likely causes the error): ");
            appendLastLines(errorMessage, templateReader.getOriginalSequence(), templateReader.getCurrentLine());
        }
        errorMessage.append(
                "\n\n\tIf this is not enough to determine the error, call AbstractLmlParser#setLinesAmountPrintedOnException(int) with a suitable number. If you keep a lot of nested macros and want to see the whole invocation tree, try debugging through parsing and use getTemplateReader().toString() before exception occurs to check all current parsed template layers.");
        return errorMessage.toString();
    }

    /** @return last lines of the currently parsed sequence for debugging purposes. */
    private String appendLastLines(final StringBuilder linesBuilder, final String sequence, final int line) {
        if (Strings.isEmpty(sequence)) {
            return "\nUnknown.";
        }
        final int errorLine = line - 1; // Lines start with 1.
        final String[] lines = Strings.separate(sequence, '\n');
        for (int index = Math.max(0, errorLine - debugLines), length = Math.min(lines.length - 1,
                errorLine + debugLines); index <= length; index++) {
            linesBuilder.append('\n');
            appendDebugIndex(linesBuilder, index, String.valueOf(length).length());
            if (index < errorLine) {
                linesBuilder.append("|VALID| ");
            } else if (index == errorLine) {
                linesBuilder.append("|ERROR| ");
            } else {
                linesBuilder.append("|     | ");
            }
            linesBuilder.append(lines[index]);
        }
        return linesBuilder.toString();
    }

    private static void appendDebugIndex(final StringBuilder debugMessageBuilder, final int index,
            final int longestId) {
        final String indexString = String.valueOf(index + 1); // +1, since line numbers from 1 are more natural.
        debugMessageBuilder.append(indexString);
        for (int spaces = 0, amount = longestId + 1 - indexString.length(); spaces < amount; spaces++) {
            debugMessageBuilder.append(' ');
        }
    }

    /** @param debugLines this is the number of lines before and after the actual error line that will be included in
     *            the exception message. Cannot be negative. */
    public void setLinesAmountPrintedOnException(final int debugLines) {
        if (debugLines < 0) {
            throw new IllegalArgumentException("Debug lines amount cannot be lower than 0.");
        }
        this.debugLines = debugLines;
    }
}
