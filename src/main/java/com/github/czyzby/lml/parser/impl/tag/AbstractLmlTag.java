package com.github.czyzby.lml.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;
import com.github.czyzby.lml.util.collection.IgnoreCaseStringMap;

/** Common base for all tag handlers.
 *
 * @author MJ
 * @see AbstractMacroLmlTag
 * @see AbstractActorLmlTag */
public abstract class AbstractLmlTag implements LmlTag {
    private final LmlParser parser;
    private final Array<String> attributes;
    private final ObjectMap<String, String> namedAttributes;
    private final String tagName;
    private final LmlTag parentTag;
    private final boolean parent, macro;

    public AbstractLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        this.parser = parser;
        this.parentTag = parentTag;
        final Array<String> entities = extractTagEntities(rawTagData, parser);
        final String tagName = entities.first();
        macro = Strings.startsWith(tagName, parser.getSyntax().getMacroMarker());
        this.tagName = LmlUtilities.stripMarker(tagName, parser.getSyntax().getMacroMarker());
        final String lastAttribute = GdxArrays.getLast(entities);
        if (Strings.endsWith(lastAttribute, parser.getSyntax().getClosedTagMarker())) {
            // The tag ends with a closing marker, which means it is a child.
            parent = false;
            if (lastAttribute.length() == 1) {
                GdxArrays.removeLast(entities);
            } else {
                entities.set(GdxArrays.sizeOf(entities) - 1, LmlUtilities.stripEnding(lastAttribute));
            }
        } else {
            parent = true;
        }
        if (hasAttributes(entities) || hasDefaultAttributes(tagName)) {
            entities.removeIndex(0); // Removing tag name from attributes.
            attributes = entities;
            if (supportsNamedAttributes() || supportsOptionalNamedAttributes()) {
                namedAttributes = new IgnoreCaseStringMap<String>();
            } else {
                namedAttributes = null;
            }
            fillAttributes(parser, entities);
        } else {
            attributes = null;
            namedAttributes = null;
        }
    }

    /** @param tagName name of the tag.
     * @return true if the tag has default attributes assigned. */
    protected boolean hasDefaultAttributes(final String tagName) {
        return false;
    }

    /** @return true if this tag type supports named attributes and they should be mapped. */
    protected abstract boolean supportsNamedAttributes();

    /** @return true if this tag can have both named and unnamed attributes. Named attributes will be filled only if ALL
     *         attributes are valid and named, but exception will not be thrown if some of the attributes are invalid.
     *         This is default behavior for some obscure macros. */
    protected boolean supportsOptionalNamedAttributes() {
        return false;
    }

    private static boolean hasAttributes(final Array<String> entities) {
        // The first entity is name, so at least 2 entities are required:
        return entities.size > 1;
    }

    private static Array<String> extractTagEntities(final StringBuilder rawTagData, final LmlParser parser) {
        Strings.replace(rawTagData, "\\n", "\n");
        Strings.replace(rawTagData, "&gt;", ">");
        boolean inQuotation = false, inDoubleQuotation = false, lastCharWhitespace = true;
        final Array<String> entities = GdxArrays.newArray(String.class);
        final StringBuilder builder = new StringBuilder();
        lastCharWhitespace = true;
        for (int index = 0, length = rawTagData.length(); index < length; index++) {
            final char character = rawTagData.charAt(index);
            if (Strings.isWhitespace(character)) {
                if (inQuotation || inDoubleQuotation) {
                    builder.append(character);
                    continue;
                } else if (lastCharWhitespace) {
                    continue;
                }
                lastCharWhitespace = true;
                entities.add(builder.toString());
                Strings.clearBuilder(builder);
            } else if (character == '\'') {
                lastCharWhitespace = false;
                builder.append(character);
                if (!inDoubleQuotation) {
                    inQuotation = !inQuotation;
                }
            } else if (character == '"') {
                lastCharWhitespace = false;
                builder.append(character);
                if (!inQuotation) {
                    inDoubleQuotation = !inDoubleQuotation;
                }
            } else {
                lastCharWhitespace = false;
                builder.append(character);
            }
        }
        if (!lastCharWhitespace) {
            entities.add(builder.toString());
        }
        return entities;
    }

    private void fillAttributes(final LmlParser parser, final Array<String> entities) {
        // Starting from 1, since 0 index is the tag name.
        for (int index = entities.size - 1; index >= 0; index--) { // Iterating backwards to take removal into account.
            final String rawAttribute = entities.get(index);
            if (Strings.isBlank(rawAttribute)) {
                entities.removeIndex(index);
                continue;
            }
            final String entity = LmlUtilities.stripQuotation(rawAttribute);
            entities.set(index, entity);
            if ((supportsNamedAttributes() || supportsOptionalNamedAttributes()) && Strings.isNotEmpty(entity)) {
                final int separatorIndex = entity.indexOf(parser.getSyntax().getAttributeSeparator());
                if (Strings.isCharacterAbsent(separatorIndex)) {
                    if (supportsNamedAttributes()) {
                        parser.throwErrorIfStrict("Invalid attribute format: \"" + entity
                                + "\". Attribute might be missing assignment character ('"
                                + parser.getSyntax().getAttributeSeparator() + "') or be otherwise unparseable.");
                    }
                    // Supports optionally: parsing another attribute.
                    continue;
                }
                final String attributeName = entity.substring(0, separatorIndex);
                final String attributeValue = LmlUtilities
                        .stripQuotation(entity.substring(separatorIndex + 1, entity.length()));
                namedAttributes.put(attributeName, attributeValue);
            }
        }
    }

    @Override
    public boolean isParent() {
        return parent;
    }

    @Override
    public boolean isChild() {
        return !parent;
    }

    @Override
    public boolean isMacro() {
        return macro;
    }

    @Override
    public LmlTag getParent() {
        return parentTag;
    }

    @Override
    public String getTagName() {
        return tagName;
    }

    @Override
    public Array<String> getAttributes() {
        return attributes;
    }

    @Override
    public ObjectMap<String, String> getNamedAttributes() {
        return namedAttributes;
    }

    @Override
    public boolean hasAttribute(final String name) {
        return namedAttributes != null && namedAttributes.containsKey(name);
    }

    @Override
    public String getAttribute(final String name) {
        return namedAttributes == null ? null : namedAttributes.get(name);
    }

    /** @return parser used to create this tag. */
    protected LmlParser getParser() {
        return parser;
    }

    /** @param builder contains ID of the skin.
     * @return utility shortcut method that returns skin from parser's LML data object. */
    protected Skin getSkin(final LmlActorBuilder builder) {
        return getSkin(builder.getSkinName());
    }

    /** @param name ID of the skin.
     * @return utility shortcut method that returns skin from parser's LML data object. */
    protected Skin getSkin(final String name) {
        final Skin skin = findSkin(name);
        if (skin == null) {
            parser.throwError("Unknown skin ID. Skin with name: " + name + " is unavailable.");
        }
        return skin;
    }

    private Skin findSkin(final String name) {
        if (name == null) {
            return parser.getData().getDefaultSkin();
        }
        return parser.getData().getSkin(name);
    }

    @Override
    public boolean isAttachable() {
        // By default, both macros and simple widgets are not attachable.
        return false;
    }

    @Override
    public void attachTo(final LmlTag tag) {
        // By default, both macros and simple widgets are not attachable.
        throw new IllegalStateException("This tag is not attachable: " + tagName);
    }

    /** Utility method. Allows to extract a {@link Stage} instance of either the passed actor or any of its parents.
     *
     * @param actor can be null. Its stage can be null.
     * @return a {@link Stage} instance if any of the actors in hierarchy is added to one. */
    protected Stage determineStage(final Actor actor) {
        Stage stage = actor == null ? null : actor.getStage();
        if (stage == null) {
            LmlTag ancestorTag = getParent();
            while (ancestorTag != null && stage == null) {
                final Actor ancestor = ancestorTag.getActor();
                if (ancestor != null) {
                    stage = ancestor.getStage();
                }
                ancestorTag = ancestorTag.getParent();
            }
        }
        return stage;
    }
}
