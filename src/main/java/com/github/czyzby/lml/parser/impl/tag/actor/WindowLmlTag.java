package com.github.czyzby.lml.parser.impl.tag.actor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** Handles {@link Window} actor. By default, if the window is a root (has no parent), it is attached to the center of
 * its stage. Handles its children like a table. Mapped to "window".
 *
 * @author MJ */
public class WindowLmlTag extends TableLmlTag {
    public WindowLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected TextLmlActorBuilder getNewInstanceOfBuilder() {
        return new TextLmlActorBuilder();
    }

    @Override
    protected final Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        final Window window = getNewInstanceOfWindow((TextLmlActorBuilder) builder);
        LmlUtilities.getLmlUserObject(window).initiateStageAttacher(); // Centers the window by default.
        return window;
    }

    /** @param builder contains data necessary to constuct a window.
     * @return a new instance of Window actor. */
    protected Window getNewInstanceOfWindow(final TextLmlActorBuilder builder) {
        return new Window(builder.getText(), getSkin(builder), builder.getStyleName());
    }

    @Override
    protected boolean hasComponentActors() {
        return true;
    }

    @Override
    protected Actor[] getComponentActors(final Actor actor) {
        return new Actor[] { ((Window) actor).getTitleLabel() };
    }
}
