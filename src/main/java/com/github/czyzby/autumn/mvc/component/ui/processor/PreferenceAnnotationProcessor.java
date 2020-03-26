package com.github.czyzby.autumn.mvc.component.ui.processor;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.stereotype.preference.Preference;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;

/** Used to scan for annotated preferences' data.
 *
 * @author MJ */
public class PreferenceAnnotationProcessor extends AbstractAnnotationProcessor<Preference> {
    @Inject private InterfaceService interfaceService;

    @Override
    public Class<Preference> getSupportedAnnotationType() {
        return Preference.class;
    }

    @Override
    public boolean isSupportingFields() {
        return true;
    }

    @Override
    public void processField(final Field field, final Preference annotation, final Object component,
            final Context context, final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        try {
            final String preferencesKey = annotation.value();
            final String preferencesPath = Reflection.getFieldValue(field, component).toString();
            interfaceService.addPreferencesToParser(preferencesKey, preferencesPath);
        } catch (final ReflectionException exception) {
            throw new GdxRuntimeException(
                    "Unable to read preference path from field: " + field + " of component: " + component + ".",
                    exception);
        }
    }
}
