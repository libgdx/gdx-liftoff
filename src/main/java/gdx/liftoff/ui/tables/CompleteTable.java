package gdx.liftoff.ui.tables;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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
        CompletePanel completePanel = new CompletePanel();
        stack(generatingPanel, completePanel);

        //initial setup
        completePanel.setColor(CLEAR_WHITE);
        completePanel.setTouchable(Touchable.disabled);

        //animation
        addAction(sequence(
            delay(1f),
            targeting(generatingPanel, fadeOut(.3f)),
            targeting(completePanel, fadeIn(.3f)),
            targeting(completePanel, touchable(Touchable.enabled))
        ));
    }

    @Override
    public void captureKeyboardFocus() {

    }

    @Override
    public void finishAnimation() {

    }
}
