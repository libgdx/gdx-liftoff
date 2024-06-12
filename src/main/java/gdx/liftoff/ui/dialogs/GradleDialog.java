package gdx.liftoff.ui.dialogs;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.Scaling;
import com.ray3k.stripe.CollapsibleGroup;
import com.ray3k.stripe.CollapsibleGroup.CollapseType;
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.ScaleContainer;
import gdx.liftoff.ui.UserData;

import static gdx.liftoff.Main.*;

/**
 * Dialog displayed when users click the "Add Gradle Tasks" button in the settings panel
 */
public class GradleDialog extends PopTable {
    public GradleDialog(boolean fullscreen) {
        setStyle(skin.get("dialog", WindowStyle.class));
        setKeepCenteredInWindow(true);
        setHideOnUnfocus(true);

        if (fullscreen) {
            CollapsibleGroup collapsibleGroup = new CollapsibleGroup(CollapseType.BOTH);
            add(collapsibleGroup).grow();

            Table contentTable = new Table();
            populate(contentTable);

            Container<Table> container = new Container<>(contentTable);
            container.minSize(0, 0);
            collapsibleGroup.addActor(container);

            contentTable = new Table();
            populate(contentTable);

            ScaleContainer scaleContainer = new ScaleContainer(Scaling.fit, contentTable);
            scaleContainer.setMinSize(1920, 1080);
            scaleContainer.setPrefSize(1920, 1080);
            collapsibleGroup.addActor(scaleContainer);
        } else {
            Table contentTable = new Table();
            add(contentTable);
            populate(contentTable);
        }
    }

    private void populate(Table contentTable) {
        contentTable.pad(SPACE_LARGE).padTop(SPACE_HUGE).padBottom(SPACE_HUGE);

        //title
        Label label = new Label(prop.getProperty("gradleTasksPrompt"), skin, "field");
        label.setWrap(true);
        contentTable.add(label).growX();

        //explanation
        contentTable.row();
        label = new Label(prop.getProperty("gradleTasksTip"), skin, "description");
        label.setWrap(true);
        contentTable.add(label).growX().spaceTop(SPACE_LARGE);

        //gradle commands textfield
        contentTable.row();
        TextField textField = new TextField("", skin);
        contentTable.add(textField).width(350);
        addIbeamListener(textField);
        stage.setKeyboardFocus(textField);
        onChange(textField, () -> {
            UserData.gradleTasks = textField.getText();
            pref.putString("GradleTasks", textField.getText());
            pref.flush();
        });

        //ok button
        contentTable.row();
        TextButton textButton = new TextButton("OK", skin);
        contentTable.add(textButton).prefWidth(140).spaceTop(SPACE_LARGE);
        addHandListener(textButton);
        onChange(textButton, this::hide);
    }

    public static PopTable show(boolean fullscreen) {
        GradleDialog dialog = new GradleDialog(fullscreen);
        dialog.setFillParent(fullscreen);
        dialog.show(stage);
        return dialog;
    }
}
