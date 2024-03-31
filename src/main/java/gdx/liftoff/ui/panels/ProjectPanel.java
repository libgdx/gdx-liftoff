package gdx.liftoff.ui.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import gdx.liftoff.Main;

import static gdx.liftoff.Main.*;

public class ProjectPanel extends Table  {
    private Actor keyboardActor;

    public ProjectPanel() {
        columnDefaults(0).right();
        columnDefaults(1).growX();
        defaults().space(10);

        Label label = new Label("PROJECT NAME", skin);
        add(label);

        TextField textField = new TextField("", skin);
        keyboardActor = textField;
        add(textField);
        addIbeamListener(textField);
        onChange(textField, () -> {

        });

        row();
        label = new Label("PACKAGE", skin);
        add(label);

        textField = new TextField("", skin);
        add(textField);
        addIbeamListener(textField);
        onChange(textField, () -> {

        });

        row();
        label = new Label("MAIN CLASS", skin);
        add(label);

        textField = new TextField("", skin);
        add(textField);
        addIbeamListener(textField);
        onChange(textField, () -> {

        });

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
