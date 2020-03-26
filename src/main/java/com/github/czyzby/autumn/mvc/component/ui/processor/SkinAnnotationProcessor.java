package com.github.czyzby.autumn.mvc.component.ui.processor;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.mvc.component.ui.dto.SkinData;
import com.github.czyzby.autumn.mvc.stereotype.preference.Skin;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;

/** Used to process annotated skin data.
 *
 * @author MJ */
public class SkinAnnotationProcessor extends AbstractAnnotationProcessor<Skin> {
    private final ObjectMap<String, SkinData> skinsData = GdxMaps.newObjectMap();

    @Override
    public Class<Skin> getSupportedAnnotationType() {
        return Skin.class;
    }

    @Override
    public boolean isSupportingFields() {
        return true;
    }

    @Override
    public void processField(final Field field, final Skin annotation, final Object component, final Context context,
            final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        validateFontsData(annotation);
        try {
            skinsData.put(annotation.value(), new SkinData(Reflection.getFieldValue(field, component).toString(),
                    annotation.fonts(), annotation.fontNames()));
        } catch (final ReflectionException exception) {
            throw new GdxRuntimeException("Unable to read skin data.", exception);
        }
    }

    private static void validateFontsData(final Skin annotationData) {
        if (annotationData.fonts().length != annotationData.fontNames().length) {
            throw new GdxRuntimeException("Fonts amount specified with @Skin should match font names amount.");
        }
    }

    /** @return all currently registered skins. */
    public ObjectMap<String, SkinData> getSkinsData() {
        return skinsData;
    }
}
