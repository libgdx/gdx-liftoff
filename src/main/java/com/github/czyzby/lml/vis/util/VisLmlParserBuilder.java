package com.github.czyzby.lml.vis.util;

import com.github.czyzby.lml.parser.LmlData;
import com.github.czyzby.lml.parser.impl.AbstractLmlParser;
import com.github.czyzby.lml.parser.impl.DefaultLmlParser;
import com.github.czyzby.lml.util.LmlParserBuilder;
import com.github.czyzby.lml.vis.parser.impl.VisLmlSyntax;
import com.kotcrab.vis.ui.VisUI;

/** Allows to build a {@link com.github.czyzby.lml.parser.LmlParser} with default Vis UI LML syntax.
 *
 * @author MJ
 * @see LmlParserBuilder */
public class VisLmlParserBuilder extends LmlParserBuilder {
    /** Constructs a new builder that wraps around an instance of {@link DefaultLmlParser} with Vis UI syntax. */
    public VisLmlParserBuilder() {
        super();
    }

    /** Constructs a new builder that wraps around an instance of {@link DefaultLmlParser} with Vis UI syntax.
     *
     * @param lmlData stores data needed to properly parse LML templates. */
    public VisLmlParserBuilder(final LmlData lmlData) {
        super(lmlData);
    }

    @Override
    protected AbstractLmlParser getInstanceOfParser(final LmlData lmlData) {
        if (!VisUI.isLoaded()) {
            VisUI.load();
        }
        lmlData.setDefaultSkin(VisUI.getSkin());
        return new DefaultLmlParser(lmlData, new VisLmlSyntax());
    }
}
