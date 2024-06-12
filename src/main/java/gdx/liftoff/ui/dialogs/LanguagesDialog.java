package gdx.liftoff.ui.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.ray3k.stripe.CollapsibleGroup;
import com.ray3k.stripe.CollapsibleGroup.CollapseType;
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.ScaleContainer;
import gdx.liftoff.ui.UserData;

import static gdx.liftoff.Main.*;

/**
 * Dialog shown when the user clicks the languages list in the add-ons panel
 */
public class LanguagesDialog extends PopTable {
    public LanguagesDialog(boolean fullscreen) {
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
        Label label = new Label(prop.getProperty("languages"), skin, "header");
        contentTable.add(label);

        //scrollable area includes languages and links
        contentTable.row();
        Table scrollTable = new Table();
        scrollTable.pad(SPACE_SMALL);
        ScrollPane scrollPane = new ScrollPane(scrollTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFlickScroll(false);
        contentTable.add(scrollPane).grow().spaceTop(SPACE_LARGE);
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
        addLanguage(table, "groovy", prop.getProperty("groovyDefaultVersion"));
        addLanguage(table, "kotlin", prop.getProperty("kotlinDefaultVersion"));
        addLanguage(table, "scala", prop.getProperty("scalaDefaultVersion"));

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
        contentTable.row();
        textButton = new TextButton("OK", skin);
        contentTable.add(textButton).prefWidth(140).spaceTop(SPACE_LARGE);
        addHandListener(textButton);
        onChange(textButton, this::hide);
    }

    /**
     * Convenience method to add a language to the table
     *
     * @param table        The table to add widgets to
     * @param languageName The name of the language
     */
    private void addLanguage(Table table, String languageName, String defaultVersion) {
        table.row();
        String localName = prop.getProperty(languageName);
        CheckBox checkBox = new CheckBox(localName, skin);
        checkBox.setChecked(UserData.languages.contains(languageName));
        table.add(checkBox);
        addHandListener(checkBox);

        TextField textField = new TextField(defaultVersion, skin);
        if (UserData.languageVersions.containsKey(languageName)) {
            textField.setText(UserData.languageVersions.get(languageName));
        }
        textField.setAlignment(Align.center);
        table.add(textField);
        addIbeamListener(textField);

        onChange(checkBox, () -> {
            if (checkBox.isChecked()) {
                UserData.languages.add(languageName);
                UserData.languageVersions.put(languageName, textField.getText());
            } else UserData.languages.remove(languageName);
            pref.putString("Languages", String.join(",", UserData.languages));
            pref.putString("LanguageVersions", String.join(",", UserData.languageVersions.values()));
            pref.flush();

        });

        onChange(textField, () -> {
            UserData.languageVersions.put(languageName, textField.getText());
        });

        Button button = new Button(skin, "external-link");
        table.add(button);
        addHandListener(button);
        onChange(button, () -> Gdx.net.openURI(prop.getProperty(languageName + "Url")));
    }

    public static void show(boolean fullscreen, Runnable onHideRunnable) {
        LanguagesDialog dialog = new LanguagesDialog(fullscreen);
        dialog.setFillParent(fullscreen);
        dialog.addListener(new PopTable.TableShowHideListener() {
            @Override
            public void tableHidden(Event event) {
                onHideRunnable.run();
            }

            @Override
            public void tableShown(Event event) {

            }
        });
        dialog.show(stage);
    }
}
