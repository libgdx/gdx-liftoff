package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractLmlTag;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;
import com.kotcrab.vis.ui.layout.DragPane;
import com.kotcrab.vis.ui.widget.Draggable;

/** Attaches {@link Draggable} listener to the parent tag's actor. Makes the actor draggable. Note that you can change
 * initial settings with static fields of {@link Draggable}, rather than including attributes in each tag. If draggable
 * is inside a {@link DragPaneLmlTag}, rather than being attached, it will be set as the drag pane's main listener with
 * {@link DragPane#setDraggable(Draggable)} and will be attached to all its children. Mapped to "drag", "draggable".
 *
 * @author MJ */
public class DraggableLmlTag extends AbstractLmlTag {
    private final Draggable draggable = new Draggable();

    public DraggableLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
        LmlUtilities.processAttributes(draggable, this, parser, null, true);
    }

    @Override
    public void handleDataBetweenTags(final CharSequence rawData) {
        if (Strings.isNotBlank(rawData)) {
            getParser().throwErrorIfStrict("Draggable cannot handle plain text. Found: " + rawData);
        }
    }

    @Override
    public Actor getActor() {
        return null;
    }

    /** @return managed {@link Draggable}. */
    @Override
    public Object getManagedObject() {
        return draggable;
    }

    @Override
    public void closeTag() {
    }

    @Override
    public void handleChild(final LmlTag childTag) {
        getParser().throwErrorIfStrict("Draggable cannot handle children. Found child tag: \"" + childTag.getTagName()
                + "\" with actor: " + childTag.getActor());
    }

    @Override
    protected boolean supportsNamedAttributes() {
        return true;
    }

    @Override
    public boolean isAttachable() {
        return true;
    }

    @Override
    public void attachTo(final LmlTag tag) {
        if (tag.getActor() instanceof DragPane) {
            ((DragPane) tag.getActor()).setDraggable(draggable);
        } else {
            draggable.attachTo(tag.getActor());
        }
    }
}
