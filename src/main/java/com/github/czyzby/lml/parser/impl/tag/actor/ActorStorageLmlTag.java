package com.github.czyzby.lml.parser.impl.tag.actor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractActorLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.scene2d.ui.reflected.ActorStorage;

/** This is a utility tag that handles {@link ActorStorage}. Children of this tag are properly parsed and created, but
 * are never immediately added to the stage. This is very convenient for parsing actors that should be added to the
 * stage after a delay (like dialogs): you can parse them along with the rest of the template, find their reference
 * (thanks to a specific ID) and manually add them later in Java. {@link ActorStorage} has additional method for
 * accessing and managing the internally stored array of actors.
 *
 * <p>
 * Note that the {@link ActorStorage} should be be a root tag - when it is a child of another tag, it will simply be
 * ignored and never added to the stage. However, if it is a root tag, it will be unnecessarily added to the stage and
 * immediately remove itself after first act.
 *
 * <p>
 * Mapped to "actorStorage", "isolate".
 *
 * @author MJ */
public class ActorStorageLmlTag extends AbstractActorLmlTag {
    public ActorStorageLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        return new ActorStorage();
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        storeActor(toLabel(plainTextLine));
    }

    @Override
    protected void handleValidChild(final LmlTag childTag) {
        storeActor(childTag.getActor());
    }

    /** @param actor will be added to the storage. See {@link ActorStorage#addActor(Actor)}. */
    protected void storeActor(final Actor actor) {
        ((ActorStorage) getActor()).addActor(actor);
    }

    @Override
    protected void doOnTagClose() {
        getActor().addAction(Actions.removeActor());
    }

    @Override
    public boolean isAttachable() {
        return true;
    }

    @Override
    public void attachTo(final LmlTag tag) {
        // Does nothing. Is never added to stage.
    }
}