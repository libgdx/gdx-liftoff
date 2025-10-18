package gdx.liftoff.ui.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.github.tommyettinger.textra.TypingLabel;
import gdx.liftoff.Main;
import gdx.liftoff.ui.UserData;
import gdx.liftoff.ui.dialogs.FullscreenDialog;

import static gdx.liftoff.Main.*;

/**
 * A table including fields for project name, package, and main class. A label displaying an applicable error message is
 * shown below.
 */
public class ProjectPanel extends Table implements Panel {
    private TextField keyboardActor;
    private TypingLabel errorLabel;

    public ProjectPanel(boolean fullscreen) {
        populate(fullscreen);
    }

    public void populate(boolean fullscreen) {
        clearChildren();

        Table table = new Table();
        add(table).growX();

        table.columnDefaults(0).right();
        table.columnDefaults(1).growX();
        table.defaults().space(SPACE_MEDIUM);

        //project label
        Label label = new Label(prop.getProperty("projectName"), skin);
        table.add(label);
        addTooltip(label, Align.top, TOOLTIP_WIDTH_LARGE, prop.getProperty("nameTip"));

        //project field
        final TextField projectTextField = new TextField("", skin);
        projectTextField.setText(UserData.projectName);
        keyboardActor = projectTextField;
        table.add(projectTextField);
        addIbeamListener(projectTextField);
        addTooltip(projectTextField, label, Align.top, TOOLTIP_WIDTH_LARGE, prop.getProperty("nameTip"));
        onChange(projectTextField, () -> {
            UserData.projectName = projectTextField.getText();
            pref.putString("Name", projectTextField.getText());
            flushPref();
        });

        //package label
        table.row();
        label = new Label(prop.getProperty("packageName"), skin);
        table.add(label);
        addTooltip(label, Align.top, TOOLTIP_WIDTH_LARGE, prop.getProperty("packageTip"));

        //package field
        final TextField packageTextField = new TextField("", skin);
        packageTextField.setText(UserData.packageName);
        table.add(packageTextField);
        addIbeamListener(packageTextField);
        addTooltip(packageTextField, label, Align.top, TOOLTIP_WIDTH_LARGE, prop.getProperty("packageTip"));
        onChange(packageTextField, () -> {
            UserData.packageName = packageTextField.getText();
            pref.putString("Package", packageTextField.getText());
            flushPref();
        });

        //main class label
        table.row();
        label = new Label(prop.getProperty("mainClassName"), skin);
        table.add(label);
        addTooltip(label, Align.top, TOOLTIP_WIDTH_LARGE, prop.getProperty("classTip"));

        //main class field
        final TextField mainTextField = new TextField("", skin);
        mainTextField.setText(UserData.mainClassName);
        table.add(mainTextField);
        addIbeamListener(mainTextField);
        addTooltip(mainTextField, label, Align.top, TOOLTIP_WIDTH_LARGE, prop.getProperty("classTip"));
        onChange(mainTextField, () -> {
            UserData.mainClassName = mainTextField.getText();
            pref.putString("MainClass", mainTextField.getText());
            flushPref();
        });

        //error label
        row();
        errorLabel = new TypingLabel("", skin, "error");
        errorLabel.setWrap(true);
        errorLabel.setAlignment(Align.top);
        add(errorLabel).growX().spaceTop(SPACE_MEDIUM).minHeight(40);

        updateError();

        ChangeListener changeListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateError();
                if (FullscreenDialog.fullscreenDialog != null) FullscreenDialog.fullscreenDialog.updateGenerateButtons();
                root.landingTable.updateOptionsButtons();
            }
        };
        projectTextField.addListener(changeListener);
        packageTextField.addListener(changeListener);
        mainTextField.addListener(changeListener);
    }

    private void updateError() {
        if (!isValidProjectName(UserData.projectName))
            errorLabel.restart(String.format(prop.getProperty("nameNotValid")));
        else if (UserData.packageName.isEmpty())
            errorLabel.restart(String.format(prop.getProperty("notEmpty"), prop.getProperty("package")));
        else if (!isValidPackageName(UserData.packageName))
            errorLabel.restart(prop.getProperty("packageNotValid"));
        else if (UserData.mainClassName.isEmpty())
            errorLabel.restart(String.format(prop.getProperty("notEmpty"), prop.getProperty("class")));
        else if (!isValidClassName(UserData.mainClassName))
            errorLabel.restart(prop.getProperty("classNotValid"));
        else
            errorLabel.restart("");
        errorLabel.skipToTheEnd();
    }

    public void captureKeyboardFocus() {
        Main.stage.setKeyboardFocus(keyboardActor);
        keyboardActor.selectAll();
    }
}
