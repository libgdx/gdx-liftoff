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
        onChange(projectFieldButton, () -> Main.pickDirectory(Gdx.files.absolute(Gdx.files.getExternalStoragePath()), new FileChooserAdapter() {
            @Override
            public void canceled() {

            }

            @Override
            public void selected(Array<FileHandle> files) {
                if (files.size > 0) {
                    String path = files.first().path();
                    projectFieldButton.setText(path);
                    UserData.projectPath = path;
                }
            }
        }));

        if (UserData.platforms.contains(prop.getProperty("android"), false)) {
            //android label
            row();
            label = new Label(prop.getProperty("androidSdkPrompt"), skin, "field");
            label.setEllipsis("...");
            add(label).minWidth(0);

            //android field
            TextButton androidFieldButton = addField(UserData.androidPath);
            onChange(androidFieldButton, () -> Main.pickDirectory(Gdx.files.absolute(Gdx.files.getExternalStoragePath()), new FileChooserAdapter() {
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
                    }
                }
            }));
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
