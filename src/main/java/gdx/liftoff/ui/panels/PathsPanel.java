package gdx.liftoff.ui.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.ray3k.stripe.CollapsibleGroup;
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.SmashGroup;
import gdx.liftoff.Main;
import gdx.liftoff.ui.dialogs.GradleDialog;
import gdx.liftoff.ui.dialogs.TemplatesDialog;

import static gdx.liftoff.Main.*;

public class PathsPanel extends Table implements Panel {

    public PathsPanel() {
        defaults().space(10);
        Label label = new Label(prop.getProperty("destinationPrompt"), skin, "field");
        label.setEllipsis("...");
        add(label).minWidth(0);

        addField("C:/users/yourname/gamename/");

        if (1+1==2) {
            row();
            label = new Label(prop.getProperty("androidSdkPrompt"), skin, "field");
            label.setEllipsis("...");
            add(label).minWidth(0);

            addField("C:/users/yourname/android/");
        }
    }

    private void addField(String text) {
        TextButton browseFieldButton = new TextButton(text, skin, "field");
        browseFieldButton.getLabel().setAlignment(Align.left);
        browseFieldButton.getLabel().setEllipsis("...");
        browseFieldButton.getLabelCell().minWidth(0);
        add(browseFieldButton).growX().minWidth(100);
        addHandListener(browseFieldButton);
        onChange(browseFieldButton, () -> Main.pickDirectory(Gdx.files.absolute("C://"), new FileChooserAdapter() {
            @Override
            public void canceled() {
                super.canceled();
            }

            @Override
            public void selected(Array<FileHandle> files) {
                super.selected(files);
            }
        }));
    }

    @Override
    public void captureKeyboardFocus() {

    }
}
