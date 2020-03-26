package com.github.czyzby.lml.parser.impl.tag.macro;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.kiwi.util.common.Nullables;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.tuple.immutable.Pair;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.LmlSyntax;
import com.github.czyzby.lml.parser.impl.tag.AbstractMacroLmlTag;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;
import com.github.czyzby.lml.util.LmlUtilities;
import com.github.czyzby.lml.util.collection.IgnoreCaseStringMap;

/** Meta macro tag allows to create custom macros from within LML templates. It basically modifies LML syntax to include
 * new macro tags, parsed from the data it receives. First attribute is an LML array of macro aliases. The second
 * (optional) attribute is the name of the data between tags argument in evaluated macro. The rest are custom attributes
 * expected by the macro. For example:
 *
 * <blockquote>
 *
 * <pre>
 * &lt;:macro newActor&gt;&lt;actor/&gt;&lt;/:macro&gt;
 * &lt;:newActor/&gt;
 * </pre>
 *
 *
 * </blockquote>This is a very simple macro that adds a single tag on evaluation. It is not prepared to handle content
 * between tags and does not have any extra arguments. After invoking (second line in the example), it will spawn a
 * single tag: &lt;actor/&gt;. <blockquote>
 *
 * <pre>
 * &lt;:macro newActor "" id&gt;&lt;actor id={id}/&gt;&lt;/:macro&gt;
 * &lt;:newActor id=actorId/&gt;
 * &lt;:newActor actorId/&gt;
 * &lt;:newActor/&gt;
 * </pre>
 *
 * </blockquote>In this updated example, we added two arguments: empty quotation (as we still don't want to parse data
 * between macro tags) and id attribute. Value assigned to "id" will replace all "{id}" arguments in the macro. First
 * and second evaluation will produce &lt;actor id=actorId/&gt; attribute: note that both named ("id=actorId") and
 * unnamed ("actorId") attribute passing is valid, as long as named and unnamed attributes are not mixed. If you use
 * named attributes, you can pass attributes in any other; if you use unnamed attributes, their passing order must match
 * declaration order. The third evaluation example will produce an actor with a null ID, as we didn't pass the attribute
 * and it was replaced by null. <blockquote>
 *
 * <pre>
 * &lt;:macro newActor "" id=defaultId&gt;&lt;actor id={id}/&gt;&lt;/:macro&gt;
 * &lt;:newActor id=actorId/&gt;
 * &lt;:newActor/&gt;
 * </pre>
 *
 * </blockquote> In this example, we added a default value to the "id" attribute. Now, if macro is evaluated without
 * "id" attribute set, "{id}" will be replaced with default value. First invocation will produce &lt;actor
 * id=actorId/&gt;, second - &lt;actor id=defaultId/&gt;. Note that quotations on default value - as well as on any
 * attribute - is optional.<blockquote>
 *
 * <pre>
 * &lt;:macro newActor content&gt;&lt;actor id={content}/&gt;&lt;/:macro&gt;
 * &lt;:newActor&gt;actorId&lt;/:newActor&gt;
 * </pre>
 *
 * </blockquote> In this example, we replaced second attribute with "content", allowing us to use data between macro
 * tags to be used inside our defined macro. Macro invocation will produce &lt;actor id=actorId/&gt; tag, replacing
 * "{content}" with data between tags. <blockquote>
 *
 * <pre>
 * &lt;:macro dialog content title includeCloseButton=true&gt;
 *      &lt;dialog title={title} defaultPad=4&gt;
 *          {content}
 *          &lt;:if {includeCloseButton}&gt;
 *              &lt;textButton expandX=true fillX=true onResult=close&gt;@closeButton&lt;/textButton&gt;
 *          &lt;/:if&gt;
 *      &lt;/dialog&gt;
 * &lt;/:macro&gt;
 *
 *      &lt;!-- This: --&gt;
 *
 * &lt;:dialog title=@error&gt;
 *      &lt;label style=big&gt;@someWarning&lt;/label&gt;
 * &lt;/:dialog&gt;
 *
 *      &lt;!-- ...evaluates to: --&gt;
 *
 * &lt;dialog title=@error defaultPad=4&gt;
 *      &lt;label style=big&gt;@someWarning&lt;/label&gt;
 *      &lt;textButton expandX=true fillX=true onResult=close&gt;@closeButton&lt;/textButton&gt;
 * &lt;/dialog&gt;
 * </pre>
 *
 * </blockquote> This is an example of more complex macro that might be used to quickly construct simple message
 * dialogs. Note that text proceeded with {@literal @} sign is extracted from i18n bundle. If you construct some kind of
 * widget multiple times with similar settings using plain LML tags, you should consider creating a macro instead for
 * simplified tags content.
 * <p>
 * Note that named attributes are supported by this macro, but it ALWAYS HAS TO start with "alias" and "replace"
 * attributes if you want to add custom macro attributes. For example:<blockquote>
 *
 * <pre>
 * &lt;:macro alias="mySimpleMacro"&gt;
 *   &lt;label text="Hello."/&gt;
 * &lt;/:macro&gt;
 *
 * &lt;:macro replace="content" alias="myContentMacro"&gt;
 *   &lt;label&gt;{content}&lt;/label&gt;
 * &lt;/:macro&gt;
 *
 * &lt;:macro alias="myMacro" replace="content" id="default" &gt;
 *   &lt;table id="{id}"&gt;
 *      {content}
 *   &lt;/table&gt;
 * &lt;/:macro&gt;
 *
 * &lt;!-- Macro invocations/; --&gt;
 * &lt;:mySimpleMacro/&gt;
 * &lt;:myContentMacro&gt;Hello.&lt;/:myContentMacro&gt;
 * &lt;:myMacro id="custom"&gt;&lt;label text="Hello."/&gt;&lt;/:myMacro&gt;
 * </pre>
 *
 * </blockquote>This meta-macro needs a way of detecting custom, user-added attributes to the new macro it creates -
 * current implementation assumes that any third (or next) attribute is custom. First two attributes are reserved for
 * "alias" and "replace". Note that DTD validation will not recognize your custom attributes in meta macro, so you might
 * need to give up DTD in your macro files (that's why a global macro file is a good idea) or modify DTD files manually.
 *
 * @author MJ */
public class MetaLmlMacroTag extends AbstractMacroLmlTag {
    /** Alias of the first macro attribute: new macro aliases array. */
    public static final String ALIAS_ATTRIBUTE = "alias";
    /** Alias of the second macro attribute: name of the argument to replace in macro with the content between tags. */
    public static final String REPLACE_ATTRIBUTE = "replace";

    public MetaLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    public void handleDataBetweenTags(final CharSequence rawMacroContent) {
        if (GdxArrays.isEmpty(getAttributes())) {
            getParser().throwErrorIfStrict("Custom macro tag needs at least one attribute: tag names array.");
            return;
        }
        final Pair<Array<String>, Array<String>> attributeNamesAndDefaultValues = getAttributeNamesAndDefaultValues();
        getParser().getSyntax().addMacroTagProvider(
                new CustomLmlMacroTagProvider(getContentAttributeName(), attributeNamesAndDefaultValues.getFirst(),
                        attributeNamesAndDefaultValues.getSecond(), rawMacroContent.toString()),
                getSupportedTagNames());
    }

    /** @return second macro attribute. */
    protected String getContentAttributeName() {
        if (hasAttribute(REPLACE_ATTRIBUTE)) {
            return getAttribute(REPLACE_ATTRIBUTE);
        } else if (GdxArrays.sizeOf(getAttributes()) > 1) {
            return getAttributes().get(1);
        }
        return null;
    }

    /** @return parsed additional macro attributes. */
    protected Pair<Array<String>, Array<String>> getAttributeNamesAndDefaultValues() {
        final Array<String> attributes = getAttributes();
        final Array<String> attributeNames = GdxArrays.newArray();
        final Array<String> defaultValues = GdxArrays.newArray();
        if (GdxArrays.sizeOf(attributes) > 2) {
            final LmlSyntax syntax = getParser().getSyntax();
            for (int index = 2, length = attributes.size; index < length; index++) {
                final String rawAttribute = attributes.get(index);
                if (Strings.contains(rawAttribute, syntax.getAttributeSeparator())) {
                    final int separatorIndex = rawAttribute.indexOf(syntax.getAttributeSeparator());
                    attributeNames.add(rawAttribute.substring(0, separatorIndex).trim());
                    defaultValues.add(LmlUtilities
                            .stripQuotation(rawAttribute.substring(separatorIndex + 1, rawAttribute.length())));
                } else {
                    attributeNames.add(rawAttribute.trim());
                    defaultValues.add(Nullables.DEFAULT_NULL_STRING);
                }
            }
        }
        return Pair.of(attributeNames, defaultValues);
    }

    /** @return first macro argument parsed as an array. */
    protected String[] getSupportedTagNames() {
        final Actor actor = getActor(); // Needed to parse raw LML data.
        final String attribute = hasAttribute(ALIAS_ATTRIBUTE) ? getAttribute(ALIAS_ATTRIBUTE)
                : getAttributes().first();
        final String[] names = getParser().parseArray(attribute, actor);
        for (int index = 0, length = names.length; index < length; index++) {
            // Arrays might not be fully parsed, but in this case, we need absolute macro names.
            names[index] = getParser().parseString(names[index], actor);
        }
        return names;
    }

    @Override
    public String[] getExpectedAttributes() {
        return new String[] { ALIAS_ATTRIBUTE, REPLACE_ATTRIBUTE };
    }

    /** Provides a custom macro tag created in LML templates.
     *
     * @author MJ */
    public static class CustomLmlMacroTagProvider implements LmlTagProvider {
        private final String contentAttributeName;
        private final Array<String> attributeNames;
        private final Array<String> defaultAttributeValues;
        private final String macroContent;

        public CustomLmlMacroTagProvider(final String contentAttributeName, final Array<String> attributeNames,
                final Array<String> defaultAttributeValues, final String macroContent) {
            this.contentAttributeName = contentAttributeName;
            this.attributeNames = attributeNames;
            this.defaultAttributeValues = defaultAttributeValues;
            this.macroContent = macroContent;
        }

        @Override
        public LmlTag create(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
            return new CustomLmlMacroTag(parser, parentTag, rawTagData, contentAttributeName, attributeNames,
                    defaultAttributeValues, macroContent);
        }
    }

    /** Represents a custom macro registered through LML template.
     *
     * @author MJ */
    public static class CustomLmlMacroTag extends AbstractMacroLmlTag {
        private final String contentAttributeName;
        private final Array<String> attributeNames;
        private final Array<String> defaultAttributeValues;
        private final String macroContent;
        private CharSequence contentBetweenTags;

        public CustomLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData,
                final String contentAttributeName, final Array<String> attributeNames,
                final Array<String> defaultAttributeValues, final String macroContent) {
            super(parser, parentTag, rawTagData);
            this.contentAttributeName = contentAttributeName;
            this.attributeNames = attributeNames;
            this.defaultAttributeValues = defaultAttributeValues;
            this.macroContent = macroContent;
        }

        @Override
        protected boolean supportsOptionalNamedAttributes() {
            return true;
        }

        @Override
        public void handleDataBetweenTags(final CharSequence rawMacroContent) {
            contentBetweenTags = rawMacroContent;
        }

        @Override
        public void closeTag() {
            appendTextToParse(replaceArguments(macroContent, getMacroArguments()));
        }

        private ObjectMap<String, CharSequence> getMacroArguments() {
            final ObjectMap<String, CharSequence> arguments = new IgnoreCaseStringMap<CharSequence>();
            if (contentAttributeName != null) {
                arguments.put(contentAttributeName,
                        contentBetweenTags == null ? Strings.EMPTY_STRING : contentBetweenTags);
            }
            if (GdxArrays.isEmpty(getAttributes())) {
                putDefaultAttributes(arguments);
            } else if (areAttributesNamed()) {
                putNamedAttributes(arguments);
            } else {
                putUnnamedAttributes(arguments);
            }
            return arguments;
        }

        private boolean areAttributesNamed() {
            boolean allNamed = true;
            boolean anyNamed = false;
            final LmlSyntax syntax = getParser().getSyntax();
            for (final String attribute : getAttributes()) {
                if (Strings.contains(attribute, syntax.getAttributeSeparator())) {
                    anyNamed = true;
                    allNamed &= true;
                } else {
                    allNamed = false;
                }
            }
            if (anyNamed && !allNamed) {
                getParser().throwError(
                        "Custom macros cannot have both named (\"attribute=value\") and unnamed (\"value\") attributes");
            }
            return allNamed;
        }

        private void putDefaultAttributes(final ObjectMap<String, CharSequence> arguments) {
            for (int index = 0, length = attributeNames.size; index < length; index++) {
                arguments.put(attributeNames.get(index), defaultAttributeValues.get(index));
            }
        }

        private void putNamedAttributes(final ObjectMap<String, CharSequence> arguments) {
            final ObjectMap<String, String> namedAttributes = getNamedAttributes();
            for (int index = 0, length = attributeNames.size; index < length; index++) {
                final String attributeName = attributeNames.get(index);
                if (namedAttributes.containsKey(attributeName)) {
                    arguments.put(attributeName, namedAttributes.get(attributeName));
                } else {
                    arguments.put(attributeName, defaultAttributeValues.get(index));
                }
            }
        }

        private void putUnnamedAttributes(final ObjectMap<String, CharSequence> arguments) {
            final Array<String> attributes = getAttributes();
            for (int index = 0, length = attributeNames.size; index < length; index++) {
                arguments.put(attributeNames.get(index),
                        index < attributes.size ? attributes.get(index) : defaultAttributeValues.get(index));
            }
        }

        @Override
        public String[] getExpectedAttributes() {
            // Creating typed array. This is a debugging method anyway.
            final Array<String> typedArray = GdxArrays.newArray(String.class);
            typedArray.addAll(attributeNames);
            return typedArray.toArray();
        }
    }
}
