package com.github.czyzby.lml.vis.util;

import com.github.czyzby.lml.parser.LmlData;
import com.github.czyzby.lml.parser.LmlParser;

/** Utility class for simplified Vis UI LML parser creation.
 *
 * @author MJ */
public class VisLml {
    private VisLml() {
    }

    /** @return a new {@link VisLmlParserBuilder}, allowing to easily create a new instance of {@link LmlParser}. */
    public static VisLmlParserBuilder parser() {
        return new VisLmlParserBuilder();
    }

    /** @param data contains data necessary to properly parse LML templates.
     * @return a new {@link VisLmlParserBuilder}, allowing to easily create a new instance of {@link LmlParser}.
     * @see com.github.czyzby.lml.parser.impl.DefaultLmlData */
    public static VisLmlParserBuilder parser(final LmlData data) {
        return new VisLmlParserBuilder(data);
    }
}
