package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractActorLmlTag;
import com.github.czyzby.lml.parser.impl.tag.builder.AlignedLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.VisSplitPane;

/** Handles {@link VisSplitPane} actor. Can manage two children - they will be set as the first and second managed
 * widgets in order in which they appeared in the template. Converts plain text between its tags to a label and assumes
 * it is one of its actors to manage. Mapped to "splitPane", "visSplitPane".
 *
 * @author MJ */
public class VisSplitPaneLmlTag extends AbstractActorLmlTag {
    private int appendedActors;

    public VisSplitPaneLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected AlignedLmlActorBuilder getNewInstanceOfBuilder() {
        return new AlignedLmlActorBuilder();
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        final AlignedLmlActorBuilder alignedBuilder = (AlignedLmlActorBuilder) builder;
        return new VisSplitPane(null, null, alignedBuilder.isVertical(), alignedBuilder.getStyleName());
    }

    @Override
    protected void handleValidChild(final LmlTag childTag) {
        setSplitPaneChild(childTag.getActor());
    }

    /** @param child will be set as the first or the second widget, according to how many widgets were added before. */
    protected void setSplitPaneChild(final Actor child) {
        if (appendedActors == 0) {
            getSplitPane().setFirstWidget(child);
        } else if (appendedActors == 1) {
            getSplitPane().setSecondWidget(child);
        } else {
            getParser().throwErrorIfStrict("Split pane can have only 2 children.");
            appendedActors = 0; // If not strict and for some reason we got here, let's pretend nothing happened.
            setSplitPaneChild(child);
        }
        appendedActors++;
        if (!getParser().isStrict()) {
            // If parser is not strict, more than 2 children can be appended. They will just override previous children.
            appendedActors %= 2;
        }
    }

    /** @return casted widget. */
    protected VisSplitPane getSplitPane() {
        return (VisSplitPane) getActor();
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        setSplitPaneChild(toLabel(plainTextLine));
    }
}
