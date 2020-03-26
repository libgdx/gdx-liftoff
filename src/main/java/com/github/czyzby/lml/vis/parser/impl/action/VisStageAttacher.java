package com.github.czyzby.lml.vis.parser.impl.action;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.github.czyzby.lml.parser.impl.action.DefaultStageAttacher;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisWindow;

/** Expands default stage attacher functionality with the ability to attach {@link VisDialog}s and {@link VisWindow}s.
 *
 * @author MJ */
public class VisStageAttacher extends DefaultStageAttacher {
    @Override
    public void attachToStage(final Actor actor, final Stage stage) {
        if (actor instanceof VisDialog) {
            ((VisDialog) actor).show(stage);
        } else if (actor instanceof VisWindow) {
            stage.addActor(((VisWindow) actor).fadeIn());
        }
        super.attachToStage(actor, stage);
    }

    /** A specialized {@link ClickListener} which shows a {@link VisWindow} each time a non-disabled actor is clicked.
     *
     * @author MJ */
    public static class PopupAttacher extends ClickListener {
        private final VisWindow visWindow;
        private final Actor actor;

        public PopupAttacher(final VisWindow visWindow, final Actor actor) {
            this.visWindow = visWindow;
            this.actor = actor;
        }

        @Override
        public void clicked(final InputEvent event, final float x, final float y) {
            if (actor instanceof Disableable && ((Disableable) actor).isDisabled()) {
                return;
            }
            actor.getStage().addActor(visWindow.fadeIn());
        }
    }
}
