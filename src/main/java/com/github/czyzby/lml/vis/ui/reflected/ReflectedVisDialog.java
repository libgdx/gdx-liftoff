package com.github.czyzby.lml.vis.ui.reflected;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisDialog;

/** If this dialog receives an {@link ActorConsumer} in {@link #result(Object)} method, it will invoke it; if a boolean
 * (primitive or wrapped) is returned by the actor consumer invocation and its value is true, dialog hiding will be
 * cancelled.
 *
 * Made as GWT utility for LML. Anonymous class was, obviously, not available for GWT's reflection mechanism, which
 * caused problems.
 *
 * @author MJ */
public class ReflectedVisDialog extends VisDialog {
    /** If returned by methods that are attached as on result actions, can be used cancel or force hiding of the dialog.
     * Use for code clarity. */
    public static final boolean CANCEL_HIDING = true, HIDE = false;

    public ReflectedVisDialog(final String title, final String styleName) {
        super(title, styleName);
        assignTableSkins();
        setDefaultCellPreferences();
    }

    private void assignTableSkins() {
        final Skin skin = VisUI.getSkin();
        setSkin(skin);
        getContentTable().setSkin(skin);
        getButtonsTable().setSkin(skin);
    }

    private void setDefaultCellPreferences() {
        final Cell<?> contentCell = getCell(getContentTable());
        contentCell.fill();
        contentCell.expand();
        final Cell<?> buttonCell = getCell(getButtonsTable());
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
