package com.github.czyzby.lml.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Common base for tags that extend {@link Group} class without adding new actor appending methods (like Table or Tree
 * do). As long as the widget properly appends children with {@link Group#addActor(Actor)} and does not require any
 * additional method calls, this base can be used without overriding any methods. Plain text children will be converted
 * to labels.
 *
 * @author MJ */
public abstract class AbstractGroupLmlTag extends AbstractActorLmlTag {
    public AbstractGroupLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected final Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        return getNewInstanceOfGroup(builder);
    }

    /** @param builder used to build the widget.
     * @return a new instance of {@link Group} managed by the tag. */
    protected abstract Group getNewInstanceOfGroup(LmlActorBuilder builder);

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        addChild(toLabel(plainTextLine));
    }

    /** @return casted actor. */
    protected Group getGroup() {
        return (Group) getActor();
    }

    /** @param child will be appended to the managed {@link Group} widget. */
    protected void addChild(final Actor child) {
        getGroup().addActor(child);
    }

    @Override
    protected void handleValidChild(final LmlTag childTag) {
        addChild(childTag.getActor());
    }
}
