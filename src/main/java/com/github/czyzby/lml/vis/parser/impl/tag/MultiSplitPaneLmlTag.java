package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractActorLmlTag;
import com.github.czyzby.lml.parser.impl.tag.builder.AlignedLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.MultiSplitPane;

/**
 * Handles {@link MultiSplitPane} widget. Can handle any children, converts text between tags to labels. Mapped to
 * "multiSplitPane".
 * @author MJ
 */
public class MultiSplitPaneLmlTag extends AbstractActorLmlTag {
    /**
     * {@link MultiSplitPane#addActor(Actor)} is unsupported: actors have to be gathered and set manually.
     */
    private final Array<Actor> children = GdxArrays.newArray(Actor.class);

    public MultiSplitPaneLmlTag(LmlParser parser, LmlTag parentTag, StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected LmlActorBuilder getNewInstanceOfBuilder() {
        return new AlignedLmlActorBuilder();
    }

    /**
     * @return stored actor casted for convenience.
     */
    public MultiSplitPane getMultiSplitPane() {
        return (MultiSplitPane) getActor();
    }

    @Override
    protected Actor getNewInstanceOfActor(LmlActorBuilder builder) {
        AlignedLmlActorBuilder alignedBuilder = (AlignedLmlActorBuilder) builder;
        return new MultiSplitPane(alignedBuilder.isVertical(), builder.getStyleName());
    }

    @Override
    protected void handleValidChild(LmlTag childTag) {
        children.add(childTag.getActor());
    }

    @Override
    protected void handlePlainTextLine(String plainTextLine) {
        children.add(toLabel(plainTextLine));
    }

    @Override
    protected void doOnTagClose() {
        getMultiSplitPane().setWidgets(children);
    }
}
