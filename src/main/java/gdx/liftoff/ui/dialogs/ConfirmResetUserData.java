package gdx.liftoff.ui.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.ray3k.stripe.PopTable;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static gdx.liftoff.Main.*;
import static gdx.liftoff.ui.dialogs.FullscreenCompleteDialog.*;
import static gdx.liftoff.ui.dialogs.FullscreenDialog.*;

public class ConfirmResetUserData extends PopTable {
    public ConfirmResetUserData() {
        setStyle(skin.get("dialog", WindowStyle.class));
        setKeepCenteredInWindow(true);
        setHideOnUnfocus(true);
        pad(SPACE_LARGE);

        defaults().space(SPACE_LARGE);
        Label label = new Label(prop.getProperty("resetConfirmation"), skin);
        label.setWrap(true);
        add(label).growX();

        row();
        Table table = new Table();
        add(table);

        table.defaults().space(SPACE_HUGE);
        TextButton resetButton = new TextButton(prop.getProperty("resetButton"), skin);
        table.add(resetButton).uniformX().fillX();
        addHandListener(resetButton);
        onChange(resetButton, this::resetConfirmed);
        key(Keys.ENTER, this::resetConfirmed);

        TextButton cancelButton = new TextButton(prop.getProperty("quickCancel"), skin);
        table.add(cancelButton).uniformX().fillX();
        addHandListener(cancelButton);
        onChange(cancelButton, this::hide);
        key(Keys.ESCAPE, this::hide);
    }

    private void resetConfirmed() {
        hide();
        resetUserData();

        if (fullscreenDialog != null) {
            Action action = sequence(alpha(0), Actions.run(() -> fullscreenDialog.populate()), fadeIn(.2f));
            fullscreenDialog.addAction(action);
        } else if (fullscreenCompleteDialog != null) {
            fullscreenCompleteDialog.hide();
            FullscreenDialog.show();
        } else if (root.getCurrentTable() == root.completeTable) {
            root.transitionTable(root.landingTable,true);
        } else {
            root.getCurrentTable().populate();
            root.getCurrentTable().setColor(1, 1, 1, 0);
            Gdx.app.postRunnable(() -> root.fadeInTable());
        }
    }

    public static void showDialog() {
        ConfirmResetUserData pop = new ConfirmResetUserData();
        pop.show(stage);
    }
}
