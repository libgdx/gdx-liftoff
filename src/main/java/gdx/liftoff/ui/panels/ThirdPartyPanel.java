package gdx.liftoff.ui.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import gdx.liftoff.ui.UserData;

import java.util.Locale;

import static gdx.liftoff.Main.*;

/**
 * A table with a searchable list of third party libraries
 */
public class ThirdPartyPanel extends Table implements Panel {
    private Actor keyboardFocus;
    private Array<SearchEntry> searchEntries = new Array<>();
    private Table scrollTable;

    public ThirdPartyPanel(boolean fullscreen) {
        populate(fullscreen);
    }

    public void populate(boolean fullscreen) {
        clearChildren();
        //title
        Label label = new Label(prop.getProperty("thirdParty"), skin, "header");
        add(label).space(10);

        //subtitle
        row();
        label = new Label(prop.getProperty("thirdPartyWarn"), skin, "description");
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
        addThirdParty("anim8");
        addThirdParty("artemisOdb");
        addThirdParty("autumn");
        addThirdParty("autumnMvc");
        addThirdParty("bladeInk");
        addThirdParty("colorful");
        addThirdParty("commonsCollections");
        addThirdParty("controllerMapping");
        addThirdParty("controllerScene2D");
        addThirdParty("controllerUtils");
        addThirdParty("cringe");
        addThirdParty("crux");
        addThirdParty("dialogs");
        addThirdParty("digital");
        addThirdParty("facebook");
        addThirdParty("fleks");
        addThirdParty("flexBox");
        addThirdParty("formic");
        addThirdParty("funderby");
        addThirdParty("fury");
        addThirdParty("gand");
        addThirdParty("gdxBasisUniversal");
        addThirdParty("gdxGltf");
        addThirdParty("gdxPsx");
        addThirdParty("gdxVfxCore");
        addThirdParty("gdxVfxEffects");
        addThirdParty("guacamole");
        addThirdParty("h2d");
        addThirdParty("h2dSpineExtension");
        addThirdParty("h2dTinyVGExtension");
        addThirdParty("h2dTypingLabelExtension");
        addThirdParty("hackLights");
        addThirdParty("inGameConsole");
        addThirdParty("jaci");
        addThirdParty("jaciGwt");
        addThirdParty("jbump");
        addThirdParty("jdkgdxds");
        addThirdParty("jdkgdxdsInterop");
        addThirdParty("joise");
        addThirdParty("juniper");
        addThirdParty("kiwi");
        addThirdParty("kotlinxCoroutines");
        addThirdParty("kryo");
        addThirdParty("kryoCringe");
        addThirdParty("kryoDigital");
        addThirdParty("kryoJdkgdxds");
        addThirdParty("kryoJuniper");
        addThirdParty("kryoNet");
        addThirdParty("kryoRegExodus");
        addThirdParty("ktxActors");
        addThirdParty("ktxAi");
        addThirdParty("ktxApp");
        addThirdParty("ktxArtemis");
        addThirdParty("ktxAshley");
        addThirdParty("ktxAssets");
        addThirdParty("ktxAssetsAsync");
        addThirdParty("ktxAsync");
        addThirdParty("ktxBox2d");
        addThirdParty("ktxCollections");
        addThirdParty("ktxFreetype");
        addThirdParty("ktxFreetypeAsync");
        addThirdParty("ktxGraphics");
        addThirdParty("ktxI18n");
        addThirdParty("ktxInject");
        addThirdParty("ktxJson");
        addThirdParty("ktxLog");
        addThirdParty("ktxMath");
        addThirdParty("ktxPreferences");
        addThirdParty("ktxReflect");
        addThirdParty("ktxScene2d");
        addThirdParty("ktxStyle");
        addThirdParty("ktxTiled");
        addThirdParty("ktxVis");
        addThirdParty("ktxVisStyle");
        addThirdParty("libgdxOboe");
        addThirdParty("lml");
        addThirdParty("lmlVis");
        addThirdParty("lombok");
        addThirdParty("makeSomeNoise");
        addThirdParty("miniaudio");
        addThirdParty("noise4j");
        addThirdParty("pieMenu");
        addThirdParty("regExodus");
        addThirdParty("screenManager");
        addThirdParty("shapeDrawer");
        addThirdParty("simpleGraphs");
        addThirdParty("spineRuntime");
        addThirdParty("squidCore");
        addThirdParty("squidFreezeCore");
        addThirdParty("squidFreezeGrid");
        addThirdParty("squidFreezeOld");
        addThirdParty("squidFreezeText");
        addThirdParty("squidGlyph");
        addThirdParty("squidGrid");
        addThirdParty("squidOld");
        addThirdParty("squidPath");
        addThirdParty("squidPlace");
        addThirdParty("squidPress");
        addThirdParty("squidSmooth");
        addThirdParty("squidStoreCore");
        addThirdParty("squidStoreGrid");
        addThirdParty("squidStoreOld");
        addThirdParty("squidStoreText");
        addThirdParty("squidText");
        addThirdParty("squidWorld");
        addThirdParty("squidlib");
        addThirdParty("squidlibExtra");
        addThirdParty("squidlibUtil");
        addThirdParty("stripe");
        addThirdParty("tantrumDigital");
        addThirdParty("tantrumJdkgdxds");
        addThirdParty("tantrumJuniper");
        addThirdParty("tantrumLibgdx");
        addThirdParty("tantrumRegExodus");
        addThirdParty("tenPatch");
        addThirdParty("textratypist");
        addThirdParty("tinyVG");
        addThirdParty("tuningFork");
        addThirdParty("typingLabel");
        addThirdParty("unbox2d");
        addThirdParty("universalTween");
        addThirdParty("utils");
        addThirdParty("utilsBox2d");
        addThirdParty("visUi");
        addThirdParty("websocket");
        addThirdParty("websocketSerialization");

        populateScrollTable(null);

        row();
        table = new Table();
        add(table).spaceTop(30).growX();

        //links title
        table.defaults().space(5).expandX();
        label = new Label(prop.getProperty("links"), skin, "field");
        table.add(label).left();

        table.defaults().left().padLeft(10);
        table.row();

        //submit an extension
        TextButton textButton = new TextButton(prop.getProperty("thirdPartyLink"), skin, "link");
        textButton.getLabel().setAlignment(Align.left);
        table.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> Gdx.net.openURI(prop.getProperty("issues")));
    }

    private SearchEntry addThirdParty(String name) {
        return addThirdParty(prop.getProperty(name), prop.getProperty(name + "Tip"), prop.getProperty(name + "Url"),  prop.getProperty(name + "Terms"));
    }

    /**
     * Convenience method to add a third party library to the searchEntries list
     * @param name The name of the library
     * @param description A short description of the library
     * @param link The URL pointing to the library's home page
     * @param keywords Search terms to be implemented in the search TextField. The library name is already included with
     *                 the keywords.
     * @return The data object containing the details of the third-party library
     */
    private SearchEntry addThirdParty(String name, String description, String link, String keywords) {
        SearchEntry searchEntry = new SearchEntry(name, description, link, keywords);
        searchEntries.add(searchEntry);
        return searchEntry;
    }

    /**
     * Clears the table and adds each SearchEntry containing the search string as a checkbox with an associated
     * description and link
     * @param search
     */
    private void populateScrollTable(String search) {
        if (search != null) search = search.toLowerCase(Locale.ROOT).replaceAll("\\W", "");
        scrollTable.clearChildren();

        for (SearchEntry searchEntry : searchEntries) {
            if (search != null && !search.equals("") &&
                !searchEntry.name.toLowerCase(Locale.ROOT).contains(search) &&
                !searchEntry.keywords.toLowerCase(Locale.ROOT).contains(search)) continue;

            //entry checkbox
            scrollTable.row();
            CheckBox checkBox = new CheckBox(searchEntry.name, skin);
            checkBox.setChecked(UserData.thirdPartyLibs.contains(searchEntry.name, false));
            checkBox.getLabel().setWrap(true);
            checkBox.getLabelCell().growX().maxWidth(200);
            scrollTable.add(checkBox).left().growX();
            addHandListener(checkBox);

            //entry label
            Label label = new Label(searchEntry.description, skin, "description");
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

    public void captureKeyboardFocus() {
        stage.setKeyboardFocus(keyboardFocus);
    }

    /**
     * A data class to store the information for a third-party library
     */
    private class SearchEntry {
        String name;
        String description;
        String link;
        String keywords;

        public SearchEntry(String name, String description, String link, String keywords) {
            this.name = name;
            this.description = description;
            this.link = link;
            this.keywords = keywords;
        }
    }
}
