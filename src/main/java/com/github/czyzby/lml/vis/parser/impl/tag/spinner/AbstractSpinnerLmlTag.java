package com.github.czyzby.lml.vis.parser.impl.tag.spinner;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractActorLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;
import com.kotcrab.vis.ui.widget.spinner.Spinner;
import com.kotcrab.vis.ui.widget.spinner.SpinnerModel;

/** Abstract base for {@link Spinner} widgets with different {@link SpinnerModel} implementation.
 *
 * @author MJ */
public abstract class AbstractSpinnerLmlTag extends AbstractActorLmlTag {
    public AbstractSpinnerLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Spinner getNewInstanceOfActor(final LmlActorBuilder builder) {
        return new Spinner(builder.getStyleName(), Strings.EMPTY_STRING, createModel(builder));
    }

    /** @param builder used to build the widget, returned by {@link #getNewInstanceOfBuilder()}.
     * @return a new instance of {@link SpinnerModel}, handling spinner's values. */
    protected abstract SpinnerModel createModel(LmlActorBuilder builder);

    /** @return wrapped actor, casted for convenience. */
    protected Spinner getSpinner() {
        return (Spinner) getActor();
    }

    @Override
    protected void handleValidChild(final LmlTag childTag) {
        if (childTag.getActor() instanceof Label) {
            handlePlainTextLine(((Label) childTag.getActor()).getText().toString());
        } else {
            getParser().throwErrorIfStrict("Spinners cannot have children. Found child: " + childTag.getActor()
                    + " with tag: " + childTag.getTagName());
            LmlUtilities.getCell(childTag.getActor(), getSpinner());
        }
    }

    @Override
    protected boolean hasComponentActors() {
        return true;
    }

    @Override
    protected Actor[] getComponentActors(final Actor actor) {
        return new Actor[] { ((Spinner) actor).getTextField() };
    }
}
