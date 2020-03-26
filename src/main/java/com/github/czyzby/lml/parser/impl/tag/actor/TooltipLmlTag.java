package com.github.czyzby.lml.parser.impl.tag.actor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.LmlParserListener;
import com.github.czyzby.lml.parser.impl.tag.builder.TooltipLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.scene2d.ui.reflected.TooltipTable;
import com.github.czyzby.lml.util.LmlParsingException;

/** Handles {@link Tooltip} listener represented by a {@link TooltipTable} actor. Handles and appends children like a
 * table - its tag can contain any table attributes, and its children's tag can have cell attributes and will be
 * properly handled. Attached to its parent tag above it. Mapped to "tooltip".
 *
 * @author MJ */
public class TooltipLmlTag extends TableLmlTag implements LmlParserListener {
    private Tooltip<TooltipTable> tooltip;

    public TooltipLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected TooltipLmlActorBuilder getNewInstanceOfBuilder() {
        return new TooltipLmlActorBuilder();
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        final TooltipManager manager = getTooltipManager((TooltipLmlActorBuilder) builder);
        final TooltipTable table = TooltipTable.create(getSkin(builder), manager);
        tooltip = table.getTooltip();
        return table;
    }

    /** @return {@link Tooltip} instance wrapped with the {@link TooltipTable}. */
    @Override
    public Object getManagedObject() {
        return tooltip;
    }

    /** @param builder contains tooltip building data.
     * @return an instance of tooltip manager with the ID selected by the builder or default tooltip manager.
     * @throws LmlParsingException if parser is strict and the ID is invalid. */
    protected TooltipManager getTooltipManager(final TooltipLmlActorBuilder builder) {
        TooltipManager manager = getParser().getData().getTooltipManager(builder.getTooltipManager());
        if (manager == null) {
            getParser().throwErrorIfStrict("Could not find tooltip manager for name: " + builder.getTooltipManager());
            manager = TooltipManager.getInstance();
        }
        return manager;
    }

    @Override
    public boolean isAttachable() {
        return true;
    }

    @Override
    public void attachTo(final LmlTag tag) {
        attachTooltip(tag.getActor());
    }

    /** @param actor will have the tooltip attached. */
    protected void attachTooltip(final Actor actor) {
        actor.addListener(tooltip);
    }

    /** @return managed instance of tooltip. */
    public Tooltip<TooltipTable> getTooltip() {
        return tooltip;
    }

    @Override
    protected void doOnTagClose() {
        super.doOnTagClose();
        final String[] ids = tooltip.getActor().getIds();
        if (ids != null && ids.length > 0) {
            getParser().doAfterParsing(this);
        }
    }

    @Override
    public boolean onEvent(final LmlParser parser, final Array<Actor> parsingResult) {
        final ObjectMap<String, Actor> actorsByIds = parser.getActorsMappedByIds();
        final boolean keep = tooltip.getActor().isKept();
        for (final String id : tooltip.getActor().getIds()) {
            final Actor actor = actorsByIds.get(id);
            if (actor != null) {
                attachTooltip(actor);
            } else if (!keep) {
                parser.throwErrorIfStrict("Unknown ID: '" + id + "'. Cannot attach tooltip.");
            }
        }
        return keep;
    }
}
