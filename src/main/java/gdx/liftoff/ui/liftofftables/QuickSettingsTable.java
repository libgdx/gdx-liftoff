package gdx.liftoff.ui.liftofftables;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import gdx.liftoff.Main;
import gdx.liftoff.ui.panels.PathsPanel;

import static gdx.liftoff.Main.*;

/**
 * A unique workflow table to bypass the full setup. This table includes the paths panel.
 */
public class QuickSettingsTable extends LiftoffTable {
    public QuickSettingsTable() {
        populate();
    }

    @Override
    public void populate() {
        clearChildren();
        setBackground(skin.getDrawable("black"));
        pad(SPACE_LARGE).padLeft(SPACE_HUGE).padRight(SPACE_HUGE);

        //title
        defaults().space(SPACE_HUGE);
        Label label = new Label(prop.getProperty("pathSettings"), skin, "header");
        add(label);

        //paths panel
        row();
        PathsPanel pathsPanel = new PathsPanel(false);
        add(pathsPanel).expandX().spaceTop(SPACE_HUGE);

        row();
        Table table = new Table();
        add(table);

        //generate button
        table.defaults().space(SPACE_MEDIUM).fillX();
        TextButton textButton = new TextButton(prop.getProperty("generate"), skin, "big");
        textButton.setDisabled(!validateUserData());
        table.add(textButton);
        addHandListener(textButton);
        addTooltip(textButton, Align.top, prop.getProperty("generateTip"));
        onChange(textButton, () -> {
            Main.generateProject();
            root.transitionTable(root.completeTable, true);
        });

        //cancel button
        table.row();
        textButton = new TextButton(prop.getProperty("quickCancel"), skin);
        table.add(textButton);
        addHandListener(textButton);
        addTooltip(textButton, Align.top, prop.getProperty("cancelTip"));
        onChange(textButton, () -> root.transitionTable(root.landingTable, false));
    }

    @Override
    public void captureKeyboardFocus() {

    }

    @Override
    public void finishAnimation() {

    }
}
