package com.github.czyzby.autumn.mvc.component.i18n.processor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.stereotype.preference.I18nBundle;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;

/** Used to scan for annotated i18n bundles data.
 *
 * @author MJ */
public class I18nBundleAnnotationProcessor extends AbstractAnnotationProcessor<I18nBundle> {
    @Inject InterfaceService interfaceService;

    @Override
    public Class<I18nBundle> getSupportedAnnotationType() {
        return I18nBundle.class;
    }

    @Override
    public boolean isSupportingFields() {
        return true;
    }

    @Override
    public void processField(final Field field, final I18nBundle annotation, final Object component,
            final Context context, final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        try {
            final Object bundleField = Reflection.getFieldValue(field, component);
            interfaceService.addBundleFile(annotation.value(), extractBundleFile(annotation, bundleField));
        } catch (final ReflectionException exception) {
            throw new GdxRuntimeException("Unable to extract i18n bundle path.", exception);
        }
    }

    private static FileHandle extractBundleFile(final I18nBundle bundleData, final Object bundleField) {
        if (bundleField instanceof FileHandle) {
            return (FileHandle) bundleField;
        }
        return Gdx.files.getFileHandle(bundleField.toString(), bundleData.fileType());
    }
}