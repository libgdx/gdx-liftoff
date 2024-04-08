package gdx.liftoff.ui.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.ray3k.stripe.SmashGroup;
import gdx.liftoff.ui.dialogs.ExtensionsDialog;
import gdx.liftoff.ui.dialogs.LanguagesDialog;
import gdx.liftoff.ui.dialogs.PlatformsDialog;
import gdx.liftoff.ui.dialogs.TemplatesDialog;

import static gdx.liftoff.Main.*;
import static gdx.liftoff.ui.data.Data.*;

public class SettingsPanel extends Table implements Panel {
    private Actor keyboardFocus;

    public SettingsPanel() {
        Label label = new Label(prop.getProperty("advanced"), skin, "header");
        add(label).space(30);

        row();
        Table table = new Table();
        add(table);

        table.columnDefaults(0).right().expandX();
        table.columnDefaults(1).growX().maxWidth(100);
        table.defaults().spaceTop(5);
        label = new Label(prop.getProperty("gdxVersion"), skin, "field");
        label.setTouchable(Touchable.enabled);
        table.add(label);

        TextField textField = new TextField("", skin);
        table.add(textField);
        addTooltip(textField, label, Align.top, 0, prop.getProperty("gdxVersionTip"));
        addIbeamListener(textField);
        keyboardFocus = textField;

        table.row();
        label = new Label(prop.getProperty("javaVersion"), skin, "field");
        label.setTouchable(Touchable.enabled);
        table.add(label);

        textField = new TextField("", skin);
        table.add(textField);
        addTooltip(textField, label, Align.top, 0, prop.getProperty("javaVersionTip"));
        addIbeamListener(textField);

        table.row();
        label = new Label(prop.getProperty("version"), skin, "field");
        label.setTouchable(Touchable.enabled);
        table.add(label);

        textField = new TextField("", skin);
        table.add(textField);
        addTooltip(textField, label, Align.top, 0, prop.getProperty("versionTip"));
        addIbeamListener(textField);

        table.row();
        label = new Label(prop.getProperty("robovmVersion"), skin, "field");
        label.setTouchable(Touchable.enabled);
        table.add(label);

        textField = new TextField("", skin);
        table.add(textField);
        addTooltip(textField, label, Align.top, 0, prop.getProperty("robovmVersionTip"));
        addIbeamListener(textField);
    }

    @Override
    public void captureKeyboardFocus() {
        stage.setKeyboardFocus(keyboardFocus);
    }
}
