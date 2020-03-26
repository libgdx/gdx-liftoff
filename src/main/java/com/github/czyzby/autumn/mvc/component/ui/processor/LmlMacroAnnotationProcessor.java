package com.github.czyzby.autumn.mvc.component.ui.processor;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.czyzby.autumn.annotation.Initiate;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.config.AutumnActionPriority;
import com.github.czyzby.autumn.mvc.stereotype.preference.LmlMacro;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;
import com.github.czyzby.kiwi.util.gdx.asset.lazy.Lazy;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;
import com.github.czyzby.lml.parser.LmlParser;

/** Used to scan for paths with LML macro files.
 *
 * @author MJ */
public class LmlMacroAnnotationProcessor extends AbstractAnnotationProcessor<LmlMacro> {
    @Inject(lazy = InterfaceService.class) private Lazy<InterfaceService> interfaceService;

    private final Array<FileHandle> macros = GdxArrays.newArray();

    @Override
    public Class<LmlMacro> getSupportedAnnotationType() {
        return LmlMacro.class;
    }

    @Override
    public boolean isSupportingFields() {
        return true;
    }

    @Override
    public void processField(final Field field, final LmlMacro annotation, final Object component,
            final Context context, final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        try {
            final Object macroData = Reflection.getFieldValue(field, component);
            final FileType fileType = annotation.fileType();
            if (macroData instanceof String) {
                macros.add(Gdx.files.getFileHandle((String) macroData, fileType));
            } else if (macroData instanceof String[]) {
                for (final String macroPath : (String[]) macroData) {
                    macros.add(Gdx.files.getFileHandle(macroPath, fileType));
                }
            } else {
                throw new GdxRuntimeException("Invalid type of LML macro definition in component: " + component
                        + ". String or String[] expected, received: " + macroData + ".");
            }
        } catch (final ReflectionException exception) {
            throw new GdxRuntimeException(
                    "Unable to extract macro paths from field: " + field + " of component: " + component + ".",
                    exception);
        }
    }

    /** Parses all collected macros one by one. */
    @Initiate(priority = AutumnActionPriority.HIGH_PRIORITY)
    private void parseMacros() {
        if (GdxArrays.isEmpty(macros)) {
            return;
        }
        final LmlParser parser = interfaceService.get().getParser();
        for (final FileHandle macro : macros) {
            parser.parseTemplate(macro);
        }
    }
}