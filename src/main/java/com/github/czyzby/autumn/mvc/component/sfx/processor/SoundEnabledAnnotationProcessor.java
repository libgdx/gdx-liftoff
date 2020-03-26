package com.github.czyzby.autumn.mvc.component.sfx.processor;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.mvc.component.sfx.MusicService;
import com.github.czyzby.autumn.mvc.stereotype.preference.sfx.SoundEnabled;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;

/** Allows to set initial music state and assign music preferences.
 *
 * @author MJ */
public class SoundEnabledAnnotationProcessor extends AbstractAnnotationProcessor<SoundEnabled> {
    @Inject private MusicService musicService;

    @Override
    public Class<SoundEnabled> getSupportedAnnotationType() {
        return SoundEnabled.class;
    }

    @Override
    public boolean isSupportingFields() {
        return true;
    }

    @Override
    public void processField(final Field field, final SoundEnabled annotation, final Object component,
            final Context context, final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        try {
            if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
                musicService.setSoundEnabled((Boolean) Reflection.getFieldValue(field, component));
            } else {
                musicService.setSoundEnabledFromPreferences(annotation.preferences(),
                        Reflection.getFieldValue(field, component).toString(), annotation.defaultSetting());
            }
        } catch (final ReflectionException exception) {
            throw new GdxRuntimeException("Unable to extract sound state.", exception);
        }
    }
}