package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.actor.TableLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.toast.Toast;
import com.kotcrab.vis.ui.widget.toast.ToastTable;

/** Creates a {@link Toast} instance with a {@link ToastTable}. Treats children as a table tag.
 *
 * <p>
 * Note that due to how toasts are added to the stage, this actor should NOT be the root tag (if used to fill a stage or
 * view), should NOT be direct child of regular actors and should NOT be added directly to the stage. Instead, put it in
 * a "actorStorage" (or "isolate") tag, get a reference to it through its ID, and then add it to stage manually with a
 * {@link com.kotcrab.vis.ui.util.ToastManager}.
 *
 * <p>
 * Mapped to "toast".
 *
 * @author MJ */
public class ToastLmlTag extends TableLmlTag {
    public ToastLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        final ToastTable content = new ToastTable();
        final Toast toast = new Toast(builder.getStyleName(), content);
        content.setToast(toast);
        return content;
    }
}
