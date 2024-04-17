package gdx.liftoff.ui.liftofftables;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import gdx.liftoff.ui.panels.CompleteButtonsPanel;
import gdx.liftoff.ui.panels.CompletePanel;
import gdx.liftoff.ui.panels.GeneratingPanel;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static gdx.liftoff.Main.CLEAR_WHITE;
import static gdx.liftoff.Main.skin;

public class CompleteTable extends LiftoffTable {
    public CompleteTable() {
        populate();
    }

    private void populate() {
        clearChildren();
        setBackground(skin.getDrawable("black"));
        pad(20).padLeft(30).padRight(30);

        GeneratingPanel generatingPanel = new GeneratingPanel();

        Table table = new Table();
        stack(generatingPanel, table);

        table.defaults().space(10);
        CompletePanel completePanel = new CompletePanel();
        table.add(completePanel);

        table.row();
        CompleteButtonsPanel completeButtonsPanel = new CompleteButtonsPanel();
        table.add(completeButtonsPanel);

        //initial setup
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
