package com.github.czyzby.lml.parser.impl.tag.macro;

import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.LmlStyleSheet;
import com.github.czyzby.lml.parser.impl.tag.AbstractMacroLmlTag;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Allows to add styles to LML style sheet at runtime. Assigns default attribute values to the chosen tags. Expects at
 * least 2 arguments: tag and attribute names. Default attribute value can be passed with either third argument or data
 * between macro tags. Both tags and attributes arguments accept arrays, so multiple tags or attributes can be modified
 * at once. For example: <blockquote>
 *
 * <pre>
 * &lt;:style button tablePad 3 /&gt;
 * &lt;:style button tablePad&gt;3&lt;/:style button tablePad&gt;
 * </pre>
 *
 * </blockquote>Both of these macro invocations would assign "3" as default value of "tablePad" attribute in all
 * "button" tags.
 * <p>
 * Note that this macro supports optional named attributes: <blockquote>
 *
 * <pre>
 * &lt;:style tag="button" attribute="tablePad" value="3" /&gt;
 * &lt;:style tag="button" attribute="tablePad"&gt;3&lt;/:style button tablePad&gt;
 * </pre>
 *
 * </blockquote>To modify multiple tags or attributes at once, separate their names with ';' (by default) to create a
 * LML array: <blockquote>
 *
 * <pre>
 * &lt;:style tag="button;textButton" attribute="tablePadLeft;tablePadRight" value="3" /&gt;
 * </pre>
 *
 * </blockquote>
 *
 * @author MJ
 * @see LmlParser#getStyleSheet() */
public class StyleLmlMacroTag extends AbstractMacroLmlTag {
    /** Optional name of the first attribute: name of the tag to modify. */
    public static final String TAG_ATTRIBUTE = "tag";
    /** Optional name of the second attribute: name of the attribute to set. */
    public static final String ATTRIBUTE_ATTRIBUTE = "attribute";
    /** Optional name of the third attribute: default value of the attribute. */
    public static final String VALUE_ATTRIBUTE = "value";

    private String content;

    public StyleLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    public void handleDataBetweenTags(final CharSequence rawData) {
        if (Strings.isNotWhitespace(rawData)) {
            content = rawData.toString();
        }
    }

    @Override
    public void closeTag() {
        if (GdxArrays.sizeOf(getAttributes()) < 2) {
            getParser().throwErrorIfStrict("Style macro needs at least 2 attributes: tag and attribute names.");
            return;
        }
        final String[] tagNames = getTagNames();
        final String[] attributeNames = getAttributeNames();
        final String defaultValue = getDefaultValueArgument();
        if (tagNames == null || tagNames.length == 0 || attributeNames == null || attributeNames.length == 0
                || defaultValue == null) {
            getParser().throwErrorIfStrict(
                    "Style macro needs at least 2 arguments: tag and attribute names. Default attribute value can be set with third argument or data between macro tags. All 3 values are required.");
            return;
        }
        final LmlStyleSheet styleSheet = getParser().getStyleSheet();
        for (final String tag : tagNames) {
            for (final String attribute : attributeNames) {
                styleSheet.addStyle(tag, attribute, defaultValue);
            }
        }
    }

    /** @return names of the modified tags extracted from attributes parsed as array. Assumes there is at least 1
     *         attribute. */
    protected String[] getTagNames() {
        final String tagNames = getTagNameArgument();
        return tagNames == null ? null : getParser().parseArray(tagNames, getActor());
    }

    /** @return name of the set attribute extracted from attributes parsed as array. Assumes there are at least 2 macro
     *         attributes. */
    private String[] getAttributeNames() {
        final String attributeNames = getAttributeNameArgument();
        return attributeNames == null ? null : getParser().parseArray(attributeNames, getActor());
    }

    /** @return raw data of names of the modified tags extracted from attributes. Assumes there is at least 1
     *         attribute. */
    protected String getTagNameArgument() {
        final ObjectMap<String, String> attributes = getNamedAttributes();
        if (GdxMaps.isNotEmpty(attributes)) {
            return attributes.get(TAG_ATTRIBUTE);
        }
        return getAttributes().first();
    }

    /** @return raw data of names of the set attributes extracted from arguments. Assumes there are at least 2 macro
     *         attributes. */
    protected String getAttributeNameArgument() {
        final ObjectMap<String, String> attributes = getNamedAttributes();
        if (GdxMaps.isNotEmpty(attributes)) {
            return attributes.get(ATTRIBUTE_ATTRIBUTE);
        }
        return getAttributes().get(1);
    }

    /** @return default value of the set attribute extracted from attributes or tag content. */
    protected String getDefaultValueArgument() {
        if (content != null) {
            return content;
        }
        final ObjectMap<String, String> attributes = getNamedAttributes();
        if (GdxMaps.isNotEmpty(attributes)) {
            return attributes.get(VALUE_ATTRIBUTE);
        }
        if (GdxArrays.sizeOf(getAttributes()) >= 3) {
            return getAttributes().get(2);
        }
        return null;
    }

    @Override
    public String[] getExpectedAttributes() {
        return new String[] { TAG_ATTRIBUTE, ATTRIBUTE_ATTRIBUTE, VALUE_ATTRIBUTE };
    }
}
