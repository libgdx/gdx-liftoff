package gdx.liftoff.ui.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import gdx.liftoff.Main;
import gdx.liftoff.ui.data.UserData;

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

        //project field
        TextButton projectFieldButton = addField(UserData.projectPath);
        onChange(projectFieldButton, () -> {
            FileHandle initialFolder = Gdx.files.absolute(Gdx.files.getExternalStoragePath());
            if (UserData.projectPath != null && !UserData.projectPath.isEmpty()) initialFolder = Gdx.files.absolute(UserData.projectPath);

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
                        updateError();
                    }
                }
            });
        });

        if (UserData.platforms.contains(prop.getProperty("android"), false)) {
            //android label
            row();
            label = new Label(prop.getProperty("androidSdkPrompt"), skin, "field");
            label.setEllipsis("...");
            add(label).minWidth(0);

            //android field
            TextButton androidFieldButton = addField(UserData.androidPath);
            onChange(androidFieldButton, () -> {
                FileHandle initialFolder = Gdx.files.absolute(Gdx.files.getExternalStoragePath());
                if (UserData.androidPath != null && !UserData.androidPath.isEmpty()) initialFolder = Gdx.files.absolute(UserData.androidPath);

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
        errorLabel = new Label("Project path cannot be empty", skin, "error");
        errorLabel.setAlignment(Align.center);
        errorLabel.setEllipsis("...");
        add(errorLabel).minWidth(0).colspan(2).growX();
    }

    private void updateError() {
        //todo:update error label based on text entered in the fields
        if (1+1==2) {
            errorLabel.setText("");
        }
    }

    /**
     * Convenience method to add fields to the table.
     * @param text
     */
    private TextButton addField(String text) {
        TextButton browseFieldButton = new TextButton(text, skin, "field");
        browseFieldButton.getLabel().setAlignment(Align.left);
        browseFieldButton.getLabel().setEllipsis("...");
        browseFieldButton.getLabelCell().minWidth(0);
        add(browseFieldButton).growX().minWidth(100);
        addHandListener(browseFieldButton);

        return browseFieldButton;
    }

    @Override
    public void captureKeyboardFocus() {

    }
}
