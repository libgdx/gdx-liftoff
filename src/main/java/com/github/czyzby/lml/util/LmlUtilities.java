package com.github.czyzby.lml.util;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.ObjectSet;
import com.github.czyzby.kiwi.util.common.Exceptions;
import com.github.czyzby.kiwi.util.common.Nullables;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.kiwi.util.gdx.collection.pooled.PooledList;
import com.github.czyzby.kiwi.util.gdx.scene2d.Alignment;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.LmlSyntax;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.action.StageAttacher;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.collection.IgnoreCaseStringMap;

/** Utility class. Contains common LML methods that might be useful during parsing or even LML actors usage.
 *
 * @author MJ */
public class LmlUtilities {
    private static final ObjectMap<String, Value> STATIC_TABLE_VALUES;

    static {
        final ObjectMap<String, Value> initialValues = GdxMaps.newObjectMap("minHeight", Value.minHeight, "prefHeight",
                Value.prefHeight, "maxHeight", Value.maxHeight, "minWidth", Value.minWidth, "prefWidth",
                Value.prefWidth, "maxWidth", Value.maxWidth);
        STATIC_TABLE_VALUES = new IgnoreCaseStringMap<Value>(initialValues);
    }

    /** Empty object array. Might be useful for method invocations with no arguments. */
    public static final Object[] EMPTY_ARRAY = new Object[0];

    private LmlUtilities() {
    }

    /** Registers the given {@link Value} under passed name. These values should be generic and work only on any actors,
     * without parsed attributes. Where a Value object is expected (for example - in {@link Table} cell sizes), you can
     * pass chosen valueName as the attribute and the corresponding Value implementation will be taken from a map.
     *
     * @param valueName will register value under this key.
     * @param value returned each time a value-requiring attribute contains the passed name. */
    public static void registerStaticTableValue(final String valueName, final Value value) {
        STATIC_TABLE_VALUES.put(valueName, value);
    }

    /** @param fromValue ends with a marker that should be removed.
     * @return passed value without last character. */
    public static String stripEnding(final String fromValue) {
        return fromValue.substring(0, fromValue.length() - 1);
    }

    /** @param fromValue ends with a marker that should be removed.
     * @param ifEqualToThisMarker expected, optional ending marker.
     * @return passed value without last character if it ends with the passed marker or the original value if it does
     *         not. */
    public static String stripEnding(final String fromValue, final char ifEqualToThisMarker) {
        if (Strings.endsWith(fromValue, ifEqualToThisMarker)) {
            return fromValue.substring(0, fromValue.length() - 1);
        }
        return fromValue;
    }

    /** @param fromValue starts with a marker character that should be removed.
     * @return passed value without the marker. */
    public static String stripMarker(final String fromValue) {
        return fromValue.substring(1);
    }

    /** @param fromValue may or may not start with a marker character that should be removed.
     * @param ifEqualToThisMarker expected, optional marker.
     * @return value without the initial marker. */
    public static String stripMarker(final String fromValue, final char ifEqualToThisMarker) {
        if (ifEqualToThisMarker == fromValue.charAt(0)) {
            return stripMarker(fromValue);
        }
        return fromValue;
    }

    /** @param fromValue if it starts and ends with single or double quotation, quotation characters will be stripped.
     * @return passed value without quotation, if quotation was present. */
    public static String stripQuotation(final String fromValue) {
        if (fromValue == null || fromValue.length() < 2) {
            // If string is empty or not long enough to contain quotation, returning unchanged.
            return fromValue;
        }
        final int lastIndex = Strings.getLastIndex(fromValue); // length - 1
        if (fromValue.charAt(0) == '\'' && fromValue.charAt(lastIndex) == '\''
                || fromValue.charAt(0) == '"' && fromValue.charAt(lastIndex) == '"') {
            return fromValue.substring(1, lastIndex);
        }
        return fromValue;
    }

    /** @param actor will have its ID attached using actor internal methods: ID will become actor's name.
     * @param id will become actor's ID. */
    public static void setActorId(final Actor actor, final String id) {
        actor.setName(id);
    }

    /** @param actor might have an ID attached using name setter.
     * @return actor's ID or null. */
    public static String getActorId(final Actor actor) {
        return actor.getName();
    }

    /** @param group will be recursively searched. Does not require loops, but in a properly structured Scene, they
     *            should never appear. This is a relatively expensive operations big views and other means of getting
     *            references to actors are preferred.
     * @param actorId ID of the actor to find.
     * @return instance of the actor with the selected ID (ignoring case) or null if not found. */
    public static Actor getActorWithId(final Group group, final String actorId) {
        final PooledList<Group> groupsToSearch = new PooledList<Group>();
        groupsToSearch.add(group);
        while (!groupsToSearch.isEmpty()) {
            for (final Actor actor : groupsToSearch.removeFirst().getChildren()) {
                if (actorId.equalsIgnoreCase(actor.getName())) {
                    return actor;
                } else if (actor instanceof Group) {
                    groupsToSearch.add((Group) actor);
                }
            }
        }
        return null;
    }

    /** @param actors clears attached {@link LmlUserObject}s with LML meta-data if any of the actors has one. */
    public static void clearLmlUserObjects(final Iterable<Actor> actors) {
        for (final Actor actor : actors) {
            clearLmlUserObject(actor);
        }
    }

    /** @param actor clears attached {@link LmlUserObject} with LML meta-data if it has one. If the actor is a
     *            {@link Group}, it will be searched - all its children will have the {@link LmlUserObject}s cleared
     *            recursively. */
    public static void clearLmlUserObject(final Actor actor) {
        if (actor.getUserObject() instanceof LmlUserObject) {
            actor.setUserObject(null);
        }
        if (actor instanceof Group) {
            final PooledList<Group> groupsToSearch = new PooledList<Group>();
            groupsToSearch.add((Group) actor);
            while (!groupsToSearch.isEmpty()) {
                for (final Actor child : groupsToSearch.removeFirst().getChildren()) {
                    if (child.getUserObject() instanceof LmlUserObject) {
                        child.setUserObject(null);
                    }
                    if (child instanceof Group) {
                        groupsToSearch.add((Group) child);
                    }
                }
            }
        }
    }

    /** @param actor might be a tree node.
     * @return tree node containing the actor if it is a tree node or null. */
    public static Tree.Node getTreeNode(final Actor actor) {
        final LmlUserObject userObject = getOptionalLmlUserObject(actor);
        if (userObject != null) {
            return userObject.getNode();
        }
        return null;
    }

    /** @param actor might be a text-based widget with a multiline property set to true.
     * @return false if actor has no user object attached or the multiline property is set to false. */
    public static boolean isMultiline(final Actor actor) {
        final LmlUserObject userObject = getOptionalLmlUserObject(actor);
        if (userObject != null && userObject.getData() instanceof Boolean) {
            return (Boolean) userObject.getData();
        }
        return false;
    }

    /** @param actor might have a LmlUserObject attached.
     * @return previous LmlUserObject instance attached to the actor or a new instance, which will also be set as
     *         actor's user object. As opposed to {@link #getOptionalLmlUserObject(Actor)}, this forces the actor to
     *         have a LmlUserObject attached.
     * @see Actor#setUserObject(Object) */
    public static LmlUserObject getLmlUserObject(final Actor actor) {
        LmlUserObject userObject;
        if (actor.getUserObject() instanceof LmlUserObject) {
            userObject = (LmlUserObject) actor.getUserObject();
        } else {
            userObject = new LmlUserObject();
            actor.setUserObject(userObject);
        }
        return userObject;
    }

    /** @param actor might have a LmlUserObject attached.
     * @return LmlUserObject instance attached to the actor or null. */
    public static LmlUserObject getOptionalLmlUserObject(final Actor actor) {
        if (actor != null && actor.getUserObject() instanceof LmlUserObject) {
            return (LmlUserObject) actor.getUserObject();
        }
        return null;
    }

    /** @param stage should contain the actors.
     * @param actors will be added to the stage, honoring their {@link StageAttacher} settings. */
    public static void appendActorsToStage(final Stage stage, final Iterable<Actor> actors) {
        if (actors == null) {
            return;
        } else if (stage == null) {
            throw new LmlParsingException(
                    "Cannot append actors: the stage is null. Are you sure the correct stage has been passed? If this method was invoked by a listener, are you sure that the actor is properly added to a stage?");
        }
        for (final Actor actor : actors) {
            stage.addActor(actor);
            final StageAttacher attacher = getStageAttacher(actor);
            if (attacher != null) {
                attacher.attachToStage(actor, stage);
            }
        }
    }

    /** @param actor might have a stage attacher set using its internal "user object" mechanism.
     * @return actor's stage attacher or null if not set. */
    public static StageAttacher getStageAttacher(final Actor actor) {
        final LmlUserObject userObject = getOptionalLmlUserObject(actor);
        return userObject == null ? null : userObject.getStageAttacher();
    }

    /** @param actor will have a stage attacher set using actor's internal "user object" mechanism.
     * @param stageAttacher used to attach the actor to a stage. */
    public static void setStageAttacher(final Actor actor, final StageAttacher stageAttacher) {
        final LmlUserObject userObject = getLmlUserObject(actor);
        userObject.setStageAttacher(stageAttacher);
    }

    /** @param actor might be in a cell. Should be added to a cell if its parent is a Table.
     * @param parent direct parent tag of the actor.
     * @return actor's cell or null if not in a table. */
    public static Cell<?> getCell(final Actor actor, final LmlTag parent) {
        final LmlUserObject userObject = getLmlUserObject(actor);
        if (userObject.getCell() == null) {
            if (parent != null && parent.getActor() instanceof Table) {
                return getCell(actor, (Table) parent.getActor());
            }
            return null;
        }
        return userObject.getCell();
    }

    /** @param actor might be in the passed table.
     * @param table if actor is currently not in a table, he will be added to this table.
     * @return a cell containing the actor. Returns null if the passed ta */
    public static Cell<?> getCell(final Actor actor, final Table table) {
        final LmlUserObject userObject = getLmlUserObject(actor);
        if (userObject.getCell() == null) {
            if (table == null) {
                throw new IllegalArgumentException("Table cannot be null. Unable to add actor to cell.");
            }
            userObject.setCell(userObject.getTableTarget().add(table, actor));
            final Table target = userObject.getTableTarget().extract(table);
            if (isOneColumn(target)) {
                target.row();
            }
        }
        return userObject.getCell();
    }

    /** @param table might be set as one column table.
     * @return true if the table is set as one column using user object mechanism */
    public static boolean isOneColumn(final Table table) {
        final LmlUserObject userObject = getOptionalLmlUserObject(table);
        if (userObject != null && userObject.getData() instanceof Boolean) {
            return ((Boolean) userObject.getData()).booleanValue();
        }
        return false;
    }

    /** @param parser parses an LML template.
     * @param actor needs an alignment.
     * @param rawAttributeData raw data to parse.
     * @return alignment value from enum constant with the selected name. If invalid name is passed, strict parser will
     *         throw an exception; non-strict parsers return the default, centered alignment.
     * @see Alignment */
    public static int parseAlignment(final LmlParser parser, final Actor actor, final String rawAttributeData) {
        final String parsedData = parser.parseString(rawAttributeData, actor);
        if (Strings.isInt(parsedData)) {
            // Attribute was an int. Someone might be trying to set alignment directly; it's OK.
            return parseAlignmentFromInt(parser, Integer.parseInt(parsedData));
        }
        try {
            final Alignment alignment = Alignment.valueOf(Strings.toUpperCase(parsedData));
            return alignment.getAlignment();
        } catch (final Exception exception) {
            Exceptions.ignore(exception); // Somewhat expected if invalid name is passed.
        }
        parser.throwErrorIfStrict("Unable to parser alignment from raw data: " + rawAttributeData
                + "; no alignment value matching: " + parsedData);
        return Alignment.CENTER.getAlignment();
    }

    private static int parseAlignmentFromInt(final LmlParser parser, final int parsedData) {
        if (parser.isStrict()) {
            // Validating passed int value. If it is a correct alignment, there will be an enum constant present.
            final Alignment alignment = Alignment.get(parsedData);
            if (alignment != null) {
                return alignment.getAlignment();
            }
            parser.throwError("Numeric alignment setting passed: " + parsedData
                    + ", but there is no alignment with this exact value.");
        }
        // Parser not strict: returning the value, regardless of its validity:
        return parsedData;
    }

    /** @param parser parses an LML template.
     * @param parent contains the actor.
     * @param actor needs a {@link Value} for vertical setting.
     * @param rawAttributeData raw data to parse. If is a simple float, fixed value will be returned. If starts with
     *            '%', will use {@link Value#percentHeight(float)} - will return a per cent of parsed actor's height. If
     *            ends with '%', will use {@link Value#percentHeight(float, Actor)} - will return a per cent of
     *            {@link Table} parent width. If a string value, will look for static values registered with
     *            {@link #registerStaticTableValue(String, Value)} (there are some default values; see this class
     *            sources for more info).
     * @return LibGDX Scene2D float value provider parsed from the raw attribute. */
    public static Value parseVerticalValue(final LmlParser parser, final LmlTag parent, final Actor actor,
            final String rawAttributeData) {
        final Object valueToParse = determineValueObjectName(parser, actor, rawAttributeData);
        if (valueToParse instanceof Value) {
            return (Value) valueToParse;
        }
        return determineVerticalValue(parser, parent, actor, Nullables.toString(valueToParse));
    }

    private static Value determineVerticalValue(final LmlParser parser, final LmlTag parent, final Actor actor,
            final String valueToParse) {
        if (Strings.isFloat(valueToParse)) {
            return new Value.Fixed(parser.parseFloat(valueToParse, actor));
        } else if (Strings.endsWith(valueToParse, '%')) {
            return Value.percentHeight(parser.parseFloat(stripEnding(valueToParse), actor),
                    getParentActorForValueParsing(parent, actor));
        } else if (Strings.startsWith(valueToParse, '%')) {
            return Value.percentHeight(parser.parseFloat(stripMarker(valueToParse), actor));
        }
        if (!STATIC_TABLE_VALUES.containsKey(valueToParse)) {
            parser.throwError(
                    "Unable to determine Value object. Value has to be a float, float beginning or ending with '%' or static value reference. See LmlUtilities#parseVerticalValue(LmlParser, LmlTag, Actor, String). Received: "
                            + valueToParse);
        }
        return STATIC_TABLE_VALUES.get(valueToParse);
    }

    /** @param parent contains the actor. Might be a Table with multiple nested tables.
     * @param actor needs a {@link Value}.
     * @return direct parent widget of the passed actor. */
    private static Actor getParentActorForValueParsing(final LmlTag parent, final Actor actor) {
        if (parent == null || parent.getActor() == actor) {
            // The value is parsed for the Table or Container itself.
            return actor;
        }
        return parent.getActor() instanceof Table ? getCell(actor, parent).getTable() : parent.getActor();
    }

    /** @param parser parses an LML template.
     * @param parent contains the actor.
     * @param actor needs a {@link Value} for horizontal setting.
     * @param rawAttributeData raw data to parse. If is a simple float, fixed value will be returned. If starts with
     *            '%', will use {@link Value#percentWidth(float)} - will return a per cent of parsed actor's width. If
     *            ends with '%', will use {@link Value#percentWidth(float, Actor)} - will return a per cent of
     *            {@link Table} parent width. If a string value, will look for static values registered with
     *            {@link #registerStaticTableValue(String, Value)} (there are some default values; see this class
     *            sources for more info).
     * @return LibGDX Scene2D float value provider parsed from the raw attribute. */
    public static Value parseHorizontalValue(final LmlParser parser, final LmlTag parent, final Actor actor,
            final String rawAttributeData) {
        final Object valueToParse = determineValueObjectName(parser, actor, rawAttributeData);
        if (valueToParse instanceof Value) {
            return (Value) valueToParse;
        }
        return determineHorizontalValue(parser, parent, actor, Nullables.toString(valueToParse));
    }

    private static Object determineValueObjectName(final LmlParser parser, final Actor actor,
            final String rawAttributeData) {
        if (Strings.startsWith(rawAttributeData, parser.getSyntax().getMethodInvocationMarker())) {
            final ActorConsumer<?, Actor> action = parser.parseAction(rawAttributeData, actor);
            if (action == null) {
                parser.throwErrorIfStrict("Cannot determine Value object with invalid action ID: " + rawAttributeData
                        + " for actor: " + actor);
            }
            return action.consume(actor);
        }
        return parser.parseString(rawAttributeData, actor);
    }

    private static Value determineHorizontalValue(final LmlParser parser, final LmlTag parent, final Actor actor,
            final String valueToParse) {
        if (Strings.isFloat(valueToParse)) {
            return new Value.Fixed(parser.parseFloat(valueToParse, actor));
        } else if (Strings.endsWith(valueToParse, '%')) {
            return Value.percentWidth(parser.parseFloat(stripEnding(valueToParse), actor),
                    getParentActorForValueParsing(parent, actor));
        } else if (Strings.startsWith(valueToParse, '%')) {
            return Value.percentWidth(parser.parseFloat(stripMarker(valueToParse), actor));
        }
        if (!STATIC_TABLE_VALUES.containsKey(valueToParse)) {
            parser.throwError(
                    "Unable to determine Value object. Value has to be a float, float beginning or ending with '%' or static value reference. See LmlUtilities#parseHorizontalValue(LmlParser, LmlTag, Actor, String). Received: "
                            + valueToParse);
        }
        return STATIC_TABLE_VALUES.get(valueToParse);
    }

    /** Utility method that processes all named attributes of the selected type.
     *
     * @param widget widget (validator, actor - processed element) that will be used to process attributes.
     * @param tag contains attributes.
     * @param parser will be used to parse attributes.
     * @param processedAttributes already processed attribute names. These attributes will be ignored. Optional, can be
     *            null.
     * @param throwExceptionIfAttributeUnknown if true and unknown attribute is found, a strict parser will throw an
     *            exception.
     * @param <Type> type of processed widget. Its class tree will be used to retrieve attributes. */
    public static <Type> void processAttributes(final Type widget, final LmlTag tag, final LmlParser parser,
            final ObjectSet<String> processedAttributes, final boolean throwExceptionIfAttributeUnknown) {
        if (GdxMaps.isEmpty(tag.getNamedAttributes())) {
            return;
        }
        final LmlSyntax syntax = parser.getSyntax();
        final boolean hasProcessedAttributes = processedAttributes != null;
        for (final Entry<String, String> attribute : tag.getNamedAttributes()) {
            if (attribute == null || hasProcessedAttributes && processedAttributes.contains(attribute.key)) {
                continue;
            }
            final LmlAttribute<Type> attributeProcessor = syntax.getAttributeProcessor(widget, attribute.key);
            if (attributeProcessor == null) {
                if (throwExceptionIfAttributeUnknown) {
                    parser.throwErrorIfStrict("Unknown attribute: \"" + attribute.key + "\" for widget type: "
                            + widget.getClass().getName());
                }
                continue;
            }
            attributeProcessor.process(parser, tag, widget, attribute.value);
            if (hasProcessedAttributes) {
                processedAttributes.add(attribute.key);
            }
        }
    }

    // Syntax helpers:

    /** Warning: uses default LML syntax. Will not work if you modified any LML markers.
     *
     * @param array will be converted to an LML array argument using default syntax.
     * @return unparsed LML array. */
    public static String toArrayArgument(final Object... array) {
        return Strings.join(";", array);
    }

    /** Warning: uses default LML syntax. Will not work if you modified any LML markers.
     *
     * @param iterable will be converted to an LML array argument using default syntax.
     * @return unparsed LML array. */
    public static String toArrayArgument(final Iterable<?> iterable) {
        return Strings.join(";", iterable);
    }

    /** Warning: uses default LML syntax. Will not work if you modified any LML markers.
     *
     * @param base base of the range. Can be null - range will not have a base and will iterate solely over numbers.
     * @param rangeStart start of range. Can be negative. Does not have to be lower than end - if start is bigger, range
     *            is iterating from bigger to lower values.
     * @param rangeEnd end of range. Can be negative.
     * @return range is format: base + rangeOpening + start + separator + end + rangeClosing. For example,
     *         "base[4,2]". */
    public static String toRangeArrayArgument(final Object base, final int rangeStart, final int rangeEnd) {
        return Nullables.toString(base, Strings.EMPTY_STRING) + '[' + rangeStart + ',' + rangeEnd + ']';
    }

    /** Warning: uses default LML syntax. Will not work if you modified any LML markers.
     *
     * @param bundleLineId name of a bundle line in default i18n bundle.
     * @return converted LML bundle line argument using default syntax. */
    public static String toBundleLine(final String bundleLineId) {
        return '@' + bundleLineId;
    }

    /** Warning: uses default LML syntax. Will not work if you modified any LML markers.
     *
     * @param bundleId name of the bundle, as registered to LML data container.
     * @param bundleLineId name of a bundle line in the specified i18n bundle.
     * @return converted LML bundle line argument using default syntax. */
    public static String toBundleLine(final String bundleId, final String bundleLineId) {
        return '@' + bundleId + '.' + bundleLineId;
    }

    /** Warning: uses default LML syntax. Will not work if you modified any LML markers.
     *
     * @param preferenceId name of a preference in default preferences object.
     * @return converted LML preference argument using default syntax. */
    public static String toPreference(final String preferenceId) {
        return '#' + preferenceId;
    }

    /** Warning: uses default LML syntax. Will not work if you modified any LML markers.
     *
     * @param preferencesId name of the preferences object, as registered to LML data container.
     * @param preferenceId name of a preference in the specified i18n bundle.
     * @return converted LML preference argument using default syntax. */
    public static String toPreference(final String preferencesId, final String preferenceId) {
        return '#' + preferencesId + '.' + preferenceId;
    }

    /** Warning: uses default LML syntax. Will not work if you modified any LML markers.
     *
     * @param methodId name of a registered {@link ActorConsumer} or method name of a registered {@link ActionContainer}
     *            .
     * @return converted LML action argument using default syntax. */
    public static String toAction(final String methodId) {
        return '$' + methodId;
    }

    /** Warning: uses default LML syntax. Will not work if you modified any LML markers.
     *
     * @param actionContainerId name of an ActionContainer, as registered to LML data container.
     * @param methodId name of a method of an {@link ActionContainer} with the specified ID.
     * @return converted LML action argument using default syntax. */
    public static String toAction(final String actionContainerId, final String methodId) {
        return '$' + actionContainerId + '.' + methodId;
    }
}
