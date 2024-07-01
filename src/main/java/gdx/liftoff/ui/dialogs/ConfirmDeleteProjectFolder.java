package gdx.liftoff.ui.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.Align;
import com.ray3k.stripe.PopTable;
import gdx.liftoff.ui.UserData;

import static gdx.liftoff.Main.*;
import static gdx.liftoff.ui.dialogs.FullscreenDialog.*;

public class ConfirmDeleteProjectFolder extends PopTable {
    public ConfirmDeleteProjectFolder() {
        setStyle(skin.get("dialog", WindowStyle.class));
        setKeepCenteredInWindow(true);
        setHideOnUnfocus(true);
        pad(SPACE_LARGE);

        defaults().space(SPACE_LARGE);
        Label label = new Label(prop.getProperty("deleteFolderConfirmation"), skin);
        label.setWrap(true);
        add(label).growX();

        row();
        label = new Label("[RED]" + UserData.projectPath + "[]", skin, "descriptionWithMarkup");
        label.setWrap(true);
        label.setAlignment(Align.center);
        add(label).growX().minWidth(300);

        row();
        Table table = new Table();
        add(table);

        table.defaults().space(SPACE_HUGE);
        TextButton deleteButton = new TextButton(prop.getProperty("delete"), skin);
        table.add(deleteButton).uniformX().fillX();
        addHandListener(deleteButton);
        onChange(deleteButton, this::deleteFolderContents);
        key(Keys.ENTER, this::deleteFolderContents);

        TextButton cancelButton = new TextButton(prop.getProperty("quickCancel"), skin);
        table.add(cancelButton).uniformX().fillX();
        addHandListener(cancelButton);
        onChange(cancelButton, this::hide);
        key(Keys.ESCAPE, this::hide);
    }

    private void deleteFolderContents() {
        FileHandle fileHandle = Gdx.files.absolute(UserData.projectPath);
        for (FileHandle child : fileHandle.list()) {
            child.deleteDirectory();
        }
        Gdx.app.postRunnable(() -> root.settingsTable.updateError());
        if (fullscreenDialog != null) fullscreenDialog.updatePathsError();
        hide();
    }

    public static void showDialog() {
        ConfirmDeleteProjectFolder pop = new ConfirmDeleteProjectFolder();
        pop.show(stage);
    }
}
