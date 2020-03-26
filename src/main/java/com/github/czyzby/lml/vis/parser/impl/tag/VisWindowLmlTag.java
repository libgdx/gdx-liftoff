package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.actor.WindowLmlTag;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.parser.impl.tag.builder.VisWindowLmlActorBuilder;
import com.kotcrab.vis.ui.widget.VisWindow;

/** Handles {@link VisWindow} actors. Processes children like a regular window tag. Mapped to "window", "visWindow".
 *
 * @author MJ */
public class VisWindowLmlTag extends WindowLmlTag {
    public VisWindowLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected TextLmlActorBuilder getNewInstanceOfBuilder() {
        return new VisWindowLmlActorBuilder();
    }

    @Override
    protected final Window getNewInstanceOfWindow(final TextLmlActorBuilder builder) {
        return getNewInstanceOfVisWindow((VisWindowLmlActorBuilder) builder);
    }

    /** @param builder contains data necessary to build {@link VisWindow}.
     * @return a new instance of {@link VisWindow}. */
    protected VisWindow getNewInstanceOfVisWindow(final VisWindowLmlActorBuilder builder) {
        final VisWindow window = new VisWindow(builder.getText(),
                getSkin(builder).get(builder.getStyleName(), WindowStyle.class));
        window.setSkin(getSkin(builder));
        return window;
    }
}
