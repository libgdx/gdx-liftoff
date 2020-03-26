package com.github.czyzby.lml.parser.impl.tag.macro;

import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractMacroLmlTag;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Throws parsing exception. Has one optional attribute: a boolean. If the boolean matches "true", exception is always
 * thrown; otherwise only strict parser throws exception. If no attribute is given, exception is always thrown.
 *
 * <blockquote>
 *
 * <pre>
 * &lt;:notNull $myMethod&gt;
 *      &lt;label text=$myMethod&gt;
 * &lt;:notNull:else/&gt;
 *      &lt;:exception&gt;MyMethod should never return null!&lt;/:exception&gt;
 * &lt;/:notNull&gt;
 * </pre>
 *
 * </blockquote>If method mapped to "myMethod" key returns null or false, this will throw an exception with a custom
 * reason message: "MyMethod should never return null!".
 *
 * <p>
 * This macro can be also used with named parameters:<blockquote>
 *
 * <pre>
 * &lt;:exception message="MyMethod should never return null!" strict="true"/&gt;
 * </pre>
 *
 * </blockquote>
 *
 *
 * @author MJ */
public class ExceptionLmlMacroTag extends AbstractMacroLmlTag {
    /** Optional name of the macro attribute - message to print. */
    public static final String MESSAGE_ATTRIBUTE = "message";
    /** Optional name of attribute - exception will be thrown only if strict. */
    public static final String STRICT_ATTRIBUTE = "strict";

    private String content = "Exception thrown by invoking exception macro.";

    public ExceptionLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    public void handleDataBetweenTags(final CharSequence rawData) {
        content = rawData.toString();
    }

    @Override
    public void closeTag() {
        boolean always;
        if (GdxArrays.isEmpty(getAttributes())) {
            always = true;
        } else {
            if (hasAttribute(STRICT_ATTRIBUTE)) {
                always = getParser().parseBoolean(getAttribute(STRICT_ATTRIBUTE), getActor());
            } else {
                always = getParser().parseBoolean(getAttributes().first(), getActor());
            }
        }
        content = hasAttribute(MESSAGE_ATTRIBUTE) ? getAttribute(MESSAGE_ATTRIBUTE) : content;
        if (always) {
            getParser().throwError(content);
        } else {
            getParser().throwErrorIfStrict(content);
        }
    }

    @Override
    public String[] getExpectedAttributes() {
        return new String[] { MESSAGE_ATTRIBUTE, STRICT_ATTRIBUTE };
    }
}
