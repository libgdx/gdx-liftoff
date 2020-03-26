package com.github.czyzby.lml.parser.impl.tag;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.github.czyzby.kiwi.util.common.Exceptions;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.attribute.table.cell.AbstractCellLmlAttribute;
import com.github.czyzby.lml.parser.impl.tag.macro.AbstractConditionalLmlMacroTag;
import com.github.czyzby.lml.parser.impl.tag.macro.TableCellLmlMacroTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;
import com.github.czyzby.lml.util.Lml;

/** Allows to create DTD schema files for LML templates.
 *
 * @author MJ */
public class Dtd {
    protected static final String XML_ELEMENT_REGEX = "[\\w:.-]+";
    private boolean displayLogs = true;
    private boolean appendComments = true;

    /** @param parser contains parsing data. Used to create mock-up actors. The skin MUST be fully loaded and contain
     *            all used actors' styles for the generator to work properly.
     * @return DTD schema file containing all possible tags and their attributes. Any problems with the generation will
     *         be logged. This is a relatively heavy operation and should be done only during development.
     * @see #getDtdSchema(LmlParser, Appendable) */
    public static String getSchema(final LmlParser parser) {
        final StringBuilder builder = new StringBuilder();
        try {
            new Dtd().getDtdSchema(parser, builder);
        } catch (final IOException exception) {
            throw new GdxRuntimeException("Unexpected: unable to append.", exception);
        }
        return builder.toString();
    }

    /** Saves DTD schema file containing all possible tags and their attributes. Any problems with the generation will
     * be logged. This is a relatively heavy operation and should be done only during development.
     *
     * @param parser contains parsing data. Used to create mock-up actors. The skin MUST be fully loaded and contain all
     *            used actors' styles for the generator to work properly.
     * @param appendable a reference to the file.
     * @see #getDtdSchema(LmlParser, Appendable)
     * @see #saveMinifiedSchema(LmlParser, Appendable) */
    public static void saveSchema(final LmlParser parser, final Appendable appendable) {
        try {
            new Dtd().getDtdSchema(parser, appendable);
        } catch (final IOException exception) {
            throw new GdxRuntimeException("Unable to append to file.", exception);
        }
    }

    /** Saves DTD schema file containing all possible tags and their attributes. Any problems with the generation will
     * be logged. This is a relatively heavy operation and should be done only during development. Comments will not be
     * appended, which will reduce the size of DTD file.
     *
     * @param parser contains parsing data. Used to create mock-up actors. The skin MUST be fully loaded and contain all
     *            used actors' styles for the generator to work properly.
     * @param appendable a reference to the file.
     * @see #getDtdSchema(LmlParser, Appendable)
     * @see #saveSchema(LmlParser, Appendable) */
    public static void saveMinifiedSchema(final LmlParser parser, final Appendable appendable) {
        try {
            new Dtd().setAppendComments(false).getDtdSchema(parser, appendable);
        } catch (final IOException exception) {
            throw new GdxRuntimeException("Unable to append to file.", exception);
        }
    }

    /** @param displayLogs defaults to true. If set to false, parsing messages will not be shown in the console.
     * @return this, for chaining. */
    public Dtd setDisplayLogs(final boolean displayLogs) {
        this.displayLogs = displayLogs;
        return this;
    }

    /** @param appendComments defaults to true. If false, corresponding Java classes will not be added as comments to
     *            the DTD file.
     * @return this, for chaining. */
    public Dtd setAppendComments(final boolean appendComments) {
        this.appendComments = appendComments;
        return this;
    }

    /** @param message will be displayed in the console. */
    protected void log(final String message) {
        if (displayLogs) {
            Gdx.app.log(Lml.LOGGER_TAG, message);
        }
    }

    /** Creates DTD schema file containing all possible tags and their attributes. Any problems with the generation will
     * be logged. This is a relatively heavy operation and should be done only during development.
     *
     * @param parser contains parsing data. Used to create mock-up actors. The skin MUST be fully loaded and contain all
     *            used actors' styles for the generator to work properly.
     * @param builder values will be appended to this object.
     * @throws IOException when unable to append. */
    public void getDtdSchema(final LmlParser parser, final Appendable builder) throws IOException {
        appendActorTags(builder, parser);
        appendActorAttributes(parser, builder);
        appendMacroTags(builder, parser);
        appendMacroAttributes(parser, builder);
    }

    protected void appendDtdElement(final Appendable builder, final String comment, final String name)
            throws IOException {
        appendDtdElement(builder, comment, Strings.EMPTY_STRING, name);
    }

    protected void appendDtdElement(final Appendable builder, final String comment, final String prefix,
            final String name) throws IOException {
        appendDtdElement(builder, comment, prefix, name, "ANY");
    }

    protected void appendDtdElement(final Appendable builder, final String comment, final String prefix,
            final String name, final String type) throws IOException {
        if (!name.matches(XML_ELEMENT_REGEX)) {
            log("Warning: '" + name + "' tag might contain invalid XML characters.");
        }
        if (appendComments) {
            builder.append("<!-- ").append(comment).append(" -->\n");
        }
        builder.append("<!ELEMENT ").append(prefix).append(name).append(' ').append(type).append(">\n");
    }

    protected void appendDtdAttributes(final Appendable builder, final String tagName,
            final ObjectMap<String, Object> attributes) throws IOException {
        if (appendComments) {
            for (final Entry<String, Object> attribute : attributes) {
                if (!attribute.key.matches(XML_ELEMENT_REGEX)) {
                    log("Warning: '" + attribute + "' attribute might contain invalid XML characters.");
                }
                builder.append("<!-- ").append(attribute.value.getClass().getSimpleName()).append(" -->\n");
                builder.append("<!ATTLIST ").append(tagName).append(' ').append(attribute.key)
                        .append(" CDATA #IMPLIED>\n");
            }
            return;
        }
        builder.append("<!ATTLIST ").append(tagName);
        for (final Entry<String, Object> attribute : attributes) {
            if (!attribute.key.matches(XML_ELEMENT_REGEX)) {
                log("Warning: '" + attribute + "' attribute might contain invalid XML characters.");
            }
            builder.append("\n\t").append(attribute.key).append(" CDATA #IMPLIED");
        }
        builder.append(">\n");
    }

    protected void appendActorTags(final Appendable builder, final LmlParser parser) throws IOException {
        if (appendComments) {
            builder.append("<!-- Actor tags: -->\n");
        }
        final ObjectMap<String, LmlTagProvider> actorTags = parser.getSyntax().getTags();
        for (final Entry<String, LmlTagProvider> actorTag : actorTags) {
            appendDtdElement(builder, getTagClassName(actorTag.value), actorTag.key);
        }
    }

    protected String getTagClassName(final LmlTagProvider provider) {
        final String providerClass = provider.getClass().getSimpleName();
        return providerClass.endsWith("Provider")
                ? providerClass.substring(0, providerClass.length() - "Provider".length()) : providerClass;
    }

    @SuppressWarnings("unchecked")
    protected void appendActorAttributes(final LmlParser parser, final Appendable builder) throws IOException {
        if (appendComments) {
            builder.append("<!-- Actor tags' attributes: -->\n");
        }
        final ObjectMap<String, LmlTagProvider> actorTags = parser.getSyntax().getTags();
        for (final Entry<String, LmlTagProvider> actorTag : actorTags) {
            final ObjectMap<String, Object> attributes = GdxMaps.newObjectMap();
            try {
                final LmlTag tag = actorTag.value.create(parser, null, new StringBuilder(actorTag.key));
                if (tag.getActor() == null) {
                    appendNonActorTagAttributes(tag, attributes, parser);
                } else {
                    appendActorTagAttributes(tag, attributes, parser);
                }
            } catch (final Exception exception) {
                Exceptions.ignore(exception);
                log("Warning: unable to create an instance of actor mapped to '" + actorTag.key
                        + "' tag name with provider: " + actorTag.value
                        + ". Attributes list will not be complete. Is the provider properly implemented? Is a default style provided for the selected actor?");
                attributes.putAll(
                        (ObjectMap<String, Object>) (Object) parser.getSyntax().getAttributesForActor(new Actor()));
                attributes.putAll((ObjectMap<String, Object>) (Object) parser.getSyntax()
                        .getAttributesForBuilder(new LmlActorBuilder()));
            }
            appendDtdAttributes(builder, actorTag.key, attributes);
        }
    }

    @SuppressWarnings("unchecked")
    protected void appendNonActorTagAttributes(final LmlTag tag, final ObjectMap<String, Object> attributes,
            final LmlParser parser) {
        final Object managedObject = tag.getManagedObject();
        if (managedObject != null) {
            attributes.putAll(
                    (ObjectMap<String, Object>) (Object) parser.getSyntax().getAttributesForActor(managedObject));
        }
    }

    @SuppressWarnings("unchecked")
    protected void appendActorTagAttributes(final LmlTag tag, final ObjectMap<String, Object> attributes,
            final LmlParser parser) {
        LmlActorBuilder actorBuilder;
        final boolean usesAbstractBase = tag instanceof AbstractActorLmlTag;
        if (usesAbstractBase) {
            actorBuilder = ((AbstractActorLmlTag) tag).getNewInstanceOfBuilder();
        } else {
            actorBuilder = new LmlActorBuilder();
        }
        // Appending attributes of component actors:
        if (!Lml.DISABLE_COMPONENT_ACTORS_ATTRIBUTE_PARSING && usesAbstractBase
                && ((AbstractActorLmlTag) tag).hasComponentActors()) {
            for (final Actor component : ((AbstractActorLmlTag) tag).getComponentActors(tag.getActor())) {
                attributes.putAll(
                        (ObjectMap<String, Object>) (Object) parser.getSyntax().getAttributesForActor(component));
            }
        }
        // Appending managed objects attributes:
        if (tag.getManagedObject() != tag.getActor()) {
            appendNonActorTagAttributes(tag, attributes, parser);
        }
        // Appending building attributes:
        attributes
                .putAll((ObjectMap<String, Object>) (Object) parser.getSyntax().getAttributesForBuilder(actorBuilder));
        // Appending regular attributes:
        attributes
                .putAll((ObjectMap<String, Object>) (Object) parser.getSyntax().getAttributesForActor(tag.getActor()));
    }

    protected void appendMacroTags(final Appendable builder, final LmlParser parser) throws IOException {
        if (appendComments) {
            builder.append("<!-- Macro tags: -->\n");
        }
        final String macroMarker = String.valueOf(parser.getSyntax().getMacroMarker());
        if (!macroMarker.matches(XML_ELEMENT_REGEX)) {
            log("Error: current macro marker (" + macroMarker
                    + ") is an invalid XML character. Override getMacroMarker in your current LmlSyntax implementation and provide a correct character to create valid DTD file.");
        }
        final ObjectMap<String, LmlTagProvider> macroTags = parser.getSyntax().getMacroTags();
        for (final Entry<String, LmlTagProvider> macroTag : macroTags) {
            appendDtdElement(builder, getTagClassName(macroTag.value), macroMarker, macroTag.key);
            // If the tag is conditional, it should provide an extra name:else tag:
            try {
                final LmlTag tag = macroTag.value.create(parser, null, new StringBuilder(macroTag.key));
                if (tag instanceof AbstractConditionalLmlMacroTag) {
                    appendDtdElement(builder, "'Else' helper tag of: " + macroTag.key, macroMarker,
                            macroTag.key + AbstractConditionalLmlMacroTag.ELSE_SUFFIX, "EMPTY");
                }
            } catch (final Exception expected) {
                // Tag might need a parent or additional attributes and cannot be checked. It's OK.
                Exceptions.ignore(expected);
                log("Unable to create a macro tag instance using: " + macroTag.value.getClass().getSimpleName());
            }
        }
    }

    protected void appendMacroAttributes(final LmlParser parser, final Appendable builder) throws IOException {
        if (appendComments) {
            builder.append("<!-- Expected macro tags' attributes: -->\n");
        }
        final String macroMarker = String.valueOf(parser.getSyntax().getMacroMarker());
        final ObjectMap<String, LmlTagProvider> macroTags = parser.getSyntax().getMacroTags();
        for (final Entry<String, LmlTagProvider> macroTag : macroTags) {
            try {
                final LmlTag tag = macroTag.value.create(parser, null, new StringBuilder(macroTag.key));
                if (tag instanceof TableCellLmlMacroTag) {
                    // Special case: listing all cell attributes:
                    appendTableDefaultsMacro(parser, builder, macroMarker, macroTag, tag);
                } else if (tag instanceof AbstractMacroLmlTag) {
                    final String[] attributeNames = ((AbstractMacroLmlTag) tag).getExpectedAttributes();
                    if (attributeNames == null || attributeNames.length == 0) {
                        continue;
                    }
                    final ObjectMap<String, Object> attributes = GdxMaps.newObjectMap();
                    for (final String attributeName : attributeNames) {
                        attributes.put(attributeName, tag);
                    }
                    appendDtdAttributes(builder, macroMarker + macroTag.key, attributes);
                }
            } catch (final Exception expected) {
                // Tag might need a parent or additional attributes and cannot be checked. It's OK.
                Exceptions.ignore(expected);
            }
        }
    }

    private void appendTableDefaultsMacro(final LmlParser parser, final Appendable builder, final String macroMarker,
            final Entry<String, LmlTagProvider> macroTag, final LmlTag tag) throws IOException {
        final Actor mockUp = new Actor();
        final ObjectMap<String, Object> attributes = GdxMaps.newObjectMap();
        for (final Entry<String, LmlAttribute<?>> attribute : parser.getSyntax().getAttributesForActor(mockUp)) {
            if (attribute.value instanceof AbstractCellLmlAttribute) {
                attributes.put(attribute.key, attribute.value);
            }
        }
        final String[] attributeNames = ((AbstractMacroLmlTag) tag).getExpectedAttributes();
        if (attributeNames != null && attributeNames.length > 0) {
            for (final String attribute : attributeNames) {
                attributes.put(attribute, tag);
            }
        }
        appendDtdAttributes(builder, macroMarker + macroTag.key, attributes);
    }
}
