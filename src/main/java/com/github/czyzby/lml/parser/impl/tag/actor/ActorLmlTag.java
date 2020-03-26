package com.github.czyzby.lml.parser.impl.tag.actor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractActorLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Simple actor tag parser. If the tag is parental, {@link Group} is created instead of {@code Actor} to handle
 * children tags and plain text. This widget is advised to be used only as a child when, for example, a cell in a table
 * needs to be filled with a mock-up actor. Text between tags is added to the group as a label. Mapped to "actor",
 * "group".
 *
 * @author MJ */
public class ActorLmlTag extends AbstractActorLmlTag {
    public ActorLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        return isParent() ? new Group() : new Actor();
    }

    @Override
    protected void handleValidChild(final LmlTag childTag) {
        validateActor();
        ((Group) getActor()).addActor(childTag.getActor());
    }

    private void validateActor() {
        if (!(getActor() instanceof Group)) {
            getParser().throwError("Fatal. Simple actor child tag used as a parent. Invalid parser implementation.");
        }
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        validateActor();
        ((Group) getActor()).addActor(toLabel(plainTextLine));
    }
}
