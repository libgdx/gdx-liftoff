package gdx.liftoff.ui.panels;

import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.PopTable.TableShowHideListener;
import gdx.liftoff.ui.dialogs.*;

import static gdx.liftoff.Main.*;

/**
 * A table to display the project settings
 */
public class SettingsPanel extends Table implements Panel {
    private Actor keyboardFocus;

    public SettingsPanel() {
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
        addField(prop.getProperty("gdxVersion"), prop.getProperty("gdxVersionTip"), table, true);

        //java version
        addField(prop.getProperty("javaVersion"), prop.getProperty("javaVersionTip"), table);

        //application version
        addField(prop.getProperty("version"), prop.getProperty("versionTip"), table);

        //robovm version
        addField(prop.getProperty("robovmVersion"), prop.getProperty("robovmVersionTip"), table);

        //add gui assets
        table.defaults().spaceTop(SPACE_MEDIUM);
        addCheck(prop.getProperty("generateSkin"), prop.getProperty("generateSkinTip"), table);

        //add readme
        addCheck(prop.getProperty("generateReadme"), prop.getProperty("generateReadmeTip"), table);

        //add gradle tasks
        row();
        TextButton textButton = new TextButton(prop.getProperty("gradleTasksButton"), skin);
        add(textButton).spaceTop(SPACE_HUGE);
        addHandListener(textButton);
        onChange(textButton, () -> {
            PopTable pop = GradleDialog.show();
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

    private void addField(String text, String tip, Table table) {
        addField(text, tip, table, false);
    }

    /**
     * Convenience method to add a text field setting
     * @param text
     * @param tip
     * @param table
     * @param setKeyboardFocus
     */
    private void addField(String text, String tip, Table table, boolean setKeyboardFocus) {
        table.row();
        Label label = new Label(text, skin, "field");
        label.setTouchable(Touchable.enabled);
        label.setEllipsis("...");
        table.add(label).minWidth(0);

        TextField textField = new TextField("", skin);
        table.add(textField);
        addTooltip(textField, label, Align.top, TOOLTIP_WIDTH, tip);
        addIbeamListener(textField);
        addLabelHighlight(textField, label, false);
        if (setKeyboardFocus) keyboardFocus = textField;
    }

    /**
     * Convenience method to add a check mark setting
     * @param text
     * @param tip
     * @param table
     */
    private void addCheck(String text, String tip, Table table) {
        table.row();
        Label label = new Label(text, skin, "field");
        label.setTouchable(Touchable.enabled);
        label.setEllipsis("...");
        table.add(label).minWidth(0);

        ImageButton imageButton = new ImageButton(skin, "check");
        imageButton.left();
        table.add(imageButton);
        addTooltip(imageButton, label, Align.top, TOOLTIP_WIDTH, tip);
        addHandListener(imageButton);
        addLabelHighlight(imageButton, label);
    }

    @Override
    public void captureKeyboardFocus() {
        stage.setKeyboardFocus(keyboardFocus);
    }
}
