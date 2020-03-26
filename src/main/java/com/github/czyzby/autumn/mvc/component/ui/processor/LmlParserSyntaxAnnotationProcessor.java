package com.github.czyzby.autumn.mvc.component.ui.processor;

import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.context.error.ContextInitiationException;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.stereotype.preference.LmlParserSyntax;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;
import com.github.czyzby.lml.parser.LmlSyntax;

/** Used to process {@link LmlParserSyntax} annotation. Sets the current LML syntax in the parser.
 *
 * @author MJ */
public class LmlParserSyntaxAnnotationProcessor extends AbstractAnnotationProcessor<LmlParserSyntax> {
    @Inject InterfaceService interfaceService;

    @Override
    public Class<LmlParserSyntax> getSupportedAnnotationType() {
        return LmlParserSyntax.class;
    }

    @Override
    public boolean isSupportingFields() {
        return true;
    }

    @Override
    public void processField(final Field field, final LmlParserSyntax annotation, final Object component,
            final Context context, final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        try {
            final Object syntax = Reflection.getFieldValue(field, component);
            if (syntax instanceof LmlSyntax) {
                interfaceService.getParser().setSyntax((LmlSyntax) syntax);
            } else {
                throw new ContextInitiationException(
                        "LmlParserSyntax-annotated fields need to contain an instance of LmlSyntax. Found: " + syntax
                                + " in field: " + field + " of component: " + component);
            }
        } catch (final ReflectionException exception) {
            throw new ContextInitiationException(
                    "Unable to extract LML syntax from field: " + field + " of component: " + component, exception);
        }
    }
}
