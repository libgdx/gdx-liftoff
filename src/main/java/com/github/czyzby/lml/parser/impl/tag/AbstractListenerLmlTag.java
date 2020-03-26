package com.github.czyzby.lml.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.LmlParserListener;
import com.github.czyzby.lml.parser.impl.tag.macro.util.Equation;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.scene2d.ui.reflected.ActorStorage;
import com.github.czyzby.lml.util.LmlUtilities;

/** Base for tags that attach a listener to actors. Exploits {@link ActorStorage} utility to store a list of actors
 * without putting them in a {@link com.badlogic.gdx.scenes.scene2d.Group Group}. By default, when the event occurs,
 * child actors of this tag will be added to the stage. Note that most of its attributes are ignored.
 *
 * @author MJ */
public abstract class AbstractListenerLmlTag extends AbstractActorLmlTag implements LmlParserListener {
    private String condition;
    private String[] ids;
    private boolean keep;

    public AbstractListenerLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        return new ActorStorage();
    }

    /** @return stored actor, casted to {@link ActorStorage} for convenience. */
    protected ActorStorage getActorStorage() {
        return (ActorStorage) getActor();
    }

    @Override
    public boolean isAttachable() {
        return true;
    }

    @Override
    protected void doOnTagClose() {
        if (getParent() == null) {
            getParser().throwErrorIfStrict(
                    "This tag should be attached to other actors. Listener tags produce mock-up actors and cannot be root tags.");
        }
        if (ids != null && ids.length > 0) {
            getParser().doAfterParsing(this);
        }
    }

    /** @param ids should not be null. Actors with these IDs will have the listener attached after template parsing. */
    public void setIds(final String[] ids) {
        this.ids = ids;
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        getActorStorage().addActor(toLabel(plainTextLine));
    }

    @Override
    protected void handleValidChild(final LmlTag childTag) {
        getActorStorage().addActor(childTag.getActor());
    }

    @Override
    public void attachTo(final LmlTag tag) {
        attachListener(tag.getActor());
    }

    /** @param condition LML equation snippet that will be processed each time the event occurs. If returns positive
     *            result, actors will be shown on the stage. Can be null (this is actually the default value) - if null,
     *            actors are displayed on each event. */
    public void setCondition(final String condition) {
        this.condition = condition;
    }

    /** @param keep if true and {@link #setIds(String[])} is used, this listener will be attached to every actor with
     *            the selected ID in every following parsed template. This setting allows to cache and reuse the same
     *            listener on multiple views. */
    public void setKeepListener(final boolean keep) {
        this.keep = keep;
    }

    /** @param actor should have a listener attached. The listener should call {@link #doOnEvent(Actor)} when the event
     *            occurs. */
    protected void attachListener(final Actor actor) {
        actor.addListener(getEventListener());
    }

    /** @return managed {@link EventListener} instance. */
    protected abstract EventListener getEventListener();

    /** @param actor has the listener attached. Its stage will be used to display stored actors. */
    protected void doOnEvent(final Actor actor) {
        if (condition != null) {
            final boolean shouldDisplay = new Equation(getParser(), actor).getBooleanResult(condition);
            if (!shouldDisplay) {
                return;
            }
        }
        LmlUtilities.appendActorsToStage(determineStage(actor), getActorStorage().getActors());
    }

    /** Invoked after template parsing. Hooks up the listener to actors registered by "attachTo" attribute.
     *
     * @param parser parsed the template.
     * @param parsingResult parsed actors.
     * @return {@link LmlParserListener#REMOVE} by default. See {@link #setKeepListener(boolean)}. */
    @Override
    public boolean onEvent(final LmlParser parser, final Array<Actor> parsingResult) {
        final ObjectMap<String, Actor> actorsByIds = parser.getActorsMappedByIds();
        for (final String id : ids) {
            final Actor actor = actorsByIds.get(id);
            if (actor != null) {
                attachListener(actor);
            } else if (!keep) {
                parser.throwErrorIfStrict("Unknown ID: '" + id + "'. Cannot attach listener.");
            }
        }
        return keep;
    }
}
