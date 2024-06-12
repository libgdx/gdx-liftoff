package gdx.liftoff.ui.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import gdx.liftoff.Main;
import gdx.liftoff.ui.UserData;
import gdx.liftoff.ui.dialogs.FullscreenDialog;

import static gdx.liftoff.Main.*;

/**
 * Table that displays the project path and the android sdk path if android is selected as a platform
 */
public class PathsPanel extends Table implements Panel {
    private Label errorLabel;

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
            FileHandle initialFolder = Gdx.files.absolute(Gdx.files.getExternalStoragePath());
            if (UserData.projectPath != null && !UserData.projectPath.isEmpty())
                initialFolder = Gdx.files.absolute(UserData.projectPath);

            Main.pickDirectory(initialFolder, new FileChooserAdapter() {
                @Override
                public void canceled() {

                }

                @Override
                public void selected(Array<FileHandle> files) {
                    if (files.size > 0) {
                        String path = files.first().path();
                        projectFieldButton.setText(path);
                        UserData.projectPath = path;
                        pref.putString("projectPath", path);
                        pref.flush();
                        updateError();
                        if (FullscreenDialog.fullscreenDialog != null)
                            FullscreenDialog.fullscreenDialog.updateGenerateButton();
                        if (root.settingsTable != null) root.settingsTable.updateGenerateButton();
                        root.quickSettingsTable.populate();
                    }
                }
            });
        });

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
                FileHandle initialFolder = Gdx.files.absolute(Gdx.files.getExternalStoragePath());
                if (UserData.androidPath != null && !UserData.androidPath.isEmpty())
                    initialFolder = Gdx.files.absolute(UserData.androidPath);

                Main.pickDirectory(initialFolder, new FileChooserAdapter() {
                    @Override
                    public void canceled() {

                    }

                    @Override
                    public void selected(Array<FileHandle> files) {
                        if (files.size > 0) {
                            String path = files.first().path();
                            androidFieldButton.setText(path);
                            UserData.androidPath = path;
                            pref.putString("AndroidSdk", path);
                            pref.flush();
                            updateError();
                        }
                    }
                });
            });
        }

        row();
        errorLabel = new Label("", skin, "error");
        errorLabel.setAlignment(Align.center);
        errorLabel.setWrap(true);
        add(errorLabel).minSize(0, errorLabel.getStyle().font.getLineHeight() * 2).colspan(2).growX();
        updateError();
    }

    private void updateError() {
        if (UserData.projectPath == null || UserData.projectPath.isEmpty()) {
            errorLabel.setText(prop.getProperty("nullDirectory"));
            return;
        }

        FileHandle tempFileHandle = Gdx.files.absolute(UserData.projectPath);
        if (!tempFileHandle.exists() || !tempFileHandle.isDirectory()) {
            errorLabel.setText(prop.getProperty("notDirectory"));
            return;
        }

        boolean android = UserData.platforms.contains("android");
        if (android && (UserData.androidPath == null || UserData.androidPath.isEmpty())) {
            errorLabel.setText(prop.getProperty("sdkNullDirectory"));
            return;
        }

        tempFileHandle = Gdx.files.absolute(UserData.androidPath);
        if (android && (!tempFileHandle.exists() || !tempFileHandle.isDirectory())) {
            errorLabel.setText(prop.getProperty("sdkNotDirectory"));
            return;
        }

        if (android && !Main.isAndroidSdkDirectory(UserData.androidPath)) {
            errorLabel.setText(prop.getProperty("invalidSdkDirectory"));
            return;
        }

        tempFileHandle = Gdx.files.absolute(UserData.projectPath);
        if (tempFileHandle.list().length != 0) {
            errorLabel.setText(prop.getProperty("notEmptyDirectory"));
            return;
        }

        errorLabel.setText("");
    }

    public boolean hasError() {
        return errorLabel.getText().notEmpty();
    }

    /**
     * Convenience method to add fields to the table.
     *
     * @param text The name of the field
     */
    private TextButton addField(String text) {
        TextButton browseFieldButton = new TextButton(text, skin, "field");
        browseFieldButton.getLabel().setAlignment(Align.left);
        browseFieldButton.getLabel().setEllipsis("...");
        browseFieldButton.getLabelCell().minWidth(0);
        add(browseFieldButton).growX().minWidth(100).maxWidth(300);
        addHandListener(browseFieldButton);

        return browseFieldButton;
    }

    @Override
    public void captureKeyboardFocus() {

    }
}
