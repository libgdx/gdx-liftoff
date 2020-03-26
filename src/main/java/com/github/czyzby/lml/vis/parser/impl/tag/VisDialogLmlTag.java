package com.github.czyzby.lml.vis.parser.impl.tag;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;
import com.github.czyzby.lml.vis.parser.impl.action.VisStageAttacher;
import com.github.czyzby.lml.vis.parser.impl.tag.builder.VisWindowLmlActorBuilder;
import com.github.czyzby.lml.vis.ui.reflected.ReflectedVisDialog;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisWindow;

/** Handles {@link VisDialog} actor. Works like a window, except it supports on result action and is attached to the
 * stage with {@link VisDialog#show(com.badlogic.gdx.scenes.scene2d.Stage)}. Mapped to "dialog", "visDialog".
 *
 * @author MJ */
public class VisDialogLmlTag extends VisWindowLmlTag {
    public VisDialogLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected VisWindow getNewInstanceOfVisWindow(final VisWindowLmlActorBuilder builder) {
        final VisDialog dialog = new ReflectedVisDialog(builder.getText(), builder.getStyleName());
        LmlUtilities.getLmlUserObject(dialog).setStageAttacher(new VisStageAttacher());
        return dialog;
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        final VisDialog dialog = getDialog();
        dialog.text(getParser().parseString(plainTextLine, dialog));
        if (LmlUtilities.isOneColumn(dialog.getContentTable())) {
            dialog.getContentTable().row();
        }
    }

    /** @return casted actor. */
    protected VisDialog getDialog() {
        return (VisDialog) getActor();
    }
}
