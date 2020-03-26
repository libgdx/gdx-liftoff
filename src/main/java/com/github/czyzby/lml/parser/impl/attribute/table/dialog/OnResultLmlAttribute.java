package com.github.czyzby.lml.parser.impl.attribute.table.dialog;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Attaches an action to the actor. Expects an action ID. Works only if the actor is a child of a dialog (does not have
 * to be a direct child - just needs it in its hierarchy). Actors with this attribute are automatically added to the
 * dialog's button table by an initial building processor. If the referenced action returns a true boolean, dialog
 * hiding will be cancelled. Will look for an action that consumes the {@link Dialog} rather than the widget that
 * contains the attribute: keep that in mind when attaching methods. Mapped to "result", "onResult".
 *
 * @author MJ
 * @see com.github.czyzby.lml.parser.impl.attribute.building.OnResultInitialLmlAttribute
 * @see com.github.czyzby.lml.scene2d.ui.reflected.ReflectedLmlDialog#CANCEL_HIDING */
public class OnResultLmlAttribute implements LmlAttribute<Actor> {
    @Override
    public Class<Actor> getHandledType() {
        return Actor.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final String rawAttributeData) {
        final Dialog dialog = getDialogParent(tag);
        if (dialog == null) {
            parser.throwErrorIfStrict(
                    "On result actions can be attached only to children of dialogs. Received on result action: "
                            + rawAttributeData + " on a tag without dialog parent: " + tag.getTagName());
            return;
        }
        dialog.setObject(actor, parser.parseAction(rawAttributeData, dialog));
    }

    private static Dialog getDialogParent(final LmlTag tag) {
        LmlTag parent = tag.getParent();
        while (parent != null) {
            if (parent.getActor() instanceof Dialog) {
                return (Dialog) parent.getActor();
            }
            parent = parent.getParent();
        }
        return null;
    }
}
