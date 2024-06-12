package gdx.liftoff.ui.liftofftables;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import gdx.liftoff.Main;
import gdx.liftoff.ui.panels.PathsPanel;
import gdx.liftoff.ui.panels.SettingsPanel;

import static gdx.liftoff.Main.*;

/**
 * The final table displayed before the user generates the project in the normal workflow. It includes the settings and
 * paths panels.
 */
public class SettingsTable extends LiftoffTable {
    private SettingsPanel settingsPanel;
    private TextButton generateButton;

    public SettingsTable() {
        populate();
    }

    public void populate() {
        clearChildren();
        setBackground(skin.getDrawable("black"));
        pad(SPACE_LARGE).padLeft(SPACE_HUGE).padRight(SPACE_HUGE);

        //The scrollable area includs the settings and paths panels
        Table scrollTable = new Table();
        scrollTable.pad(SPACE_SMALL);
        ScrollPane scrollPane = new ScrollPane(scrollTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        add(scrollPane).grow();
        addScrollFocusListener(scrollPane);

        //settings panel
        scrollTable.defaults().space(SPACE_LARGE);
        settingsPanel = new SettingsPanel(false);
        scrollTable.add(settingsPanel).growX().spaceTop(0).maxHeight(500);

        //paths panel
        scrollTable.row();
        PathsPanel pathsPanel = new PathsPanel(false);
        scrollTable.add(pathsPanel).growX().spaceTop(SPACE_HUGE);

        row();
        Table table = new Table();
        add(table).bottom().growX();

        //previous button
        TextButton textButton = new TextButton(prop.getProperty("previous"), skin);
        table.add(textButton).uniformX().fillX();
        addHandListener(textButton);
        addTooltip(textButton, Align.top, prop.getProperty("previousTip"));
        onChange(textButton, () -> root.previousTable());

        //empty space between buttons
        table.add().growX().space(SPACE_SMALL);

        //generate button
        generateButton = new TextButton(prop.getProperty("generate"), skin);
        generateButton.setDisabled(!validateUserData());
        table.add(generateButton).uniformX().fillX();
        addHandListener(generateButton);
        addTooltip(generateButton, Align.top, prop.getProperty("generateTip"));
        onChange(generateButton, () -> {
            Main.generateProject();
            root.nextTable();
        });
    }

    @Override
    public void captureKeyboardFocus() {
        settingsPanel.captureKeyboardFocus();
    }

    @Override
    public void finishAnimation() {

    }

    public void updateGenerateButton() {
        generateButton.setDisabled(!validateUserData());
    }

}
