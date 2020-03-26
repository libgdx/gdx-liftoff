package com.github.czyzby.autumn.mvc.component.i18n.processor;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.mvc.component.i18n.LocaleService;
import com.github.czyzby.autumn.mvc.component.i18n.dto.LocaleChangingAction;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.stereotype.preference.AvailableLocales;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;
import com.github.czyzby.lml.parser.LmlParser;

/** Used to scan for preferences of available game translations.
 *
 * @author MJ */
public class AvailableLocalesAnnotationProcessor extends AbstractAnnotationProcessor<AvailableLocales> {
    @Inject private InterfaceService interfaceService;
    @Inject private LocaleService localeService;

    @Override
    public Class<AvailableLocales> getSupportedAnnotationType() {
        return AvailableLocales.class;
    }

    @Override
    public boolean isSupportingFields() {
        return true;
    }

    @Override
    public void processField(final Field field, final AvailableLocales annotation, final Object component,
            final Context context, final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        try {
            final Object locales = Reflection.getFieldValue(field, component);
            if (locales instanceof String[]) {
                final String[] availableLocales = (String[]) locales;
                final LmlParser parser = interfaceService.getParser();

                parser.getData().addArgument(annotation.viewArgumentName(), availableLocales);
                for (final String locale : availableLocales) {
                    parser.getData().addActorConsumer(annotation.localeChangeMethodPrefix() + locale,
                            new LocaleChangingAction(localeService, LocaleService.toLocale(locale)));
                }
                return;
            }
            throw new GdxRuntimeException("Invalid field annotated with @AvailableLocales in component " + component
                    + ". Expected String[], received: " + locales + ".");
        } catch (final ReflectionException exception) {
            throw new GdxRuntimeException(
                    "Unable to read available locales from field: " + field + " of component: " + component + ".",
                    exception);
        }
    }
}