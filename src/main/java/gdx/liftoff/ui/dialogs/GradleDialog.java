package gdx.liftoff.ui.dialogs;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.Scaling;
import com.github.tommyettinger.textra.TextraButton;
import com.github.tommyettinger.textra.TextraField;
import com.github.tommyettinger.textra.TextraLabel;
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
        TextraLabel label = new TextraLabel(prop.getProperty("gradleTasksPrompt"), skin, "field");
        label.setWrap(true);
        contentTable.add(label).growX();

        //explanation
        contentTable.row();
        label = new TextraLabel(prop.getProperty("gradleTasksTip"), skin, "description");
        label.setWrap(true);
        contentTable.add(label).growX().spaceTop(SPACE_LARGE);

        //gradle commands textfield
        contentTable.row();
        TextraField textField = new TextraField("", skin);
        contentTable.add(textField).width(350);
        addIbeamListener(textField);
        stage.setKeyboardFocus(textField);
        onChange(textField, () -> {
            UserData.gradleTasks = textField.getText();
            pref.putString("GradleTasks", textField.getText());
            flushPref();
        });

        //ok button
        contentTable.row();
        TextraButton textButton = new TextraButton("OK", skin);
        contentTable.add(textButton).prefWidth(140).spaceTop(SPACE_LARGE);
        addHandListener(textButton);
        onChange(textButton, this::hide);
        key(Keys.ENTER, this::hide);
        key(Keys.ESCAPE, this::hide);
    }

    public static PopTable show(boolean fullscreen) {
        GradleDialog dialog = new GradleDialog(fullscreen);
        dialog.setFillParent(fullscreen);
        dialog.show(stage);
        return dialog;
    }
}
