package gdx.liftoff.ui.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.ray3k.stripe.PopTable;
import gdx.liftoff.ui.dialogs.FullscreenDialog;

import static gdx.liftoff.Main.*;

/**
 * The table to display the buttons to create a new project, open the project in IDEA, or exit the application after
 * project generation is complete. This panel is intended for use only in the Fullscreen workflow.
 */
public class CompleteButtonsFullscreenPanel extends Table implements Panel {
    PopTable popTable;

    public CompleteButtonsFullscreenPanel(PopTable popTable, boolean fullscreen) {
        this.popTable = popTable;
        populate(fullscreen);
    }

    @Override
    public void populate(boolean fullscreen) {
        //new project button
        defaults().space(SPACE_MEDIUM);
        TextButton textButton = new TextButton(prop.getProperty("newProject"), skin, "big");
        add(textButton);
        addTooltip(textButton, Align.top, TOOLTIP_WIDTH, prop.getProperty("newProjectTip"));
        addHandListener(textButton);
        onChange(textButton, () -> {
            popTable.hide();
            FullscreenDialog.show();
        });

        row();
        Table table = new Table();
        add(table);

        //idea button
        table.defaults().fillX().space(SPACE_MEDIUM);
        textButton = new TextButton(prop.getProperty("openIdea"), skin);
        table.add(textButton);
        addHandListener(textButton);

        //exit button
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
