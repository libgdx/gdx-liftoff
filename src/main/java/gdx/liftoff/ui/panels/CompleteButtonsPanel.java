package gdx.liftoff.ui.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.utils.Align;
import com.ray3k.stripe.PopTable;
import gdx.liftoff.ui.UserData;
import gdx.liftoff.ui.dialogs.FullscreenDialog;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

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
    boolean intellijIsFlatpak = false;

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
            List<String> findIntellijExecutable = (UIUtils.isWindows) ? Arrays.asList("where.exe", "idea") : Arrays.asList("which", "idea");

            Process whereProcess = new ProcessBuilder(findIntellijExecutable).start();
            if (whereProcess.waitFor() == 0) {
                intellijPath = new BufferedReader(new InputStreamReader(whereProcess.getInputStream())).readLine();
                ideaButton.setDisabled(false);
            } else {
                if (UIUtils.isLinux) {
                    intellijPath = findFlatpakIntellij();
                    intellijIsFlatpak = true;
                    ideaButton.setDisabled(false);
                } else if (UIUtils.isWindows) {
                    intellijPath = findManuallyInstalledIntellijOnWindows();
                    ideaButton.setDisabled(false);
                } else {
                    throw new Exception("IntelliJ not found");
                }
            }
        } catch (Exception e) {
            addTooltip(ideaButton, Align.top, TOOLTIP_WIDTH, prop.getProperty("ideaNotFoundTip"));
            ideaButton.setDisabled(true);
        }

        onChange(ideaButton, () -> {
            try {
                if (intellijIsFlatpak) {
                    new ProcessBuilder("flatpak", "run", intellijPath, ".").directory(Gdx.files.absolute(UserData.projectPath).file()).start();
                } else {
                    new ProcessBuilder(intellijPath, ".").directory(Gdx.files.absolute(UserData.projectPath).file()).start();
                }
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

    private static String findManuallyInstalledIntellijOnWindows() throws Exception {
        String programFilesFolder = System.getenv("PROGRAMFILES");
        File jetbrainsFolder = new File(programFilesFolder, "JetBrains");
        File[] ideaFolders = jetbrainsFolder.listFiles((dir, name) -> name.contains("IDEA"));
        if (ideaFolders == null) {
            throw new Exception("IntelliJ not found");
        }

        File intellijFolder = Stream.of(ideaFolders)
            .max(Comparator.comparingLong(File::lastModified))
            .orElseThrow(() -> new Exception("IntelliJ not found"));

        return new File(intellijFolder, "bin/idea64.exe").getAbsolutePath();
    }

    @NotNull
    private static String findFlatpakIntellij() throws Exception {
        List<String> findFlatpakIntelliJ = Arrays.asList("flatpak", "list", "--app", "--columns=application");

        Process flatpakProcess = new ProcessBuilder(findFlatpakIntelliJ).start();
        if (flatpakProcess.waitFor() != 0) {
            throw new Exception("Flatpak not found");
        }

        List<String> intellijIds = Arrays.asList("com.jetbrains.IntelliJ-IDEA-Ultimate", "com.jetbrains.IntelliJ-IDEA-Community");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(flatpakProcess.getInputStream()));

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (intellijIds.contains(line)) {
                return line;
            }
        }
        throw new Exception("Flatpak IntelliJ not installed");
    }

    @Override
    public void captureKeyboardFocus() {

    }
}
