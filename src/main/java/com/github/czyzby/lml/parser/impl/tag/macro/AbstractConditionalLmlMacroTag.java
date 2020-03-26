package com.github.czyzby.lml.parser.impl.tag.macro;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.kiwi.util.common.Nullables;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.tuple.immutable.Pair;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.LmlSyntax;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.impl.tag.AbstractMacroLmlTag;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Abstract base for conditional macros. Conditional macros evaluate a condition stored in their attributes and append
 * one part of the data stored between their tags. For example: <blockquote>
 *
 * <pre>
 * &lt;:notNull {someArgument}&gt;
 *      Added on true.
 * &lt;:notNull:else/&gt;
 *      Added on false.
 * &lt;/:notNull&gt;
 * </pre>
 *
 * </blockquote>This particular macro - as you might guess - checks if the {someArgument} value is not null. If it is
 * present, "Added on true." will be processed by the parser, ignoring the rest of the macro's content. If argument
 * happens to be null or boolean false, "Added on false." will be processed.
 *
 * <p>
 * Else tag is optional: if else tag is not given, the whole macro content is appended only on "true" condition. Else
 * tag follows this syntax: tagOpening (&lt;) + macroMarker (:) + macroTagName (for example, "notNull") + ":else"
 * (ignoring case) + closedTagMarker (/) + tagClosing (&gt;). Typos or whitespaces in else tags might result in invalid
 * parsing.
 *
 * <p>
 * All conditional tags can be used with named attributes:<blockquote>
 *
 * <pre>
 * &lt;:if test="5!=3"&gt;
 *      Added on true.
 * &lt;/:if&gt;
 * </pre>
 *
 * </blockquote>
 *
 * @author MJ */
public abstract class AbstractConditionalLmlMacroTag extends AbstractMacroLmlTag {
    /** This value is appended to original macro tag name to create if-else functionality. {@literal <name:else/>} tag
     * should separate value appended on true "true" from "false". */
    public static final String ELSE_SUFFIX = ":else";
    /** When using named parameters, this value is used to construct the condition instead of other attributes. */
    public static final String TEST_ATTRIBUTE = "test";

    public AbstractConditionalLmlMacroTag(final LmlParser parser, final LmlTag parentTag,
            final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    public void handleDataBetweenTags(final CharSequence rawMacroContent) {
        final Pair<CharSequence, CharSequence> content = splitInTwo(rawMacroContent, getSeparator(rawMacroContent));
        if (checkCondition()) {
            appendTextToParse(content.getFirst());
        } else {
            appendTextToParse(content.getSecond());
        }
    }

    /** @param content will be split by the separator.
     * @return separator, used to split raw texts into two parts - value added on "true" and on "false". */
    protected String getSeparator(CharSequence content) {
        // Since splitting method does not use a regex, we accept 2 separators as a workaround.
        final LmlSyntax syntax = getParser().getSyntax();
        String separator = buildSeparator(syntax, true);
        return Strings.containsIgnoreCase(content, separator) ? separator : buildSeparator(syntax, false);
    }

    private String buildSeparator(LmlSyntax syntax, boolean includeWhitespace) {
        final StringBuilder builder = new StringBuilder();
        builder.append(syntax.getTagOpening());
        builder.append(syntax.getMacroMarker());
        builder.append(getTagName());
        builder.append(ELSE_SUFFIX);
        if (includeWhitespace) {
            builder.append(' ');
        }
        builder.append(syntax.getClosedTagMarker());
        builder.append(syntax.getTagClosing());
        return builder.toString();
    }

    /** @return evaluated condition result that will decide if the "true" or "false" content is appended. */
    protected abstract boolean checkCondition();

    /** @param attribute will be checked.
     * @return true if the attribute is a method invocation. */
    protected boolean isAction(final String attribute) {
        return Strings.startsWith(attribute, getParser().getSyntax().getMethodInvocationMarker());
    }

    /** @param attribute is an action.
     * @return result of the action invocation. */
    protected Object invokeAction(final String attribute) {
        final ActorConsumer<?, Actor> action = getParser().parseAction(attribute, getActor());
        if (action != null) {
            return action.consume(getActor());
        }
        getParser().throwError(
                "Unable to evaluate conditional macro. Unknown action ID: " + attribute + " for actor: " + getActor());
        return null;
    }

    /** @param value can be null.
     * @return true if value is mapped to null or boolean false. */
    protected boolean isNullOrFalse(final Object value) {
        return isNullOrFalse(Nullables.toNullableString(value));
    }

    /** @param value LML value.
     * @return true if value is mapped to null or boolean false. */
    protected boolean isNullOrFalse(final String value) {
        return value == null || Strings.isWhitespace(value) || Nullables.DEFAULT_NULL_STRING.equalsIgnoreCase(value)
                || Boolean.FALSE.toString().equalsIgnoreCase(value);
    }

    @Override
    public String[] getExpectedAttributes() {
        return new String[] { TEST_ATTRIBUTE };
    }
}
