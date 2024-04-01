package gdx.liftoff.ui.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import gdx.liftoff.Main;

import static gdx.liftoff.Main.*;
import static gdx.liftoff.ui.data.Data.*;

public class ProjectPanel extends Table  {
    private static final float TOOLTIP_WIDTH = 300;
    private Actor keyboardActor;

    public ProjectPanel() {
        columnDefaults(0).right();
        columnDefaults(1).growX();
        defaults().space(10);

        Label label = new Label("PROJECT NAME", skin);
        add(label);
        addTooltip(label, Align.top, TOOLTIP_WIDTH, projectNameTooltipDescription);

        TextField textField = new TextField("", skin);
        keyboardActor = textField;
        add(textField);
        addIbeamListener(textField);
        addTooltip(textField, label, Align.top, TOOLTIP_WIDTH, projectNameTooltipDescription);
        onChange(textField, () -> {

        });

        row();
        label = new Label("PACKAGE", skin);
        add(label);
        addTooltip(label, Align.top, TOOLTIP_WIDTH, packageTooltipDescription);

        textField = new TextField("", skin);
        add(textField);
        addIbeamListener(textField);
        addTooltip(textField, label, Align.top, TOOLTIP_WIDTH, packageTooltipDescription);
        onChange(textField, () -> {

        });

        row();
        label = new Label("MAIN CLASS", skin);
        add(label);
        addTooltip(label, Align.top, TOOLTIP_WIDTH, mainClassTooltipDescription);

        textField = new TextField("", skin);
        add(textField);
        addIbeamListener(textField);
        addTooltip(textField, label, Align.top, TOOLTIP_WIDTH, mainClassTooltipDescription);
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
