package com.github.czyzby.lml.scene2d.ui.reflected;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.github.czyzby.lml.parser.action.ActorConsumer;

/** If this dialog receives an {@link ActorConsumer} in {@link #result(Object)} method, it will invoke it; if a boolean
 * (primitive or wrapped) is returned by the actor consumer invocation and its value is true, dialog hiding will be
 * cancelled.
 *
 * Made as GWT utility for LML. Anonymous class was, obviously, not available for GWT's reflection mechanism, which
 * caused problems.
 *
 * @author MJ */
public class ReflectedLmlDialog extends Dialog {
    /** If returned by methods that are attached as on result actions, can be used cancel or force hiding of the dialog.
     * Use for code clarity. */
    public static final boolean CANCEL_HIDING = true, HIDE = false;

    public ReflectedLmlDialog(final String title, final Skin skin, final String windowStyleName) {
        super(title, skin, windowStyleName);
        getContentTable().setSkin(skin);
        getButtonTable().setSkin(skin);
        setDefaultCellPreferences();
    }

    private void setDefaultCellPreferences() {
        final Cell<?> contentCell = getCell(getContentTable());
        contentCell.fill();
        contentCell.expand();
        final Cell<?> buttonCell = getCell(getButtonTable());
        buttonCell.fillX();
        buttonCell.expandX();
        row();
    }

    @Override
    protected void result(final Object object) {
        if (object instanceof ActorConsumer<?, ?>) {
            @SuppressWarnings("unchecked") final Object result = ((ActorConsumer<?, Object>) object).consume(this);
            if (result instanceof Boolean && ((Boolean) result).booleanValue()) {
                cancel();
            }
        }
    }
}