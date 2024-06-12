package gdx.liftoff.ui.panels;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.PopTable.TableShowHideListener;
import gdx.liftoff.ui.UserData;
import gdx.liftoff.ui.dialogs.GradleDialog;

import static gdx.liftoff.Main.*;

/**
 * A table to display the project settings
 */
public class SettingsPanel extends Table implements Panel {
    private TextField keyboardFocus;

    public SettingsPanel(boolean fullscreen) {
        populate(fullscreen);
    }

    public void populate(boolean fullscreen) {
        //title
        Label label = new Label(prop.getProperty("advanced"), skin, "header");
        add(label).space(SPACE_HUGE);

        row();
        Table table = new Table();
        add(table).growX();

        //libgdx version
        table.columnDefaults(0).right().expandX();
        table.columnDefaults(1).expandX().left().prefWidth(100).minWidth(50);
        table.defaults().spaceTop(SPACE_SMALL).spaceLeft(SPACE_MEDIUM);
        TextField libgdxTextField = addField(prop.getProperty("gdxVersion"), String.format(prop.getProperty("gdxVersionTip"), prop.getProperty("libgdxDefaultVersion")), UserData.libgdxVersion, table, true);
        onChange(libgdxTextField, () -> UserData.libgdxVersion = libgdxTextField.getText());

        //java version
        TextField javaTextField = addField(prop.getProperty("javaVersion"), prop.getProperty("javaVersionTip"), UserData.javaVersion, table);
        onChange(javaTextField, () -> UserData.libgdxVersion = javaTextField.getText());

        //application version
        TextField applicationTextField = addField(prop.getProperty("version"), prop.getProperty("versionTip"), UserData.appVersion, table);
        onChange(applicationTextField, () -> UserData.libgdxVersion = applicationTextField.getText());

        //robovm version
        if (UserData.platforms.contains("ios")) {
            TextField robovmTextField = addField(prop.getProperty("robovmVersion"),
                prop.getProperty("robovmVersionTip"), UserData.robovmVersion, table);
            onChange(robovmTextField, () -> UserData.libgdxVersion = robovmTextField.getText());
        }

        //add gui assets
        table.defaults().spaceTop(SPACE_MEDIUM);
        ImageButton guiImageButton = addCheck(prop.getProperty("generateSkin"), prop.getProperty("generateSkinTip"), UserData.addGuiAssets, table);
        onChange(guiImageButton, () -> UserData.addGuiAssets = guiImageButton.isChecked());

        //add readme
        ImageButton readmeImageButton = addCheck(prop.getProperty("generateReadme"), prop.getProperty("generateReadmeTip"), UserData.addReadme, table);
        onChange(readmeImageButton, () -> UserData.addReadme = readmeImageButton.isChecked());

        //add gradle tasks
        row();
        TextButton textButton = new TextButton(prop.getProperty("gradleTasksButton"), skin);
        add(textButton).spaceTop(SPACE_HUGE);
        addHandListener(textButton);
        addTooltip(textButton, Align.top, prop.getProperty("gradleTasksTipShort"));
        onChange(textButton, () -> {
            PopTable pop = GradleDialog.show(fullscreen);
            pop.addListener(new TableShowHideListener() {
                @Override
                public void tableShown(Event event) {

                }

                @Override
                public void tableHidden(Event event) {
                    captureKeyboardFocus();
                }
            });
        });
    }

    private TextField addField(String text, String tip, String version, Table table) {
        return addField(text, tip, version, table, false);
    }

    /**
     * Convenience method to add a text field setting
     *
     * @param text             The name of the setting
     * @param tip              A short description of the setting
     * @param table            The table to add this setting to
     * @param setKeyboardFocus True indicates that this textfield should take keyboard focus when the screen is switched
     */
    private TextField addField(String text, String tip, String version, Table table, boolean setKeyboardFocus) {
        table.row();
        Label label = new Label(text, skin, "field");
        label.setTouchable(Touchable.enabled);
        label.setEllipsis("...");
        table.add(label).minWidth(0);

        TextField textField = new TextField("", skin);
        textField.setText(version);
        table.add(textField);
        addTooltip(textField, label, Align.top, TOOLTIP_WIDTH, tip);
        addIbeamListener(textField);
        addLabelHighlight(textField, label, false);
        if (setKeyboardFocus) keyboardFocus = textField;
        return textField;
    }

    /**
     * Convenience method to add a check mark setting
     *
     * @param text  The name of the setting
     * @param tip   A short description of the setting
     * @param table The table to add this setting to
     */
    private ImageButton addCheck(String text, String tip, Boolean checked, Table table) {
        table.row();
        Label label = new Label(text, skin, "field");
        label.setTouchable(Touchable.enabled);
        label.setEllipsis("...");
        table.add(label).minWidth(0);

        ImageButton imageButton = new ImageButton(skin, "check");
        imageButton.left();
        imageButton.setChecked(checked);
        table.add(imageButton);
        addTooltip(imageButton, label, Align.top, TOOLTIP_WIDTH, tip);
        addHandListener(imageButton);
        addLabelHighlight(imageButton, label);

        return imageButton;
    }

    @Override
    public void captureKeyboardFocus() {
        stage.setKeyboardFocus(keyboardFocus);
        keyboardFocus.selectAll();
    }
}
