package gdx.liftoff.ui.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import gdx.liftoff.Main;

import java.util.Locale;

import static gdx.liftoff.Main.*;

public class ThirdPartyPanel extends Table implements Panel {
    private Actor keyboardFocus;
    private Array<SearchEntry> searchEntries = new Array<>();
    private Table scrollTable;

    public ThirdPartyPanel() {
        Label label = new Label(prop.getProperty("thirdParty"), skin, "header");
        add(label).space(10);

        row();
        label = new Label(prop.getProperty("thirdPartyWarn"), skin, "description");
        label.setEllipsis("...");
        add(label).minWidth(0);

        row();
        TextField textField = new TextField("", skin, "search");
        add(textField).growX().spaceTop(30);
        addIbeamListener(textField);
        keyboardFocus = textField;
        onChange(textField, () -> populateScrollTable(textField.getText()));

        //Third Party Extensions
        row();
        Table table = new Table();
        table.setBackground(skin.getDrawable("button-outline-up-10"));
        add(table).grow().spaceTop(20);

        table.row();
        scrollTable = new Table();
        scrollTable.top();
        scrollTable.defaults().space(10).top();
        ScrollPane scrollPane = new ScrollPane(scrollTable, skin);
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        table.add(scrollPane).grow();
        addScrollFocusListener(scrollPane);

        addThirdParty("anim8");
        addThirdParty("artemisOdb");
        addThirdParty("autumn");
        addThirdParty("autumnMvc");
        addThirdParty("bladeInk");
        addThirdParty("colorful");
        addThirdParty("controllerMapping");
        addThirdParty("controllerScene2D");
        addThirdParty("controllerUtils");
        addThirdParty("dialogs");
        addThirdParty("digital");
        addThirdParty("facebook");
        addThirdParty("fleks");
        addThirdParty("formic");
        addThirdParty("funderby");
        addThirdParty("gdxBasisUniversal");
        addThirdParty("flexBox");
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
        addThirdParty("tenPatch");
        addThirdParty("textratypist");
        addThirdParty("tinyVG");
        addThirdParty("tuningFork");
        addThirdParty("typingLabel");
        addThirdParty("universalTween");
        addThirdParty("utils");
        addThirdParty("utilsBox2d");
        addThirdParty("visUi");
        addThirdParty("websocket");
        addThirdParty("websocketSerialization");

        populateScrollTable(null);
    }

    private SearchEntry addThirdParty(String name) {
        return addThirdParty(prop.getProperty(name), prop.getProperty(name + "Tip"), prop.getProperty(name + "Url"),  prop.getProperty(name + "Terms"));
    }

    private SearchEntry addThirdParty(String name, String description, String link, String keywords) {
        SearchEntry searchEntry = new SearchEntry(name, description, link, keywords);
        searchEntries.add(searchEntry);
        return searchEntry;
    }

    private void populateScrollTable(String search) {
        if (search != null) search = search.toLowerCase(Locale.ROOT).replaceAll("\\W", "");
        System.out.println(search);
        scrollTable.clearChildren();

        for (SearchEntry searchEntry : searchEntries) {
            if (search != null && !search.equals("") &&
                !searchEntry.name.toLowerCase(Locale.ROOT).contains(search) &&
                !searchEntry.keywords.toLowerCase(Locale.ROOT).contains(search)) continue;

            scrollTable.row();
            CheckBox checkBox = new CheckBox(searchEntry.name, skin);
            checkBox.getLabel().setWrap(true);
            checkBox.getLabelCell().growX().maxWidth(200);
            scrollTable.add(checkBox).left().growX();
            addHandListener(checkBox);

            Label label = new Label(searchEntry.description, skin, "description");
            label.setWrap(true);
            scrollTable.add(label).growX().align(Align.left);
            addLabelHighlight(checkBox, label);

            Button button = new Button(skin, "external-link");
            scrollTable.add(button).padRight(5);
            addHandListener(button);
            onChange(button, () -> {
                System.out.println("searchEntry.link = " + searchEntry.link);
                Gdx.net.openURI(searchEntry.link);
            });
        }
    }

    public void captureKeyboardFocus() {
        stage.setKeyboardFocus(keyboardFocus);
    }

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
