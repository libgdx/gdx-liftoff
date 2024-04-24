package gdx.liftoff.ui.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.Align;
import com.ray3k.stripe.PopTable;

import static gdx.liftoff.Main.*;
import static gdx.liftoff.ui.data.Data.*;

/**
 * Dialog shown when the user clicks the languages list in the add-ons panel
 */
public class LanguagesDialog extends PopTable  {
    public LanguagesDialog() {
        setStyle(skin.get("dialog", WindowStyle.class));
        setKeepCenteredInWindow(true);
        setHideOnUnfocus(true);
        pad(SPACE_LARGE).padTop(SPACE_HUGE).padBottom(SPACE_HUGE);

        //title
        Label label = new Label(prop.getProperty("languages"), skin, "header");
        add(label);

        //scrollable area includes languages and links
        row();
        Table scrollTable = new Table();
        scrollTable.pad(SPACE_SMALL);
        ScrollPane scrollPane = new ScrollPane(scrollTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFlickScroll(false);
        add(scrollPane).grow().spaceTop(SPACE_LARGE);
        addScrollFocusListener(scrollPane);
        stage.setScrollFocus(scrollPane);

        scrollTable.defaults().left();
        Table table = new Table();
        table.left();
        scrollTable.add(table).spaceTop(SPACE_MEDIUM).growX();

        //language label
        table.defaults().space(SPACE_MEDIUM);
        label = new Label(prop.getProperty("language"), skin, "field");
        table.add(label);

        //version label
        label = new Label(prop.getProperty("languageVersion"), skin, "field");
        table.add(label);

        table.columnDefaults(0).left();
        table.columnDefaults(1).width(175);

        //languages
        addLanguage(table, "groovy");
        addLanguage(table, "kotlin");
        addLanguage(table, "scala");

        //languages description
        scrollTable.row();
        label = new Label(prop.getProperty("languagesPrompt"), skin, "description");
        label.setWrap(true);
        label.setAlignment(Align.left);
        scrollTable.add(label).spaceTop(SPACE_HUGE).growX();

        //links
        scrollTable.row();
        table = new Table();
        scrollTable.add(table).spaceTop(SPACE_LARGE).growX();

        table.defaults().space(SPACE_SMALL);
        label = new Label(prop.getProperty("links"), skin, "field");
        table.add(label).left();

        //clojure
        table.defaults().left().padLeft(SPACE_MEDIUM);
        table.row();
        TextButton textButton = new TextButton(prop.getProperty("clojureLink"), skin, "link");
        textButton.getLabel().setAlignment(Align.left);
        table.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> Gdx.net.openURI(prop.getProperty("clojureUrl")));

        //other jvm's
        table.row();
        textButton = new TextButton(prop.getProperty("otherLanguagesPrompt"), skin, "link");
        table.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> Gdx.net.openURI(prop.getProperty("otherJvmUrl")));

        //ok button
        row();
        textButton = new TextButton("OK", skin);
        add(textButton).prefWidth(140).spaceTop(SPACE_LARGE);
        addHandListener(textButton);
        onChange(textButton, this::hide);
    }

    /**
     * Convenience method to add a language to the table
     * @param table
     * @param name
     */
    private void addLanguage(Table table, String name) {
        table.row();
        CheckBox checkBox = new CheckBox(prop.getProperty(name), skin);
        table.add(checkBox);
        addHandListener(checkBox);

        TextField textField = new TextField(groovyDefaultVersion, skin);
        textField.setAlignment(Align.center);
        table.add(textField);
        addIbeamListener(textField);

        Button button = new Button(skin, "external-link");
        table.add(button);
        addHandListener(button);
        onChange(button, () -> Gdx.net.openURI(prop.getProperty(name + "Url")));
    }

    public static void show() {
        LanguagesDialog dialog = new LanguagesDialog();
        dialog.show(stage);
    }
}
