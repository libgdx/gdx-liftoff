package gdx.liftoff.ui.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;

import static gdx.liftoff.Main.*;

/**
 * The table to display the buttons to create a new project, open the project in IDEA, or exit the application after
 * project generation is complete. This panel is intended for use only in the normal and quick project workflows.
 */
public class CompleteButtonsPanel extends Table implements Panel {
    public CompleteButtonsPanel() {
        defaults().space(SPACE_MEDIUM);
        TextButton textButton = new TextButton(prop.getProperty("newProject"), skin, "big");
        add(textButton);
        addTooltip(textButton, Align.top, TOOLTIP_WIDTH, prop.getProperty("newProjectTip"));
        addHandListener(textButton);
        onChange(textButton, () -> root.transitionTable(root.landingTable, true));

        row();
        Table table = new Table();
        add(table);

        table.defaults().fillX().space(SPACE_MEDIUM);
        textButton = new TextButton(prop.getProperty("openIdea"), skin);
        table.add(textButton);
        addHandListener(textButton);

        table.row();
        textButton = new TextButton(prop.getProperty("exit"), skin);
        table.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> Gdx.app.exit());
    }

    @Override
    public void captureKeyboardFocus() {

    }
}
