package com.github.czyzby.lml.parser.impl.tag.actor;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.scene2d.ui.reflected.ReflectedLmlDialog;
import com.github.czyzby.lml.util.LmlUtilities;

/** Handles {@link Dialog} actor. Works like a window, except it supports on result action and is attached to the stage
 * with {@link Dialog#show(com.badlogic.gdx.scenes.scene2d.Stage)}. Mapped to "dialog".
 *
 * @author MJ */
public class DialogLmlTag extends WindowLmlTag {
    public DialogLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Dialog getNewInstanceOfWindow(final TextLmlActorBuilder builder) {
        return new ReflectedLmlDialog(builder.getText(), getSkin(builder), builder.getStyleName());
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        final Dialog dialog = getDialog();
        dialog.text(getParser().parseString(plainTextLine, dialog));
        if (LmlUtilities.isOneColumn(dialog.getContentTable())) {
            dialog.getContentTable().row();
        }
    }

    /** @return casted actor. */
    protected Dialog getDialog() {
        return (Dialog) getActor();
    }
}
