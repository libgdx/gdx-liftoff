package gdx.liftoff.ui.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.textra.TextraButton;
import com.github.tommyettinger.textra.TextraCheckBox;
import com.github.tommyettinger.textra.TextraLabel;
import gdx.liftoff.Listing;
import gdx.liftoff.NaturalTextComparator;
import gdx.liftoff.data.libraries.Library;
import gdx.liftoff.ui.UserData;

import java.util.Locale;

import static gdx.liftoff.Main.*;

/**
 * A table with a searchable list of third party libraries
 */
public class ThirdPartyPanel extends Table implements Panel {
    private Actor keyboardFocus;
    private final Array<SearchEntry> searchEntries = new Array<>();
    private Table scrollTable;
    private TextraCheckBox filterCheckBox;

    public ThirdPartyPanel(boolean fullscreen) {
        populate(fullscreen);
    }

    public void populate(boolean fullscreen) {
        clearChildren();
        //title
        TextraLabel label = new TextraLabel(prop.getProperty("thirdParty"), skin, "header");
        add(label).space(10);

        //subtitle
        row();
        label = new TextraLabel(prop.getProperty("thirdPartyWarn"), skin, "description");
        label.setEllipsis("...");
        add(label).minWidth(0);

        //search field
        row();
        TextField textField = new TextField("", skin, "search");
        add(textField).growX().spaceTop(30);
        addIbeamListener(textField);
        keyboardFocus = textField;
        onChange(textField, () -> populateScrollTable(textField.getText()));

        row();
        Table table = new Table();
        table.setBackground(skin.getDrawable("button-outline-up-10"));
        add(table).grow().spaceTop(20);

        //scrollable area includes all the third party libraries
        table.row();
        scrollTable = new Table();
        scrollTable.top();
        scrollTable.defaults().space(10).top();
        ScrollPane scrollPane = new ScrollPane(scrollTable, skin);
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        table.add(scrollPane).grow();
        addScrollFocusListener(scrollPane);

        //third party extensions
        for (Library lib : Listing.unofficialLibraries) {
            addThirdParty(lib.getId());
        }
        searchEntries.sort((a, b) -> NaturalTextComparator.CASE_INSENSITIVE.compare(a.name.replaceFirst("(?i)gdx([ -]?)", ""), b.name.replaceFirst("(?i)gdx([ -]?)", "")));

        //selected filter checkbox
        row();
        filterCheckBox = new TextraCheckBox("", skin);
        add(filterCheckBox).left().spaceTop(SPACE_SMALL);
        addHandListener(filterCheckBox);
        updateFilterCheckBox();
        onChange(filterCheckBox, () -> populateScrollTable(textField.getText()));

        populateScrollTable(null);

        row();
        table = new Table();
        add(table).spaceTop(30).growX();

        //links title
        table.defaults().space(5).expandX();
        label = new TextraLabel(prop.getProperty("links"), skin, "field");
        table.add(label).left();

        table.defaults().left().padLeft(10);
        table.row();

        //submit an extension
        TextraButton textButton = new TextraButton(prop.getProperty("thirdPartyLink"), skin, "link");
        textButton.getTextraLabel().setAlignment(Align.left);
        table.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> Gdx.net.openURI(prop.getProperty("issues")));
    }

    private SearchEntry addThirdParty(String name) {
        return addThirdParty(name, prop.getProperty(name, name), prop.getProperty(name + "Tip", "Unknown third-party extension."), prop.getProperty(name + "Url", ""), prop.getProperty(name + "Terms", "unknown"));
    }

    /**
     * Convenience method to add a third party library to the searchEntries list
     *
     * @param name        The name of the library
     * @param description A short description of the library
     * @param link        The URL pointing to the library's home page
     * @param keywords    Search terms to be implemented in the search TextField; the library name is already included with
     *                    the keywords
     * @return The data object containing the details of the third-party library
     */
    private SearchEntry addThirdParty(String id, String name, String description, String link, String keywords) {
        SearchEntry searchEntry = new SearchEntry(id, name, description, link, keywords);
        searchEntries.add(searchEntry);
        return searchEntry;
    }

    /**
     * Clears the table and adds each SearchEntry containing the search string as a checkbox with an associated
     * description and link
     *
     * @param search
     */
    private void populateScrollTable(String search) {
        boolean showOnlySelected = filterCheckBox.isChecked();
        if (search != null) search = search.toLowerCase(Locale.ROOT).replaceAll("\\W", "");
        scrollTable.clearChildren();

        for (SearchEntry searchEntry : searchEntries) {
            if (showOnlySelected && !UserData.thirdPartyLibs.contains(searchEntry.id)) continue;

            if (search != null && !search.isEmpty() &&
                !searchEntry.name.toLowerCase(Locale.ROOT).contains(search) &&
                !searchEntry.keywords.toLowerCase(Locale.ROOT).contains(search)) continue;

            //entry checkbox
            scrollTable.row();
            TextraCheckBox checkBox = new TextraCheckBox(searchEntry.name, skin);
            checkBox.setChecked(UserData.thirdPartyLibs.contains(searchEntry.id));
            checkBox.getTextraLabel().setWrap(true);
            checkBox.getTextraLabelCell().growX().maxWidth(200);
            scrollTable.add(checkBox).left().growX();
            onChange(checkBox, () -> {
                if (checkBox.isChecked() && !UserData.thirdPartyLibs.contains(searchEntry.id))
                    UserData.thirdPartyLibs.add(searchEntry.id);
                else UserData.thirdPartyLibs.remove(searchEntry.id);
                pref.putString("ThirdParty", String.join(",", UserData.thirdPartyLibs));
                flushPref();
                updateFilterCheckBox();
            });
            addHandListener(checkBox);

            //entry label
            TextraLabel label = new TextraLabel(searchEntry.description, skin, "description");
            label.setWrap(true);
            scrollTable.add(label).growX().align(Align.left);
            addLabelHighlight(checkBox, label);

            //entry link button
            Button button = new Button(skin, "external-link");
            scrollTable.add(button).padRight(SPACE_SMALL);
            addHandListener(button);
            onChange(button, () -> Gdx.net.openURI(searchEntry.link));
        }
    }

    private void updateFilterCheckBox() {
        filterCheckBox.setText(String.format("Show only selected (%d of %d)", UserData.thirdPartyLibs.size(), Listing.unofficialLibraries.size()));
    }

    public void captureKeyboardFocus() {
        stage.setKeyboardFocus(keyboardFocus);
    }

    /**
     * A data class to store the information for a third-party library
     */
    private static class SearchEntry {
        String id;
        String name;
        String description;
        String link;
        String keywords;

        public SearchEntry(String id, String name, String description, String link, String keywords) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.link = link;
            this.keywords = keywords;
        }
    }
}
