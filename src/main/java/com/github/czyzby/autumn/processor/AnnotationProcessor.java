package com.github.czyzby.autumn.processor;

import java.lang.annotation.Annotation;

import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;

/** Common interface for processors of a single annotation.
 *
 * @author MJ
 * @param <SupportedAnnotation> type of annotation supported by this processor. */
public interface AnnotationProcessor<SupportedAnnotation extends Annotation> {
    /** @return true if the annotation can be present on fields and this processor is prepared to handle such fields. */
    boolean isSupportingFields();

    /** @return true if the annotation can be present on methods and this processor is prepare to handle such
     *         methods. */
    boolean isSupportingMethods();

    /** @return true if the annotation can be present on classes and this processor is prepare to handle such
     *         classes. */
    boolean isSupportingTypes();

    /** @return class of the supported annotation. */
    Class<SupportedAnnotation> getSupportedAnnotationType();

    /** @param field is annotated with the supported annotation.
     * @param annotation an instance of the supported annotation present in the field.
     * @param component contains the field.
     * @param context has references to other components.
     * @param initializer currently initiates the context.
     * @param contextDestroyer allows to register context destruction callbacks. */
    void processField(Field field, SupportedAnnotation annotation, Object component, Context context,
                      ContextInitializer initializer, ContextDestroyer contextDestroyer);

    /** @param method is annotated with the supported annotation.
     * @param annotation an instance of the supported annotation present in the method.
     * @param component contains the method.
     * @param context has references to other components.
     * @param initializer currently initiates the context.
     * @param contextDestroyer allows to register context destruction callbacks. */
    void processMethod(Method method, SupportedAnnotation annotation, Object component, Context context,
                       ContextInitializer initializer, ContextDestroyer contextDestroyer);

    /** @param type is annotated with the supported annotation.
     * @param annotation an instance of the supported annotation present in the type.
     * @param component is an instance of the type class.
     * @param context has references to other components.
     * @param initializer currently initiates the context.
     * @param contextDestroyer allows to register context destruction callbacks. */
    void processType(Class<?> type, SupportedAnnotation annotation, Object component, Context context,
                     ContextInitializer initializer, ContextDestroyer contextDestroyer);

    /** Executed before regular component scanning is in progress.
     *
     * @param initializer is about to begin scanning for regular components. */
    void doBeforeScanning(ContextInitializer initializer);

    /** Executed after all components (and their annotations) were processed.
     *
     * @param initializer has finished scanning and initiating components.
     * @param context contains references to components.
     * @param destroyer used to schedule destruction callbacks. */
    void doAfterScanning(ContextInitializer initializer, Context context, ContextDestroyer destroyer);
}
