package com.github.czyzby.lml.parser.action;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.czyzby.kiwi.util.common.Exceptions;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.util.Lml;
import com.github.czyzby.lml.util.collection.IgnoreCaseStringMap;

/** Wraps around an {@link ActionContainer}, providing access to its methods and fields. Mostly for internal parsing
 * use.
 *
 * @author MJ */
public class ActionContainerWrapper {
    private final ActionContainer actionContainer;
    private final ObjectMap<String, Method> annotatedMethods = new IgnoreCaseStringMap<Method>();
    private final ObjectMap<String, Field> annotatedFields = new IgnoreCaseStringMap<Field>();

    public ActionContainerWrapper(final ActionContainer actionContainer) {
        this.actionContainer = actionContainer;
        mapAnnotatedMethods();
    }

    private void mapAnnotatedMethods() {
        try {
            Class<?> containerClass = actionContainer.getClass();
            while (containerClass != null) {
                mapClassMethods(containerClass);
                mapClassFields(containerClass);
                containerClass = containerClass.getSuperclass();
            }
        } catch (final Exception exception) {
            throw new GdxRuntimeException("Unable to map ActionContainer methods.", exception);
        }
    }

    private void mapClassMethods(final Class<?> containerClass) {
        for (final Method method : ClassReflection.getDeclaredMethods(containerClass)) {
            final LmlAction actionData = Reflection.getAnnotation(method, LmlAction.class);
            if (actionData != null) {
                final String[] ids = actionData.value();
                if (ids.length > 0) {
                    for (final String actionId : ids) {
                        annotatedMethods.put(actionId, method);
                    }
                } else {
                    annotatedMethods.put(method.getName(), method);
                }
            }
        }
    }

    private void mapClassFields(final Class<?> containerClass) {
        if (!Lml.EXTRACT_FIELDS_AS_METHODS) {
            return;
        }
        for (final Field field : ClassReflection.getDeclaredFields(containerClass)) {
            final LmlAction actionData = Reflection.getAnnotation(field, LmlAction.class);
            if (actionData != null) {
                final String[] ids = actionData.value();
                if (ids.length > 0) {
                    for (final String actionId : ids) {
                        annotatedFields.put(actionId, field);
                    }
                } else {
                    annotatedFields.put(field.getName(), field);
                }
            }
        }
    }

    /** @return wrapped action container. */
    public ActionContainer getActionContainer() {
        return actionContainer;
    }

    /** @param methodId ID of the referenced method.
     * @return method referenced directly with the selected ID or null if none. */
    public Method getNamedMethod(final String methodId) {
        return annotatedMethods.get(methodId);
    }

    /** @param fieldId ID of the referenced field.
     * @return field referenced directly with the selected ID or null if none. */
    public Field getNamedField(final String fieldId) {
        return annotatedFields.get(fieldId);
    }

    /** @param methodName name of the possibly contained method.
     * @param parameterClass class of the expected parameter. Optional.
     * @return method with passed name and one or zero parameters. Might be null. */
    public Method getMethod(final String methodName, final Class<?> parameterClass) {
        Class<?> containerClass = actionContainer.getClass();
        while (containerClass != null) {
            try {
                // If this does not throw an exception, the method is found. Null check is not needed.
                return getDeclaredMethod(containerClass, methodName, parameterClass);
            } catch (final Exception exception) {
                Exceptions.ignore(exception); // Expected. Method unavailable.
            }
            containerClass = containerClass.getSuperclass();
        }
        return null;
    }

    private static Method getDeclaredMethod(final Class<?> containerClass, final String methodName,
            final Class<?> parameterClass) throws ReflectionException {
        if (parameterClass == null) {
            return ClassReflection.getDeclaredMethod(containerClass, methodName);
        }
        return ClassReflection.getDeclaredMethod(containerClass, methodName, parameterClass);
    }

    /** @param fieldName name of the field.
     * @return field with the selected name or null. */
    public Field getField(final String fieldName) {
        Class<?> containerClass = actionContainer.getClass();
        while (containerClass != null) {
            try {
                // If this does not throw an exception, the field is found. Null check is not needed.
                return ClassReflection.getDeclaredField(actionContainer.getClass(), fieldName);
            } catch (final Exception exception) {
                Exceptions.ignore(exception); // Expected. Field unavailable.
            }
            containerClass = containerClass.getSuperclass();
        }
        return null;
    }
}