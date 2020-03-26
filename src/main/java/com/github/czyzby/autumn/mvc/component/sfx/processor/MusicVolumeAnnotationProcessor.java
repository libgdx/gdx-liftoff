package com.github.czyzby.autumn.mvc.component.sfx.processor;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.mvc.component.sfx.MusicService;
import com.github.czyzby.autumn.mvc.stereotype.preference.sfx.MusicVolume;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;

/** Allows to set initial music volume and assign music preferences.
 *
 * @author MJ */
public class MusicVolumeAnnotationProcessor extends AbstractAnnotationProcessor<MusicVolume> {
    @Inject private MusicService musicService;

    @Override
    public Class<MusicVolume> getSupportedAnnotationType() {
        return MusicVolume.class;
    }

    @Override
    public boolean isSupportingFields() {
        return true;
    }

    @Override
    public void processField(final Field field, final MusicVolume annotation, final Object component,
            final Context context, final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        try {
            if (field.getType().equals(float.class) || field.getType().equals(Float.class)) {
                musicService.setMusicVolume((Float) Reflection.getFieldValue(field, component));
            } else {
                musicService.setMusicVolumeFromPreferences(annotation.preferences(),
                        Reflection.getFieldValue(field, component).toString(), annotation.defaultVolume());
            }
        } catch (final ReflectionException exception) {
            throw new GdxRuntimeException("Unable to extract music volume.", exception);
        }
    }
}