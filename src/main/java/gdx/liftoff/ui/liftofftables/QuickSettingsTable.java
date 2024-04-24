package gdx.liftoff.ui.liftofftables;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import gdx.liftoff.ui.panels.PathsPanel;

import static gdx.liftoff.Main.*;

/**
 * A unique workflow table to bypass the full setup. This table includes the paths panel.
 */
public class QuickSettingsTable extends LiftoffTable {
    public QuickSettingsTable() {
        populate();
    }

    private void populate() {
        clearChildren();
        setBackground(skin.getDrawable("black"));
        pad(20).padLeft(30).padRight(30);

        //title
        defaults().space(30);
        Label label = new Label(prop.getProperty("pathSettings"), skin, "header");
        add(label);

        //paths panel
        row();
        PathsPanel pathsPanel = new PathsPanel();
        add(pathsPanel).growX().spaceTop(30);

        row();
        Table table = new Table();
        add(table);

        //generate button
        table.defaults().space(10).fillX();
        TextButton textButton = new TextButton(prop.getProperty("generate"), skin, "big");
        table.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> root.transitionTable(root.completeTable, true));

        //cancel button
        table.row();
        textButton = new TextButton(prop.getProperty("quickCancel"), skin);
        table.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> root.transitionTable(root.landingTable, false));
    }

    @Override
    public void captureKeyboardFocus() {

    }

    @Override
    public void finishAnimation() {

    }
}
