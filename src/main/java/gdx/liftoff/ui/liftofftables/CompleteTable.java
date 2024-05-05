package gdx.liftoff.ui.liftofftables;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import gdx.liftoff.ui.panels.CompleteButtonsPanel;
import gdx.liftoff.ui.panels.CompletePanel;
import gdx.liftoff.ui.panels.GeneratingPanel;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static gdx.liftoff.Main.*;

/**
 * The final table in the workflow. An animation begins by showing the generating panel first. Then it displays the
 * complete and complete buttons panels.
 */
public class CompleteTable extends LiftoffTable {
    public CompleteTable() {
        populate();
    }

    @Override
    public void populate() {
        clearChildren();
        setBackground(skin.getDrawable("black"));
        pad(SPACE_LARGE).padLeft(SPACE_HUGE).padRight(SPACE_HUGE);

        //generating panel
        GeneratingPanel generatingPanel = new GeneratingPanel(false);

        Table table = new Table();
        stack(generatingPanel, table);

        //complete panel
        table.defaults().space(SPACE_MEDIUM);
        CompletePanel completePanel = new CompletePanel(false);
        table.add(completePanel);

        //complete buttons panel
        table.row();
        CompleteButtonsPanel completeButtonsPanel = new CompleteButtonsPanel(false);
        table.add(completeButtonsPanel);

        //animation initial setup
        table.setColor(CLEAR_WHITE);
        table.setTouchable(Touchable.disabled);

        //animation
        addAction(sequence(
            delay(1f),
            targeting(generatingPanel, fadeOut(.3f)),
            targeting(table, fadeIn(.3f)),
            targeting(table, touchable(Touchable.enabled))
        ));
    }

    @Override
    public void captureKeyboardFocus() {

    }

    @Override
    public void finishAnimation() {

    }
}
