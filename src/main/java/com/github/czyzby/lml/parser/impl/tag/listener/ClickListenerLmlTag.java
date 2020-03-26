package com.github.czyzby.lml.parser.impl.tag.listener;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractListenerLmlTag;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Attachable tag. Can be a child of any tag and can have any actor tag children. Adds a {@link ClickListener} to the
 * parent actor. Each time the parent is clicked, actors that are children of listener tag will be added to the stage.
 * Useful for "delayed" adding of actors - for example, one can easily define a dialog that will be shown each time a
 * certain actor is clicked. Note that is not simply an equivalent of
 * {@link com.github.czyzby.lml.parser.impl.tag.macro.ClickListenerLmlMacroTag click listener macro}: macro parses
 * template between its tags after event occurs (so actor creation is delayed). When using this tag, actors are created
 * along the rest of the template - they are simply not added to the stage immediately, but otherwise they are parsed
 * like any other actors and can access local variables.
 *
 * <p>
 * Mapped to "onClick", "clickListener".
 *
 * @author MJ
 * @see #setCondition(String) */
public class ClickListenerLmlTag extends AbstractListenerLmlTag {
    private final ClickListener listener = new ClickListener() {
        @Override
        public void clicked(final InputEvent event, final float x, final float y) {
            doOnEvent(event.getListenerActor());
        }
    };

    public ClickListenerLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected ClickListener getEventListener() {
        return listener;
    }
}
