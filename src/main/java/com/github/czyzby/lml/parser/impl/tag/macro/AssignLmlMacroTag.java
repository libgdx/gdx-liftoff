package com.github.czyzby.lml.parser.impl.tag.macro;

import com.badlogic.gdx.utils.Array;
import com.github.czyzby.kiwi.util.common.Nullables;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractMacroLmlTag;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Allows to assign LML parser arguments from within LML templates. Arguments will be available after macro evaluation
 * with the given name, by default: {accessedLikeThis}. First macro attribute is the name of the argument to assign.
 * Other - optional - arguments will be joined with space and assigned as argument value. Alternatively, argument value
 * can be the data between macro tags. For example: <blockquote>
 *
 * <pre>
 * &lt;:assign arg0 Value/&gt;
 * &lt;:assign arg1 Complex value  with many   parts./&gt;
 * &lt;:assign arg2&gt;Data between macro tags.&lt;/:assign&gt;
 * </pre>
 *
 * </blockquote>Assigned values:
 * <ul>
 * <li>{arg0}: {@code "Value"}
 * <li>{arg1}: {@code "Complex value with many parts."}
 * <li>{arg2}: {@code "Data between macro tags."}
 * </ul>
 * Of course, data between assignment macro tags can contain any other tags (including nested assign macros) and can be
 * used to effectively assign template parts to convenient-to-use arguments.
 *
 * <p>
 * Assignment macro supports optional named attributes:<blockquote>
 *
 * <pre>
 * &lt;:assign key="arg0" value="Value"/&gt;
 * &lt;:assign key="arg1" value="Complex value with many parts."/&gt;
 * &lt;:assign key="arg2"&gt;Data between macro tags.&lt;/:assign&gt;
 * </pre>
 *
 * </blockquote>
 *
 * @author MJ */
public class AssignLmlMacroTag extends AbstractMacroLmlTag {
    /** Optional name of the first attribute: name of the argument to set. */
    public static final String KEY_ATTRIBUTE = "key";
    /** Optional name of the second attribute: value to assign. */
    public static final String VALUE_ATTRIBUTE = "value";
    private CharSequence argument;

    public AssignLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    public void handleDataBetweenTags(final CharSequence rawMacroContent) {
        if (Strings.isNotEmpty(rawMacroContent)) {
            argument = rawMacroContent;
        }
    }

    @Override
    public void closeTag() {
        if (hasArgumentName()) {
            getParser().getData().addArgument(getArgumentName(), processArgumentValue(getArgumentValue()));
        } else {
            getParser().throwErrorIfStrict(
                    "Assignment macro needs at least one attribute: name of the argument to assign. If you use named attributes, use 'key' attribute to set the argument name.");
        }
    }

    /** @param argumentValue should be evaluated according to macro specification.
     * @return evaluated value. */
    protected String processArgumentValue(final CharSequence argumentValue) {
        // Assignment macro does parse the value, it just assigns it.
        return argumentValue.toString();
    }

    /** @return argument value that should be assigned. */
    protected CharSequence getArgumentValue() {
        if (argument != null) {
            if (hasArgumentValue()) {
                getParser().throwErrorIfStrict(
                        "Assignment macro cannot have both argument value to assign (second macro attribute) and content between tags. Only 1 value can be assigned to 1 argument name.");
            }
            return argument;
        }
        if (hasArgumentValue()) {
            return getArgumentValueFromAttributes();
        }
        getParser().throwErrorIfStrict(
                "Assignment macro has to have a value to assign. Pass second attribute name or add content between macro tags.");
        return Nullables.DEFAULT_NULL_STRING;
    }

    /** @return true if has at least one attribute. */
    protected boolean hasArgumentName() {
        return GdxArrays.isNotEmpty(getAttributes());
    }

    /** @return true if has the argument value to assign. */
    protected boolean hasArgumentValue() {
        return hasAttribute(VALUE_ATTRIBUTE) || GdxArrays.sizeOf(getAttributes()) > 1;
    }

    /** @return attribute assigned to argument name. */
    protected String getArgumentName() {
        return hasAttribute(KEY_ATTRIBUTE) ? getAttribute(KEY_ATTRIBUTE) : getAttributes().get(0);
    }

    /** @return attribute assigned to argument value. */
    protected String getArgumentValueFromAttributes() {
        if (hasAttribute(VALUE_ATTRIBUTE)) {
            return getAttribute(VALUE_ATTRIBUTE);
        }
        final Array<String> attributes = getAttributes();
        if (GdxArrays.sizeOf(attributes) == 2) {
            return attributes.get(1);
        }
        final StringBuilder builder = new StringBuilder();
        final char separator = getAttributeSeparator();
        for (int index = 1, length = GdxArrays.sizeOf(attributes); index < length; index++) {
            if (builder.length() > 0) {
                builder.append(separator);
            }
            builder.append(attributes.get(index));
        }
        return builder.toString();
    }

    /** @return character will be used to separate multiple macro attributes. */
    protected char getAttributeSeparator() {
        return ' ';
    }

    @Override
    public String[] getExpectedAttributes() {
        return new String[] { KEY_ATTRIBUTE, VALUE_ATTRIBUTE };
    }
}
