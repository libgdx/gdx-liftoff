package gdx.liftoff.ui.liftofftables;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import gdx.liftoff.ui.panels.PathsPanel;
import gdx.liftoff.ui.panels.SettingsPanel;

import static gdx.liftoff.Main.*;

/**
 * The final table displayed before the user generates the project in the normal workflow. It includes the settings and
 * paths panels.
 */
public class SettingsTable extends LiftoffTable {
    private SettingsPanel settingsPanel;

    public SettingsTable() {
        populate();
    }

    private void populate() {
        clearChildren();
        setBackground(skin.getDrawable("black"));
        pad(20).padLeft(30).padRight(30);

        //The scrollable area includs the settings and paths panels
        Table scrollTable = new Table();
        scrollTable.pad(5);
        ScrollPane scrollPane = new ScrollPane(scrollTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        add(scrollPane).grow();
        addScrollFocusListener(scrollPane);

        //settings panel
        scrollTable.defaults().space(30);
        settingsPanel = new SettingsPanel();
        scrollTable.add(settingsPanel).growX().spaceTop(0).maxHeight(500);

        //paths panel
        scrollTable.row();
        PathsPanel pathsPanel = new PathsPanel();
        scrollTable.add(pathsPanel).growX().spaceTop(30);

        row();
        Table table = new Table();
        add(table).bottom().growX();

        //previous button
        TextButton textButton = new TextButton(prop.getProperty("previous"), skin);
        table.add(textButton).uniformX().fillX();
        addHandListener(textButton);
        onChange(textButton, () -> root.previousTable());

        //empty space between buttons
        table.add().growX().space(5);

        //generate button
        textButton = new TextButton(prop.getProperty("generate"), skin);
        table.add(textButton).uniformX().fillX();
        addHandListener(textButton);
        onChange(textButton, () -> root.nextTable());
    }

    @Override
    public void captureKeyboardFocus() {
        settingsPanel.captureKeyboardFocus();
    }

    @Override
    public void finishAnimation() {

    }
}
