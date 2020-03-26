package com.github.czyzby.lml.parser.impl.tag.builder;

import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;

/** Extends standard widget builder with an initial text property.
 *
 * @author MJ */
public class TextLmlActorBuilder extends LmlActorBuilder {
    private String text = Strings.EMPTY_STRING;

    /** @return initial text of the widget. */
    public String getText() {
        return text;
    }

    /** @param text will become initial text of the widget. */
    public void setText(final String text) {
        this.text = text;
    }
}
