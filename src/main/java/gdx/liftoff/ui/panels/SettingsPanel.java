package gdx.liftoff.ui.panels;

import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.PopTable.TableShowHideListener;
import gdx.liftoff.ui.dialogs.*;

import static gdx.liftoff.Main.*;

public class SettingsPanel extends Table implements Panel {
    private Actor keyboardFocus;

    public SettingsPanel() {
        Label label = new Label(prop.getProperty("advanced"), skin, "header");
        add(label).space(30);

        row();
        Table table = new Table();
        add(table).grow();

        //libgdx version
        table.columnDefaults(0).right().expandX();
        table.columnDefaults(1).expandX().left().prefWidth(100).minWidth(50);
        table.defaults().spaceTop(5).spaceLeft(10);
        addField(prop.getProperty("gdxVersion"), prop.getProperty("gdxVersionTip"), table, true);

        //java version
        addField(prop.getProperty("javaVersion"), prop.getProperty("javaVersionTip"), table);

        //application version
        addField(prop.getProperty("version"), prop.getProperty("versionTip"), table);

        //robovm version
        addField(prop.getProperty("robovmVersion"), prop.getProperty("robovmVersionTip"), table);

        //add gui assets
        table.defaults().spaceTop(10);
        addCheck(prop.getProperty("generateSkin"), prop.getProperty("generateSkinTip"), table);

        //add readme
        addCheck(prop.getProperty("generateReadme"), prop.getProperty("generateReadmeTip"), table);

        //add gradle tasks
        row();
        TextButton textButton = new TextButton(prop.getProperty("gradleTasksButton"), skin);
        add(textButton).spaceTop(30);
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

    private void addField(String text, String tip, Table table, boolean setKeyboardFocus) {
        table.row();
        Label label = new Label(text, skin, "field");
        label.setTouchable(Touchable.enabled);
        label.setEllipsis("...");
        table.add(label).minWidth(0);

        TextField textField = new TextField("", skin);
        table.add(textField);
        addTooltip(textField, label, Align.top, 200, tip);
        addIbeamListener(textField);
        addLabelHighlight(textField, label, false);
        if (setKeyboardFocus) keyboardFocus = textField;
    }

    private void addCheck(String text, String tip, Table table) {
        table.row();
        Label label = new Label(text, skin, "field");
        label.setTouchable(Touchable.enabled);
        label.setEllipsis("...");
        table.add(label).minWidth(0);

        ImageButton imageButton = new ImageButton(skin, "check");
        imageButton.left();
        table.add(imageButton);
        addTooltip(imageButton, label, Align.top, 200, tip);
        addHandListener(imageButton);
        addLabelHighlight(imageButton, label);
    }

    @Override
    public void captureKeyboardFocus() {
        stage.setKeyboardFocus(keyboardFocus);
    }
}
