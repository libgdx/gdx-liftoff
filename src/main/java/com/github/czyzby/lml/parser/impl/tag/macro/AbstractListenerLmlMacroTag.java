package com.github.czyzby.lml.parser.impl.tag.macro;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.LmlParserListener;
import com.github.czyzby.lml.parser.impl.tag.AbstractMacroLmlTag;
import com.github.czyzby.lml.parser.impl.tag.macro.util.Equation;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** This base class for macros that create event listeners and attach them to their parent actor. When the event is
 * detected, content between macro tag is parsed with {@link LmlParser} and the result is added to the stage.
 *
 * @author MJ */
public abstract class AbstractListenerLmlMacroTag extends AbstractMacroLmlTag implements LmlParserListener {
    /** Represents the condition that has to be met before the selected template is parsed. */
    public static final String IF_ATTRIBUTE = "if";
    /** If this (optional) attribute is set to true, selected code snippet will be parsed once - the created actors will
     * be saved and added to the stage on each event occurrence. */
    public static final String CACHE_ATTRIBUTE = "cache";
    /** If this (optional) attribute is set to true, the managed listener will be attached to the actors with selected
     * IDs ({@link #IDS_ATTRIBUTE}) after every following template parsing. This allows to keep this macro's listener
     * and share it across multiple views. */
    public static final String KEEP_ATTRIBUTE = "keep";
    /** Optional attribute that might be used to attach this macro to additional actors. Represents an array of IDs of
     * actors that this listener should be attached to. */
    public static final String IDS_ATTRIBUTE = "ids";

    private Array<Actor> cachedActors;
    private String content;
    private boolean cacheActors;
    private String[] ids;
    private boolean keep = REMOVE;

    public AbstractListenerLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    public void handleDataBetweenTags(final CharSequence rawData) {
        setContent(rawData.toString());
    }

    @Override
    protected boolean supportsNamedAttributes() {
        return true;
    }

    @Override
    public void closeTag() {
        if (content == null) {
            getParser().throwErrorIfStrict("Listener macro should not be empty.");
            return;
        }
        final Actor actor = getActor();
        final boolean hasIdsAttribute = Strings.isNotWhitespace(getAttribute(IDS_ATTRIBUTE));
        if (actor == null) {
            if (!hasIdsAttribute) {
                getParser().throwErrorIfStrict(
                        "Listener macro can be attached only to valid actors. No valid actor parent tag and no non-empty 'attachTo' attribute was found.");
                return;
            }
        } else {
            attachListener(actor);
        }
        cacheActors = hasAttribute(CACHE_ATTRIBUTE) && getParser().parseBoolean(getAttribute(CACHE_ATTRIBUTE), actor);
        // Adding listener that attaches listener to tags after parsing:
        if (hasIdsAttribute) {
            ids = getParser().parseArray(getAttribute(IDS_ATTRIBUTE), actor);
            getParser().doAfterParsing(this);
        }
        setKeepListener(
                hasAttribute(KEEP_ATTRIBUTE) ? getParser().parseBoolean(getAttribute(KEEP_ATTRIBUTE), actor) : REMOVE);
    }

    /** @param actor should have the proper listener attached. The listener should invoke {@link #doOnEvent(Actor)} when
     *            the event occurs. */
    protected void attachListener(final Actor actor) {
        actor.addListener(getEventListener());
    }

    /** @param keep see {@link #KEEP_ATTRIBUTE}. */
    public void setKeepListener(final boolean keep) {
        this.keep = keep;
    }

    /** @return true if actors are cached. Defaults to false. Always returns false before the tag is closed. */
    protected boolean isCachingActors() {
        return cacheActors;
    }

    /** @param actor has the listener attached. */
    protected void doOnEvent(final Actor actor) {
        // Checking optional condition:
        if (hasAttribute(IF_ATTRIBUTE)) {
            final boolean condition = new Equation(getParser(), actor).getBooleanResult(getAttribute(IF_ATTRIBUTE));
            if (!condition) {
                return;
            }
        }
        if (cachedActors != null) { // Actors already parsed - returning cached values:
            addActors(actor, cachedActors);
        } else { // Template not parsed or actors are not being cached - parsing template:
            addActors(actor, parseSnippet());
        }
    }

    /** @param actor has the listener attached.
     * @param actors should be added to the stage.
     * @see #determineStage(Actor) */
    protected void addActors(final Actor actor, final Array<Actor> actors) {
        LmlUtilities.appendActorsToStage(determineStage(actor), actors);
    }

    /** @return current content between macro tags. */
    protected String getContent() {
        return content;
    }

    /** @param content will become current content between macro tags. */
    protected void setContent(final String content) {
        this.content = content;
    }

    /** @return an array of actors parsed from the content of the macro. Will be cached if {@link #isCachingActors()}
     *         returns true. */
    protected Array<Actor> parseSnippet() {
        final Array<Actor> actors = getParser().parseTemplate(content);
        if (isCachingActors()) {
            cachedActors = actors;
        }
        return actors;
    }

    /** Invoked after template parsing. Hooks up the listener to actors registered by {@link #IDS_ATTRIBUTE} attribute.
     *
     * @param parser parsed the template.
     * @param parsingResult parsed actors.
     * @return {@link LmlParserListener#REMOVE} by default. {@link #KEEP_ATTRIBUTE} value if it is set. */
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

    /** @return managed {@link EventListener} instance. */
    protected abstract EventListener getEventListener();

    /** @return instance of the managed {@link EventListener}. */
    @Override
    public Object getManagedObject() {
        return getEventListener();
    }

    @Override
    public String[] getExpectedAttributes() {
        return new String[] { IF_ATTRIBUTE, CACHE_ATTRIBUTE, KEEP_ATTRIBUTE, IDS_ATTRIBUTE };
    }
}
