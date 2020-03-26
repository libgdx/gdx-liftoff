package com.github.czyzby.lml.parser.impl.tag.macro;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.impl.tag.AbstractActorLmlTag;
import com.github.czyzby.lml.parser.impl.tag.AbstractMacroLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;
import com.github.czyzby.lml.scene2d.ui.reflected.GenericTreeNode;
import com.github.czyzby.lml.util.LmlUtilities;

/** Allows to register new tags from within LML templates. Normally, you have to implement {@code LmlTag} interface
 * (there are good abstracts for that - {@link AbstractMacroLmlTag} and {@link AbstractActorLmlTag}, but still) and add
 * a {@link LmlTagProvider} to syntax object - all in Java. This macro allows you to use LML templates to register new
 * tags; this is much less flexible solution, but a lot quicker, if you want to override a method or two in the original
 * class. For example, let's say you want a Table that does some extra work in the constructor:
 *
 * <blockquote>
 *
 * <pre>
 * public Table getMyTable() {
 *     return new Table() {
 *         {
 *             columnDefaults(1).pad(4f);
 *         }
 *     };
 * }
 * </pre>
 *
 * </blockquote>If you want to use this specialized Table method as a provider for a new tag, all you have to do is use
 * this macro:
 *
 * <blockquote>
 *
 * <pre>
 * &lt;:newTag myTable getMyTable /&gt;
 * &lt;!-- Now you can use: --&gt;
 * &lt;myTable&gt;
 *      &lt;label pad=3&gt;Will be properly added.&lt;/label&gt;
 * &lt;/myTable&gt;
 * </pre>
 *
 * </blockquote>The first argument is an array of tag names (in this case: "myTable" tag name). The second is the method
 * reference that returns the actor instance that you want to assign to the tag names. If the actor extends
 * {@link Group}, {@link Table} or {@link Tree}, it will append children and (text converted to labels) with the most
 * appropriate method. Actor's attributes will be properly handled: if the actor extends a Table, for example, it will
 * be able to parse all Table attributes and its children can have any Cell attributes, as expected.
 *
 * <p>
 * But there are times when you need more data to create the widget, like its style. That's why you can create a method
 * that consumes {@link LmlActorBuilder}: <blockquote>
 *
 * <pre>
 * public Table getMyTable(LmlActorBuilder builder) {
 *     return new Table(lmlParser.getData().getSkin(builder.getSkinName()) {
 *         {
 *             columnDefaults(1).pad(4f);
 *         }
 *     };
 * }
 * </pre>
 *
 * </blockquote>You do need a reference to your LmlParser if you want to have multiple skins support, but this should
 * not be an issue. If you need a different builder (one with more data and more assigned attributes - be careful
 * though, as you might need to register some building attributes), you can pass a third macro argument: builder
 * provider method.
 *
 * <blockquote>
 *
 * <pre>
 * public LmlActorBuilder getMyBuilder() {
 *     return new TextLmlActorBuilder();
 * }
 *
 * public Table getMyTable(LmlActorBuilder builder) {
 *     return new Table(lmlParser.getData().getSkin(builder.getSkinName()) {
 *         {
 *             add(((TextLmlActorBuilder) builder).getText();
 *             columnDefaults(1).pad(4f);
 *         }
 *     };
 * }
 *
 * &lt;!-- In template: --&gt;
 * &lt;:newTag myTable;myAlias getMyTable getMyBuilder /&gt;
 * </pre>
 *
 * </blockquote>Building attributes are mapped to builder types, so if you use one of custom widget builders (like the
 * text actor builder in the example above), your tag will automatically handle all its attributes.
 *
 * <p>
 * Note that this macro supports named attributes: <blockquote>
 *
 * <pre>
 * &lt;:newTag alias="myTable" method="getMyTable" builder="getMyBuilder" /&gt;
 * </pre>
 *
 * </blockquote>
 *
 * @author MJ */
public class NewTagLmlMacroTag extends AbstractMacroLmlTag {
    /** Alias for the first macro attribute: list of tag aliases. */
    public static final String ALIAS_ATTRIBUTE = "alias";
    /** Alias for the second macro attribute: name of the method that returns an actor instance and optionally consumes
     * a {@link LmlActorBuilder}. */
    public static final String METHOD_ATTRIBUTE = "method";
    /** Alias for the third macro attribute: name of the method that returns a customized {@link LmlActorBuilder}. */
    public static final String BUILDER_ATTRIBUTE = "builder";

    public NewTagLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    public void handleDataBetweenTags(final CharSequence rawData) {
        if (Strings.isNotWhitespace(rawData)) {
            getParser().throwErrorIfStrict("New tag macro cannot parse content between tags.");
        }
    }

    @Override
    public void closeTag() {
        final Array<String> attributes = getAttributes();
        if (GdxArrays.sizeOf(attributes) < 2) {
            getParser().throwError(
                    "Cannot register a new tag without two attributes: tag names array and method ID (consuming LmlActorBuilder, providing Actor).");
        }
        // Possible tag names:
        final String[] tagNames = getTagNames();
        // Creates actual actor:
        final ActorConsumer<Actor, LmlActorBuilder> creator = getActorCreator();
        final ActorConsumer<LmlActorBuilder, ?> builderCreator;
        if (attributes.size > 2) { // Provides builders:
            builderCreator = getBuilderCreator();
        } else { // Using default builders:
            builderCreator = null;
        }
        if (creator == null) { // No actor creation method - new tag cannot be created.
            getParser().throwError(
                    "Cannot register a method consuming LmlActorBuilder, providing Actor. Method consuming LmlActorBuilder and returning actor not found for attribute: "
                            + attributes.get(1));

        }
        // Registering provider that will create custom tags for the selected tag names:
        getParser().getSyntax().addTagProvider(getNewTagProvider(creator, builderCreator), tagNames);
    }

    /** @return {@link ActorConsumer} returning {@link LmlActorBuilder}. */
    @SuppressWarnings("unchecked")
    private ActorConsumer<LmlActorBuilder, ?> getBuilderCreator() {
        if (hasAttribute(BUILDER_ATTRIBUTE)) {
            return (ActorConsumer<LmlActorBuilder, ?>) getParser().parseAction(getAttribute(BUILDER_ATTRIBUTE),
                    getActor());
        }
        return (ActorConsumer<LmlActorBuilder, ?>) getParser().parseAction(getAttributes().get(2), getActor());
    }

    /** @return {@link ActorConsumer} returning an {@link Actor} and (optionally) consuming {@link LmlActorBuilder}. */
    @SuppressWarnings("unchecked")
    protected ActorConsumer<Actor, LmlActorBuilder> getActorCreator() {
        if (hasAttribute(METHOD_ATTRIBUTE)) {
            return (ActorConsumer<Actor, LmlActorBuilder>) getParser().parseAction(getAttribute(METHOD_ATTRIBUTE),
                    new LmlActorBuilder());
        }
        return (ActorConsumer<Actor, LmlActorBuilder>) getParser().parseAction(getAttributes().get(1),
                new LmlActorBuilder());
    }

    /** @return list of tag aliases. */
    protected String[] getTagNames() {
        if (hasAttribute(ALIAS_ATTRIBUTE)) {
            return getParser().parseArray(getAttribute(ALIAS_ATTRIBUTE), getActor());
        } else if (GdxMaps.isNotEmpty(getNamedAttributes())) {
            getParser().throwError(
                    "When using named attributes, new tag macro needs at least two attributes: 'method' (name of the method that returns an Actor and optionally consumes LmlActorBuilder) and 'tag' (array of new tag aliases). Found attributes: "
                            + getNamedAttributes());
        }
        return getParser().parseArray(getAttributes().first(), getActor());
    }

    @Override
    public String[] getExpectedAttributes() {
        return new String[] { ALIAS_ATTRIBUTE, METHOD_ATTRIBUTE, BUILDER_ATTRIBUTE };
    }

    /** @param creator method that spawns new actors.
     * @param builderCreator spawns actor builders. Optional.
     * @return an instance of {@link LmlTagProvider} that provides custom tags. */
    protected LmlTagProvider getNewTagProvider(final ActorConsumer<Actor, LmlActorBuilder> creator,
            final ActorConsumer<LmlActorBuilder, ?> builderCreator) {
        return new LmlTagProvider() {
            @Override
            public LmlTag create(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
                return new CustomLmlTag(parser, parentTag, rawTagData) {
                    @Override
                    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
                        return creator.consume(builder);
                        // This is an abstract method, because we cannot pass the creator in the constructor. The actor
                        // is created IN the super constructor (so it can be final), before creator is even assigned.
                    }

                    @Override
                    protected LmlActorBuilder getNewInstanceOfBuilder() {
                        if (builderCreator != null) {
                            return builderCreator.consume(null);
                        }
                        return super.getNewInstanceOfBuilder();
                    }
                };
            }
        };
    }

    /** Custom tag created with new tag macro. If the returned actor extends {@link Group}, it can be parental: plain
     * text will be converted to labels and added; regular tags will be added with {@link Group#addActor(Actor)}. If the
     * actor implements {@link Layout}, it will be packed after its tag is closed.
     *
     * @author MJ */
    public static abstract class CustomLmlTag extends AbstractActorLmlTag {
        public CustomLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
            super(parser, parentTag, rawTagData);
        }

        @Override
        protected void handlePlainTextLine(final String plainTextLine) {
            if (getActor() instanceof Label) {
                // Labels might be a pretty basic widget that sometimes needs extension, so we want to support its
                // unique text parsing.
                appendText((Label) getActor(), plainTextLine);
            } else if (getActor() instanceof Group) {
                addChild(toLabel(plainTextLine));
            }
        }

        /** @param actor casted for convenience.
         * @param plainTextLine should be appended to label. */
        protected void appendText(final Label actor, final String plainTextLine) {
            final String textToAppend = getParser().parseString(plainTextLine, actor);
            if (Strings.isEmpty(actor.getText())) {
                actor.setText(textToAppend);
            } else {
                if (LmlUtilities.isMultiline(actor)) {
                    actor.getText().append('\n');
                }
                actor.getText().append(textToAppend);
            }
            actor.invalidate();
        }

        /** @param child will be added to the actor casted to a Group or a Table. */
        protected void addChild(final Actor child) {
            final Actor actor = getActor();
            if (actor instanceof Tree) {
                final Tree.Node node = LmlUtilities.getTreeNode(child);
                if (node != null) {
                    ((Tree) actor).add(node);
                } else {
                    ((Tree) actor).add(new GenericTreeNode(child));
                }
            } else if (actor instanceof Table) {
                LmlUtilities.getCell(child, (Table) actor);
            } else {
                ((Group) actor).addActor(child);
            }
        }

        @Override
        protected void handleValidChild(final LmlTag childTag) {
            if (getActor() instanceof Group) {
                addChild(childTag.getActor());
            }
        }
    }
}
