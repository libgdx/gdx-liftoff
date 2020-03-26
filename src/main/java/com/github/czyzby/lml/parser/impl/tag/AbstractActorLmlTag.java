package com.github.czyzby.lml.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.ObjectSet;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.kiwi.util.gdx.collection.GdxSets;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.LmlSyntax;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlBuildingAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.scene2d.ui.reflected.GenericTreeNode;
import com.github.czyzby.lml.util.Lml;
import com.github.czyzby.lml.util.LmlUserObject;
import com.github.czyzby.lml.util.LmlUtilities;

/**
 * Common base class for all tags that spawn and manage an actor.
 * @author MJ
 */
public abstract class AbstractActorLmlTag extends AbstractLmlTag {
    private final Actor actor;

    public AbstractActorLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
        actor = prepareActor();
    }

    /**
     * Warning: invoked by the constructor.
     * @return a fully initiated instance of the actor, with its tag attributes processed.
     */
    protected Actor prepareActor() {
        final LmlActorBuilder builder = getNewInstanceOfBuilder();
        final ObjectSet<String> processedAttributes = GdxSets.newSet();
        addDefaultAttributes();
        processBuildingAttributes(builder, processedAttributes);
        final Actor actor;
        try {
            actor = getNewInstanceOfActor(builder);
        } catch (final Exception exception) {
            getParser().throwError("Unable to create a new instance of actor with tag: " + getTagName(), exception);
            return null;
        }
        builder.finishBuilding(actor);
        processTagAttributes(processedAttributes, actor, getManagedObject());
        invokeOnCreateActions(actor);
        return actor;
    }

    @Override
    protected boolean hasDefaultAttributes(final String tagName) {
        return GdxMaps.isNotEmpty(getParser().getStyleSheet().getStyles(tagName));
    }

    private void addDefaultAttributes() {
        final ObjectMap<String, String> defaultAttributes = getParser().getStyleSheet().getStyles(getTagName());
        final ObjectMap<String, String> namedAttributes = getNamedAttributes();
        if (defaultAttributes != null) {
            for (Entry<String, String> attribute : defaultAttributes) {
                if (!namedAttributes.containsKey(attribute.key)) {
                    namedAttributes.put(attribute.key, attribute.value);
                }
            }
        }
    }

    private void processBuildingAttributes(final LmlActorBuilder builder, final ObjectSet<String> processedAttributes) {
        if (getNamedAttributes() == null) {
            return;
        }
        final LmlSyntax syntax = getParser().getSyntax();
        for (final Entry<String, String> attribute : getNamedAttributes()) {
            // Processing building attributes:
            final LmlBuildingAttribute<LmlActorBuilder> buildingAttributeProcessor = syntax
                    .getBuildingAttributeProcessor(builder, attribute.key);
            if (buildingAttributeProcessor != null // This is the actual processing method:
                    && buildingAttributeProcessor.process(getParser(), this, builder, attribute.value)) {
                // If processing returns true, the attribute is fully parsed and can be omitted during attribute parsing
                // after the actor is initiated. If it returns false, it is expected that the attribute will be
                // eventually parsed by a second processor, after the widget is created.
                processedAttributes.add(attribute.key);
            }
        }
    }

    private void processTagAttributes(final ObjectSet<String> processedAttributes, final Actor actor,
            final Object managedObject) {
        if (hasComponentActors() && !Lml.DISABLE_COMPONENT_ACTORS_ATTRIBUTE_PARSING) {
            // Processing own attributes first, ignoring unknowns:
            LmlUtilities.processAttributes(actor, this, getParser(), processedAttributes, false);
            // Processing leftover attributes for component children:
            processComponentAttributes(processedAttributes, actor);
            // Processing leftover attributes, after the widget is fully constructed; not throwing errors for unknown
            // attributes. After we're done with components, we parse original attributes again for meaningful
            // exceptions - "attribute for Window not found" is a lot better than "attribute for Label not found", just
            // because we were parsing Label component of Window last. Continuing even for non-strict parser to ensure
            // the same parsing behavior.
        }
        boolean hasDistinctManagedObject = managedObject != null && managedObject != actor;
        if (hasDistinctManagedObject) {
            // Throws errors if actor is null:
            LmlUtilities.processAttributes(managedObject, this, getParser(), processedAttributes, actor == null);
        }
        if (actor != null) {
            // Processing own attributes. Throwing errors for the unknown attributes:
            LmlUtilities.processAttributes(actor, this, getParser(), processedAttributes, true);
        }
    }

    private void processComponentAttributes(final ObjectSet<String> processedAttributes, final Actor actor) {
        if (hasComponentActors()) {
            final Actor[] components = getComponentActors(actor);
            if (components == null || components.length == 0) {
                return;
            }
            for (final Actor component : components) {
                LmlUtilities.processAttributes(component, this, getParser(), processedAttributes, false);
            }
        }
    }

    /**
     * @return true if the widget consists of multiple widgets that should have their attributes parsed separately. If
     * this method returns true, {@link #getComponentActors(Actor)} cannot return null.
     */
    protected boolean hasComponentActors() {
        return false;
    }

    /**
     * @param actor instance of the actor. Component widgets should be extracted from this.
     * @return components that are used to create the widget. If {@link #hasComponentActors()} returns true, this method
     * cannot return false.
     */
    protected Actor[] getComponentActors(final Actor actor) {
        return null;
    }

    /**
     * @param actor will have its specialized user object extracted (if present) and will invoke all its referenced on
     * create actions.
     */
    protected void invokeOnCreateActions(final Actor actor) {
        final LmlUserObject userObject = LmlUtilities.getOptionalLmlUserObject(actor);
        if (userObject != null) {
            userObject.invokeOnCreateActions(actor);
        }
    }

    /**
     * @param actor will have its specialized user object extracted (if present) and will invoke all its referenced on
     * tag close actions.
     */
    protected void invokeOnCloseActions(final Actor actor) {
        final LmlUserObject userObject = LmlUtilities.getOptionalLmlUserObject(actor);
        if (userObject != null) {
            userObject.invokeOnCloseActions(actor);
        }
    }

    /**
     * @param builder fully initiated builder object with all building attributes already processed.
     * @return a new instance of handled actor.
     */
    protected abstract Actor getNewInstanceOfActor(LmlActorBuilder builder);

    /** @return specialized builder needed to construct the widget. */
    protected LmlActorBuilder getNewInstanceOfBuilder() {
        return new LmlActorBuilder();
    }

    @Override
    public Actor getActor() {
        return actor;
    }

    @Override
    public Object getManagedObject() {
        return actor;
    }

    @Override
    protected boolean supportsNamedAttributes() {
        return true;
    }

    @Override
    public final void handleDataBetweenTags(final CharSequence rawData) {
        if (Strings.isWhitespace(rawData)) {
            return;
        }
        final String[] lines = Strings.split(rawData, '\n');
        final Tree.Node node = LmlUtilities.getTreeNode(actor);
        if (node != null) {
            appendTreeNodes(lines, node);
            return;
        }
        for (String line : lines) {
            if (Strings.isNotWhitespace(line)) {
                line = line.trim();
                handlePlainTextLine(line);
            }
        }
    }

    private void appendTreeNodes(final String[] lines, final Tree.Node node) {
        for (String line : lines) {
            if (Strings.isNotWhitespace(line)) {
                line = line.trim();
                node.add(new GenericTreeNode(toLabel(line)));
            }
        }
    }

    /** @param plainTextLine trimmed line of data between tags. Is not empty. Should be handled by the tag. */
    protected abstract void handlePlainTextLine(String plainTextLine);

    /**
     * @param rawData unparsed LML data.
     * @return parsed LML data as a new label widget.
     */
    protected Label toLabel(final String rawData) {
        final LmlParser parser = getParser();
        return new Label(parser.parseString(rawData, actor), parser.getData().getDefaultSkin());
    }

    @Override
    public void handleChild(final LmlTag childTag) {
        if (childTag.isAttachable()) {
            // Child tag is an attachable object that can be appended to any widget, even if it is not prepared to
            // handle children. For example, tooltip tag can be nested inside a label and will be properly attached,
            // even though label wouldn't know how to handle a normal child, like a button or another label.
            if (actor != null) {
                childTag.attachTo(this);
            }
        } else if (childTag.getActor() != null) {
            final Tree.Node node = LmlUtilities.getTreeNode(actor);
            if (node != null) {
                // This actor is a tree node. Adding its child as a leaf.
                Tree.Node childNode = LmlUtilities.getTreeNode(childTag.getActor());
                if (childNode == null) {
                    childNode = new GenericTreeNode(childTag.getActor());
                }
                node.add(childNode);
            } else {
                handleValidChild(childTag);
            }
        }
    }

    /**
     * @param childTag is validated and fully initiated. Contains a non-null actor. Should be appended to the stored
     * widget.
     */
    protected abstract void handleValidChild(LmlTag childTag);

    @Override
    public final void closeTag() {
        doOnTagClose();
        closeComponentActors();
        invokeOnCloseActions(actor);
    }

    private void closeComponentActors() {
        if (hasComponentActors()) {
            for (final Actor component : getComponentActors(actor)) {
                invokeOnCloseActions(component);
            }
        }
    }

    /** Callback method, safe to override. Invoked by {@link #closeTag()} before on close actions are invoked. */
    protected void doOnTagClose() {
        // Most actors do nothing upon tag closing. This is reserved for the few widgets that might need additional
        // packing, preparing, etc.
    }
}
