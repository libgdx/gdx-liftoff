package gdx.liftoff.ui.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import gdx.liftoff.Main;
import gdx.liftoff.config.Configuration;
import gdx.liftoff.ui.UserData;
import gdx.liftoff.ui.dialogs.ConfirmDeleteProjectFolder;
import gdx.liftoff.ui.dialogs.FullscreenDialog;

import java.util.ArrayList;
import java.util.List;

import static gdx.liftoff.Main.*;

/**
 * Table that displays the project path, and the Android SDK path if Android is selected as a platform.
 */
public class PathsPanel extends Table implements Panel {
    private TypingLabel errorLabel;
    private Button deleteProjectPathButton;

    public PathsPanel(boolean fullscreen) {
        populate(fullscreen);
    }

    public void populate(boolean fullscreen) {
        //project label
        defaults().space(SPACE_MEDIUM);
        Label label = new Label(prop.getProperty("destinationPrompt"), skin, "field");
        label.setEllipsis("...");
        add(label).minWidth(0);
        addTooltip(label, Align.top, 0, prop.getProperty("destinationTip"));

        //project field
        TextButton projectFieldButton = addField(UserData.projectPath);
        addTooltip(projectFieldButton, label, Align.top, 0, prop.getProperty("destinationTip"));
        onChange(projectFieldButton, () -> {
            Gdx.input.setInputProcessor(null);
            Gdx.graphics.setSystemCursor(SystemCursor.Arrow);

            FileHandle initialFolder = Gdx.files.absolute(Gdx.files.getExternalStoragePath());
            if (UserData.projectPath != null && !UserData.projectPath.isEmpty())
                initialFolder = Gdx.files.absolute(UserData.projectPath);

            Main.pickDirectory(initialFolder, new FileChooserAdapter() {
                @Override
                public void canceled() {
                    Gdx.app.postRunnable(() -> Gdx.input.setInputProcessor(stage));
                }

                @Override
                public void selected(Array<FileHandle> files) {
                    if (files.size > 0) {
                        String path = files.first().path();
                        projectFieldButton.setText(path);
                        UserData.projectPath = path;
                        pref.putString("projectPath", path);
                        flushPref();
                        updateError();
                        if (FullscreenDialog.fullscreenDialog != null)
                            FullscreenDialog.fullscreenDialog.updateGenerateButtons();
                        if (root.settingsTable != null) root.settingsTable.updateGenerateButton();
                        updateDeleteProjectPathButton();
                    }
                    Gdx.app.postRunnable(() -> Gdx.input.setInputProcessor(stage));
                }
            });
        });

        //select folder button
        Button button = new Button(skin, "folder");
        add(button);
        addHandListener(button);
        addTooltip(button, Align.top, 0, prop.getProperty("destinationTip"));
        onChange(button, () -> projectFieldButton.setChecked(!projectFieldButton.isChecked()));

        //delete folder contents button
        deleteProjectPathButton = new Button(skin, "delete-folder");
        add(deleteProjectPathButton);
        addHandListener(deleteProjectPathButton);
        addTooltip(deleteProjectPathButton, Align.top, 0, prop.getProperty("deleteFolder"));
        onChange(deleteProjectPathButton, ConfirmDeleteProjectFolder::showDialog);
        updateDeleteProjectPathButton();

        if (UserData.platforms.contains("android")) {
            //android label
            row();
            label = new Label(prop.getProperty("androidSdkPrompt"), skin, "field");
            label.setEllipsis("...");
            add(label).minWidth(0);
            addTooltip(label, Align.top, 0, prop.getProperty("sdkTip"));

            //android field
            TextButton androidFieldButton = addField(UserData.androidPath);
            addTooltip(androidFieldButton, label, Align.top, 0, prop.getProperty("sdkTip"));
            onChange(androidFieldButton, () -> {
                Gdx.input.setInputProcessor(null);
                Gdx.graphics.setSystemCursor(SystemCursor.Arrow);

                FileHandle initialFolder = Gdx.files.absolute(Gdx.files.getExternalStoragePath());
                if (UserData.androidPath != null && !UserData.androidPath.isEmpty())
                    initialFolder = Gdx.files.absolute(UserData.androidPath);

                Main.pickDirectory(initialFolder, new FileChooserAdapter() {
                    @Override
                    public void canceled() {
                        Gdx.app.postRunnable(() -> Gdx.input.setInputProcessor(stage));
                    }

                    @Override
                    public void selected(Array<FileHandle> files) {
                        if (files.size > 0) {
                            String path = files.first().path();
                            androidFieldButton.setText(path);
                            UserData.androidPath = path;
                            pref.putString("AndroidSdk", path);
                            flushPref();
                            updateError();
                            if (FullscreenDialog.fullscreenDialog != null)
                                FullscreenDialog.fullscreenDialog.updateGenerateButtons();
                            if (root.settingsTable != null) root.settingsTable.updateGenerateButton();
                        }
                        Gdx.app.postRunnable(() -> Gdx.input.setInputProcessor(stage));
                    }
                });
            });

            button = new Button(skin, "folder");
            add(button);
            addHandListener(button);
            addTooltip(button, Align.top, 0, prop.getProperty("sdkTip"));
            onChange(button, () -> androidFieldButton.setChecked(!androidFieldButton.isChecked()));
        }

        row();
        errorLabel = new TypingLabel("", skin, "error");
        errorLabel.setAlignment(Align.left);
        errorLabel.setWrap(true);
        add(errorLabel).minSize(0, errorLabel.getFont().cellHeight * 2).align(Align.bottomLeft).colspan(4).growX();
        updateError();
    }

    private void updateDeleteProjectPathButton() {
        deleteProjectPathButton.setDisabled(UserData.projectPath == null || UserData.projectPath.isEmpty());
    }

    public void updateError() {
        final List<String> errors = new ArrayList<>();

        final String java = UserData.javaVersion;
        final double javaInt = Configuration.parseJavaVersion(java);

        if (UserData.platforms.contains("html") && javaInt > 11) {
            errors.add(prop.getProperty("htmlWrongJavaVersion"));
        }

        if (UserData.platforms.contains("ios")) {
            if (UserData.platforms.contains("teavm")) errors.add(prop.getProperty("iosTeavmIncompatible"));
            if (!"7".equals(java) && !"8".equals(java)) errors.add(prop.getProperty("iosWrongJavaVersion"));
        }

        if (UserData.platforms.contains("teavm") && javaInt < 11) {
            errors.add(prop.getProperty("teavmWrongJavaVersion"));
        }

        if (UserData.projectPath == null || UserData.projectPath.isEmpty()) {
            errors.add(prop.getProperty("nullDirectory"));
        }

        FileHandle tempFileHandle = Gdx.files.absolute(UserData.projectPath);
        if (!tempFileHandle.exists() || !tempFileHandle.isDirectory()) {
            errors.add(prop.getProperty("notDirectory"));
        }

        boolean android = UserData.platforms.contains("android");
        if (android && (UserData.androidPath == null || UserData.androidPath.isEmpty())) {
            errors.add(prop.getProperty("sdkNullDirectory"));
        }

        tempFileHandle = Gdx.files.absolute(UserData.androidPath);
        if (android && (!tempFileHandle.exists() || !tempFileHandle.isDirectory())) {
            errors.add(prop.getProperty("sdkNotDirectory"));
        }

        if (android && !Main.isAndroidSdkDirectory(UserData.androidPath)) {
            errors.add(prop.getProperty("invalidSdkDirectory"));
        }

        tempFileHandle = Gdx.files.absolute(UserData.projectPath);
        if (UIUtils.isWindows && tempFileHandle.path().matches("(?i).*([\\\\/]|^)OneDrive[^\\\\/]*([\\\\/]|$).*")) {
            errors.add(prop.getProperty("noOneDrivePaths"));
        }

        if (tempFileHandle.list().length != 0) {
            errors.add(prop.getProperty("notEmptyDirectory"));
        }

        if (errors.isEmpty()) {
            errorLabel.restart("");
        } else if (errors.size() == 1) {
            errorLabel.restart(errors.get(0));
        } else {
            errorLabel.restart(String.join("\n", errors));
        }
        errorLabel.skipToTheEnd();
    }

    public boolean hasError() {
        return errorLabel.layout.countGlyphs() != 0;
    }

    /**
     * Convenience method to add fields to the table.
     *
     * @param text The name of the field
     */
    private TextButton addField(String text) {
        TextButton browseFieldButton = new TextButton(text, skin, "path");
        browseFieldButton.getLabel().setAlignment(Align.left);
        browseFieldButton.getLabel().setWrap(true);
        browseFieldButton.getLabelCell().minWidth(0);
        add(browseFieldButton).growX().minWidth(100).maxWidth(300);
        addHandListener(browseFieldButton);

        return browseFieldButton;
    }

    @Override
    public void captureKeyboardFocus() {

    }
}
