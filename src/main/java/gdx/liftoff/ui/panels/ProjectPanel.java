package gdx.liftoff.ui.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import gdx.liftoff.Main;
import gdx.liftoff.ui.data.UserData;

import static gdx.liftoff.Main.*;

/**
 * A table including fields for project name, package, and main class. A label displaying an applicable error message is
 * shown below.
 */
public class ProjectPanel extends Table implements Panel {
    private TextField keyboardActor;

    public ProjectPanel(boolean fullscreen) {
        populate(fullscreen);
    }

    public void populate(boolean fullscreen) {
        clearChildren();
        columnDefaults(0).right();
        columnDefaults(1).growX();
        defaults().space(SPACE_MEDIUM);

        //project label
        Label label = new Label(prop.getProperty("project"), skin);
        add(label);
        addTooltip(label, Align.top, TOOLTIP_WIDTH_LARGE, prop.getProperty("nameTip"));

        //project field
        final TextField projectTextField = new TextField("", skin);
        projectTextField.setText(UserData.projectName);
        keyboardActor = projectTextField;
        add(projectTextField);
        addIbeamListener(projectTextField);
        addTooltip(projectTextField, label, Align.top, TOOLTIP_WIDTH_LARGE, prop.getProperty("nameTip"));
        onChange(projectTextField, () -> {
            UserData.projectName = projectTextField.getText();
            pref.putString("Name", projectTextField.getText());
            pref.flush();
        });

        //package label
        row();
        label = new Label(prop.getProperty("package"), skin);
        add(label);
        addTooltip(label, Align.top, TOOLTIP_WIDTH_LARGE, prop.getProperty("packageTip"));

        //package field
        final TextField packageTextField = new TextField("", skin);
        packageTextField.setText(UserData.packageName);
        add(packageTextField);
        addIbeamListener(packageTextField);
        addTooltip(packageTextField, label, Align.top, TOOLTIP_WIDTH_LARGE, prop.getProperty("packageTip"));
        onChange(packageTextField, () -> {
            UserData.packageName = packageTextField.getText();
            pref.putString("Package", packageTextField.getText());
            pref.flush();
        });

        //main class label
        row();
        label = new Label(prop.getProperty("mainClass"), skin);
        add(label);
        addTooltip(label, Align.top, TOOLTIP_WIDTH_LARGE, prop.getProperty("classTip"));

        //main class field
        final TextField mainTextField = new TextField("", skin);
        mainTextField.setText(UserData.mainClassName);
        add(mainTextField);
        addIbeamListener(mainTextField);
        addTooltip(mainTextField, label, Align.top, TOOLTIP_WIDTH_LARGE, prop.getProperty("classTip"));
        onChange(mainTextField, () -> {
            UserData.mainClassName = mainTextField.getText();
            pref.putString("MainClass", mainTextField.getText());
            pref.flush();
        });

        //error label
        columnDefaults(0).reset();
        row();
        Label errorLabel = new Label("Project name cannot be empty", skin, "error");
        errorLabel.setEllipsis("...");
        add(errorLabel).colspan(2).minWidth(0);

        ChangeListener changeListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (1+1 == 2) {
                    //todo:update error label based on text entered in the fields
                    errorLabel.setText("something");
                }
            }
        };

        projectTextField.addListener(changeListener);
        packageTextField.addListener(changeListener);
        mainTextField.addListener(changeListener);
    }

    public void captureKeyboardFocus() {
        Main.stage.setKeyboardFocus(keyboardActor);
        keyboardActor.selectAll();
    }
}
