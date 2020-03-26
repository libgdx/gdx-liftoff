package com.github.czyzby.autumn.mvc.component.ui.processor;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.czyzby.autumn.annotation.OnMessage;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.context.error.ContextInitiationException;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.config.AutumnMessage;
import com.github.czyzby.autumn.mvc.stereotype.SkinAsset;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;
import com.github.czyzby.kiwi.util.gdx.collection.lazy.LazyObjectMap;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;
import com.github.czyzby.kiwi.util.tuple.immutable.Pair;

/** Injects assets from application's {@link Skin} (when it is fully loaded) into
 * fields annotated with {@link SkinAsset}.
 *
 * @author MJ */
public class SkinAssetAnnotationProcessor extends AbstractAnnotationProcessor<SkinAsset> {
    // <ID of object, ID of skin>, <field, fieldOwner>
    private final ObjectMap<Pair<String, String>, Array<Pair<Field, Object>>> fieldsToInject = LazyObjectMap
            .newMapOfArrays();

    @Override
    public Class<SkinAsset> getSupportedAnnotationType() {
        return SkinAsset.class;
    }

    @Override
    public boolean isSupportingFields() {
        return true;
    }

    @Override
    public void processField(final Field field, final SkinAsset annotation, final Object component,
            final Context context, final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        fieldsToInject.get(Pair.of(annotation.value(), annotation.skin())).add(Pair.of(field, component));
    }

    /** Invoked when all skins are loaded. Injects skin assets.
     *
     * @param interfaceService used to retrieve skins.
     * @return {@link OnMessage#REMOVE}. */
    @SuppressWarnings("unchecked")
    @OnMessage(AutumnMessage.SKINS_LOADED)
    public boolean injectFields(final InterfaceService interfaceService) {
        for (final Entry<Pair<String, String>, Array<Pair<Field, Object>>> entry : fieldsToInject) {
            final Skin skin = interfaceService.getParser().getData().getSkin(entry.key.getSecond());
            final String assetId = entry.key.getFirst();
            if (skin == null) {
                throw new ContextInitiationException(
                        "Unable to inject asset: " + assetId + ". Unknown skin ID: " + entry.key.getSecond());
            }
            for (final Pair<Field, Object> injection : entry.value) {
                try {
                    Reflection.setFieldValue(injection.getFirst(), injection.getSecond(),
                            skin.get(assetId, injection.getFirst().getType()));
                } catch (final ReflectionException exception) {
                    throw new GdxRuntimeException("Unable to inject skin asset: " + assetId + " from skin: " + skin
                            + " to field: " + injection.getFirst() + " of component: " + injection.getSecond(),
                            exception);
                }
            }
        }
        fieldsToInject.clear();
        return OnMessage.REMOVE;
    }
}
