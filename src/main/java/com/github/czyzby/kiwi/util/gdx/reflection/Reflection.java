package com.github.czyzby.kiwi.util.gdx.reflection;

import java.lang.annotation.Annotation;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.czyzby.kiwi.util.common.Exceptions;
import com.github.czyzby.kiwi.util.common.UtilitiesClass;

/** LibGDX reflection utilities.
 *
 * @author MJ */
public class Reflection extends UtilitiesClass {
    private Reflection() {
    }

    /** Object array with 0 length that contains no values. Might be useful for no-arg methods. */
    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    /** Allows to gracefully create a new instance of class, without having to try-catch exceptions.
     *
     * @param ofClass instance of this class will be constructed using reflection.
     * @return a new instance of passed class.
     * @throws GdxRuntimeException when unable to create a new instance.
     * @param <Type> type of constructed value. */
    public static <Type> Type newInstance(final Class<Type> ofClass) {
        try {
            return ClassReflection.newInstance(ofClass);
        } catch (final Throwable exception) {
            throw new GdxRuntimeException("Unable to create a new instance of class: " + ofClass, exception);
        }
    }

    /** Allows to gracefully create a new instance of class, without having to try-catch exceptions.
     *
     * @param ofClass instance of this class will be constructed using reflection.
     * @return a new instance of passed class or null if unable to construct new instance.
     * @param <Type> type of constructed value. */
    public static <Type> Type newInstanceOrNull(final Class<Type> ofClass) {
        return newInstance(ofClass, null);
    }

    /** Allows to gracefully create a new instance of class, without having to try-catch exceptions.
     *
     * @param ofClass instance of this class will be constructed using reflection.
     * @param defaultValue will be returned if unable to construct new instance.
     * @return a new instance of passed class or default value.
     * @param <Type> type of constructed value. */
    public static <Type> Type newInstance(final Class<Type> ofClass, final Type defaultValue) {
        try {
            return ClassReflection.newInstance(ofClass);
        } catch (final Throwable exception) {
            Exceptions.ignore(exception);
            return defaultValue;
        }
    }

    /** Utility method that allows to extract actual annotation from field, bypassing LibGDX annotation wrapper. Returns
     * null if annotation is not present.
     *
     * @param field might be annotated.
     * @param annotationType class of the annotation.
     * @return an instance of the annotation if the field is annotated or null if not.
     * @param <Type> type of annotation. */
    public static <Type extends Annotation> Type getAnnotation(final Field field, final Class<Type> annotationType) {
        if (isAnnotationPresent(field, annotationType)) {
            return field.getDeclaredAnnotation(annotationType).getAnnotation(annotationType);
        }
        return null;
    }

    /** Utility method kept for backwards compatibility. Annotation checking used to be problematic on GWT.
     *
     * @param field might be annotated. Can be null.
     * @param annotationType class of the annotation that the field is checked against.
     * @return true if field is annotated with the specified annotation. */
    public static boolean isAnnotationPresent(final Field field, final Class<? extends Annotation> annotationType) {
        return field != null && field.isAnnotationPresent(annotationType);
    }

    /** Utility method that allows to extract actual annotation from class, bypassing LibGDX annotation wrapper. Returns
     * null if annotation is not present.
     *
     * @param fromClass class that might be annotated.
     * @param annotationType class of the annotation.
     * @return an instance of the annotation if the class is annotated or null if not.
     * @param <Type> type of annotation. */
    public static <Type extends Annotation> Type getAnnotation(final Class<?> fromClass,
            final Class<Type> annotationType) {
        if (ClassReflection.isAnnotationPresent(fromClass, annotationType)) {
            return ClassReflection.getDeclaredAnnotation(fromClass, annotationType).getAnnotation(annotationType);
        }
        return null;
    }

    /** Utility method that allows to extract actual annotation from method, bypassing LibGDX annotation wrapper.
     * Returns null if annotation is not present.
     *
     * @param method method that might be annotated.
     * @param annotationType class of the annotation.
     * @return an instance of the annotation if the method is annotated or null if not.
     * @param <Type> type of annotation. */
    public static <Type extends Annotation> Type getAnnotation(final Method method, final Class<Type> annotationType) {
        if (isAnnotationPresent(method, annotationType)) {
            return method.getDeclaredAnnotation(annotationType).getAnnotation(annotationType);
        }
        return null;
    }

    /** Utility method kept for backwards compatibility. Annotation checking used to be problematic on GWT.
     *
     * @param method might contain the specified annotation. Can be null.
     * @param annotationType class of the annotation that the method is checked against.
     * @return true if method is annotated with the specified annotation. */
    public static boolean isAnnotationPresent(final Method method, final Class<? extends Annotation> annotationType) {
        return method != null && method.isAnnotationPresent(annotationType);
    }

    /** @param method will be set as accessible and invoked.
     * @param methodOwner instance of class with the method. Will have the method invoked. Can be null (static methods).
     * @param arguments method arguments.
     * @return result of method invocation.
     * @throws ReflectionException when unable to invoke the method. */
    public static Object invokeMethod(final Method method, final Object methodOwner, final Object... arguments)
            throws ReflectionException {
        method.setAccessible(true);
        return method.invoke(methodOwner, arguments);
    }

    /** @param method will be set accessible and invoked.
     * @param methodOwner will have the method invoked. Can be null (static methods).
     * @param resultType result will be casted to this type.
     * @param arguments method arguments.
     * @return result of method invocation.
     * @throws ReflectionException when unable to invoke the method.
     * @param <ResultType> type of returned value. */
    @SuppressWarnings("unchecked")
    public static <ResultType> ResultType invokeMethod(final Method method, final Object methodOwner,
            final Class<ResultType> resultType, final Object... arguments) throws ReflectionException {
        method.setAccessible(true);
        return (ResultType) method.invoke(methodOwner, arguments);
    }

    /** @param field will be set accessible and extracted.
     * @param fieldOwner instance of class that contains the field.
     * @return current field value.
     * @throws ReflectionException if unable to extract. */
    public static Object getFieldValue(final Field field, final Object fieldOwner) throws ReflectionException {
        field.setAccessible(true);
        return field.get(fieldOwner);
    }

    /** @param field will be set accessible and extracted.
     * @param fieldOwner instance of class that contains the field.
     * @param fieldType class of the field. Will be used to cast the field object.
     * @return current field value.
     * @throws ReflectionException if unable to extract.
     * @param <FieldType> type of field value. */
    @SuppressWarnings("unchecked")
    public static <FieldType> FieldType getFieldValue(final Field field, final Object fieldOwner,
            final Class<FieldType> fieldType) throws ReflectionException {
        field.setAccessible(true);
        return (FieldType) field.get(fieldOwner);
    }

    /** @param field will be set in the passed object.
     * @param fieldOwner instance of the class that contains the field.
     * @param fieldValue will be set as the new field value.
     * @throws ReflectionException if unable to set. */
    public static void setFieldValue(final Field field, final Object fieldOwner, final Object fieldValue)
            throws ReflectionException {
        field.setAccessible(true);
        field.set(fieldOwner, fieldValue);
    }

    /** @param classToCheck its superclass tree will be checked.
     * @param baseClass class to look for.
     * @return true if classToCheck or any of its superclasses is baseClass. */
    public static boolean isExtending(Class<?> classToCheck, final Class<?> baseClass) {
        while (classToCheck != null) {
            if (classToCheck.equals(baseClass)) {
                return true;
            }
            classToCheck = classToCheck.getSuperclass();
        }
        return false;
    }
}