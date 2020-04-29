package com.github.czyzby.lml.parser.impl.tag.actor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractActorLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Handles {@link ScrollPane} actor. Can have a single child tag which will be set as scroll pane's managed widget.
 * Parses plain text between tags to a label and sets it as its managed widget. Mapped to "scrollPane".
 *
 * @author MJ */
public class ScrollPaneLmlTag extends AbstractActorLmlTag {
    public ScrollPaneLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        ScrollPane scrollPane = new ScrollPane(null, getSkin(builder), builder.getStyleName());
        scrollPane.addListener(new InputListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                scrollPane.getStage().setScrollFocus(event.getListenerActor());
            }
        });
        return scrollPane;
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        setChild(toLabel(plainTextLine));
    }

    /** @param child will be set as the managed child. */
    protected void setChild(final Actor child) {
        final ScrollPane scrollPane = getScrollPane();
        if (scrollPane.getActor() != null) {
            getParser().throwErrorIfStrict("Scroll pane can have only one child. Received another child: " + child);
        }
        scrollPane.setActor(child);
    }

    /** @return casted actor. */
    protected ScrollPane getScrollPane() {
        return (ScrollPane) getActor();
    }

    @Override
    protected void handleValidChild(final LmlTag childTag) {
        setChild(childTag.getActor());
    }
}
