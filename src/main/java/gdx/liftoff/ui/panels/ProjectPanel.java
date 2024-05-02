package gdx.liftoff.ui.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import gdx.liftoff.Main;
import gdx.liftoff.ui.data.UserData;

import static gdx.liftoff.Main.*;

/**
 * A table including fields for project name, package, and main class. A label displaying an applicable error message is
 * shown below.
 */
public class ProjectPanel extends Table implements Panel {
    private Actor keyboardActor;

    public ProjectPanel() {
        columnDefaults(0).right();
        columnDefaults(1).growX();
        defaults().space(SPACE_MEDIUM);

        //project label
        Label label = new Label(prop.getProperty("project"), skin);
        add(label);
        addTooltip(label, Align.top, TOOLTIP_WIDTH_LARGE, prop.getProperty("nameTip"));

        //project field
        TextField textField = new TextField("", skin);
        textField.setText(UserData.projectName);
        keyboardActor = textField;
        add(textField);
        addIbeamListener(textField);
        addTooltip(textField, label, Align.top, TOOLTIP_WIDTH_LARGE, prop.getProperty("nameTip"));
        onChange(textField, () -> {

        });

        //package label
        row();
        label = new Label(prop.getProperty("package"), skin);
        add(label);
        addTooltip(label, Align.top, TOOLTIP_WIDTH_LARGE, prop.getProperty("packageTip"));

        //package field
        textField = new TextField("", skin);
        textField.setText(UserData.packageName);
        add(textField);
        addIbeamListener(textField);
        addTooltip(textField, label, Align.top, TOOLTIP_WIDTH_LARGE, prop.getProperty("packageTip"));
        onChange(textField, () -> {

        });

        //main class label
        row();
        label = new Label(prop.getProperty("mainClass"), skin);
        add(label);
        addTooltip(label, Align.top, TOOLTIP_WIDTH_LARGE, prop.getProperty("classTip"));

        //main class field
        textField = new TextField("", skin);
        textField.setText(UserData.mainClassName);
        add(textField);
        addIbeamListener(textField);
        addTooltip(textField, label, Align.top, TOOLTIP_WIDTH_LARGE, prop.getProperty("classTip"));
        onChange(textField, () -> {

        });

        //error label
        columnDefaults(0).reset();
        row();
        label = new Label("Project name cannot be empty", skin, "error");
        label.setEllipsis("...");
        add(label).colspan(2).minWidth(0);
    }

    public void captureKeyboardFocus() {
        Main.stage.setKeyboardFocus(keyboardActor);
    }
}
