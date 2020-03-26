package com.github.czyzby.lml.parser.impl.tag.actor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractActorLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Handles {@link Container} actor. Can manage a single child - works as a simplified Table. Converts plain text
 * between tags to a label and adds it as its child. Mapped to "container".
 *
 * @author MJ */
public class ContainerLmlTag extends AbstractActorLmlTag {
    public ContainerLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        return new Container<Actor>();
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        addChild(toLabel(plainTextLine));
    }

    @Override
    protected void handleValidChild(final LmlTag childTag) {
        addChild(childTag.getActor());
    }

    /** @param child will be set as container's child. */
    protected void addChild(final Actor child) {
        final Container<Actor> container = getContainer();
        if (container.getActor() != null) {
            getParser().throwErrorIfStrict("Container widget can manage only one child.");
        }
        container.setActor(child);
    }

    /** @return casted actor. */
    @SuppressWarnings("unchecked")
    protected Container<Actor> getContainer() {
        return (Container<Actor>) getActor();
    }
}
