package com.github.czyzby.autumn.mvc.component.ui.controller.impl;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;

/** Base class for controller wrappers of annotated objects.
 *
 * @author MJ */
public abstract class AbstractAnnotatedController {
    protected final Object wrappedObject;
    private Field stageField;

    public AbstractAnnotatedController(final Object wrappedObject) {
        this.wrappedObject = wrappedObject;
    }

    /** @param stage will be injected into {@link com.github.czyzby.autumn.mvc.stereotype.ViewStage}-annotated field, if
     *            present. */
    protected void injectStage(final Stage stage) {
        if (stageField != null) {
            try {
                Reflection.setFieldValue(stageField, wrappedObject, stage);
            } catch (final ReflectionException exception) {
                throw new GdxRuntimeException("Unable to inject stage into controller: " + wrappedObject + ".",
                        exception);
            }
        }
    }

    /** Will inject null into {@link com.github.czyzby.autumn.mvc.stereotype.ViewStage}-annotated field. */
    protected void clearStage() {
        injectStage(null);
    }

    /** Allows to specify a field holding reference to current managed {@link Stage}.
     *
     * @param field will have current stage injected upon created and null upon stage destruction. */
    public void registerStageField(final Field field) {
        if (stageField != null) {
            throw new GdxRuntimeException("Multiple stages fields annotated for view: " + wrappedObject + ".");
        }
        stageField = field;
    }
}
