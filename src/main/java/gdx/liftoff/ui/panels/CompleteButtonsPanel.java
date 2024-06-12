package gdx.liftoff.ui.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.utils.Align;
import com.ray3k.stripe.PopTable;
import gdx.liftoff.ui.UserData;
import gdx.liftoff.ui.dialogs.FullscreenDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import static gdx.liftoff.Main.*;

/**
 * The table to display the buttons to create a new project, open the project in IDEA, or exit the application after
 * project generation is complete.
 */
public class CompleteButtonsPanel extends Table implements Panel {
    /**
     * The PopTable to hide after the user clicks a button
     */
    PopTable popTable;

    String intellijPath = null;

    public CompleteButtonsPanel(boolean fullscreen) {
        this(null, fullscreen);
    }

    public CompleteButtonsPanel(PopTable popTable, boolean fullscreen) {
        this.popTable = popTable;
        populate(fullscreen);
    }

    @Override
    public void populate(boolean fullscreen) {
        defaults().space(SPACE_MEDIUM);

        //new project button
        TextButton textButton = new TextButton(prop.getProperty("newProject"), skin, "big");
        add(textButton);
        addTooltip(textButton, Align.top, TOOLTIP_WIDTH, prop.getProperty("newProjectTip"));
        addHandListener(textButton);
        if (!fullscreen) onChange(textButton, () -> root.transitionTable(root.landingTable, true));
        else {
            onChange(textButton, () -> {
                popTable.hide();
                FullscreenDialog.show();
                root.showTableInstantly(root.landingTable);
            });
        }

        row();
        Table table = new Table();
        add(table);

        //idea button
        table.defaults().fillX().space(SPACE_MEDIUM);
        final TextButton ideaButton = new TextButton(prop.getProperty("openIdea"), skin);
        table.add(ideaButton);
        addHandListener(ideaButton);
        try {
            List<String> findIntellij = (UIUtils.isWindows) ? Arrays.asList("where.exe", "idea") : Arrays.asList("which", "idea");

            Process process = new ProcessBuilder(findIntellij).start();
            if (process.waitFor() != 0) {
                throw new Exception("IntelliJ not found");
            }

            intellijPath = new BufferedReader(new InputStreamReader(process.getInputStream())).readLine();
            ideaButton.setDisabled(false);
        } catch (Exception e) {
            addTooltip(ideaButton, Align.top, TOOLTIP_WIDTH, prop.getProperty("ideaNotFoundTip"));
            ideaButton.setDisabled(true);
        }
        onChange(ideaButton, () -> {
            try {
                new ProcessBuilder(intellijPath, ".").directory(Gdx.files.absolute(UserData.projectPath).file()).start();
            } catch (IOException e) {
                ideaButton.setText("WHOOPS");
            }
        });

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
