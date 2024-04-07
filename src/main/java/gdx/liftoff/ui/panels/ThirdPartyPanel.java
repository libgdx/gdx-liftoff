package gdx.liftoff.ui.panels;

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

        addThirdParty(prop.getProperty("anim8"), prop.getProperty("anim8Tip"), "https://github.com/tommyettinger/anim8-gdx", "animate");
        addThirdParty(prop.getProperty("artemisOdb"), prop.getProperty("artemisOdbTip"), "https://github.com/junkdog/artemis-odb", "ecs,entity component system");
        addThirdParty(prop.getProperty("autumn"), prop.getProperty("anim8Tip"), "https://github.com/crashinvaders/gdx-lml/tree/master/autumn", "dependency injection");
        addThirdParty(prop.getProperty("autumnMvc"), prop.getProperty("autumnMvcTip"), "https://github.com/crashinvaders/gdx-lml/tree/master/mvc", "dependency injection,model view controller,lml");
        addThirdParty(prop.getProperty("bladeInk"), prop.getProperty("bladeInkTip"), "https://github.com/bladecoder/blade-ink-java", "scripting language");
        addThirdParty(prop.getProperty("colorful"), prop.getProperty("colorfulTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("controllerMapping"), prop.getProperty("controllerMappingTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("controllerScene2D"), prop.getProperty("controllerScene2DTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("controllerUtils"), prop.getProperty("controllerUtilsTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("dialogs"), prop.getProperty("dialogsTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("digital"), prop.getProperty("digitalTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("facebook"), prop.getProperty("facebookTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("fleks"), prop.getProperty("fleksTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("formic"), prop.getProperty("formicTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("funderby"), prop.getProperty("funderbyTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("gdxBasisUniversal"), prop.getProperty("gdxBasisUniversalTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("flexBox"), prop.getProperty("flexBoxTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("gdxGltf"), prop.getProperty("gdxGltfTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("gdxPsx"), prop.getProperty("gdxPsxTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("gdxVfxCore"), prop.getProperty("gdxVfxCoreTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("gdxVfxEffects"), prop.getProperty("gdxVfxEffectsTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("guacamole"), prop.getProperty("guacamoleTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("h2d"), prop.getProperty("h2dTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("h2dSpineExtension"), prop.getProperty("h2dSpineExtensionTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("h2dTinyVGExtension"), prop.getProperty("h2dTinyVGExtensionTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("h2dTypingLabelExtension"), prop.getProperty("h2dTypingLabelExtensionTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("hackLights"), prop.getProperty("hackLightsTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("inGameConsole"), prop.getProperty("inGameConsoleTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("jaci"), prop.getProperty("jaciTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("jaciGwt"), prop.getProperty("jaciGwtTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("jbump"), prop.getProperty("jbumpTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("jdkgdxds"), prop.getProperty("jdkgdxdsTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("jdkgdxdsInterop"), prop.getProperty("jdkgdxdsInteropTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("joise"), prop.getProperty("joiseTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("juniper"), prop.getProperty("juniperTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("kiwi"), prop.getProperty("kiwiTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("kotlinxCoroutines"), prop.getProperty("kotlinxCoroutinesTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("kryo"), prop.getProperty("kryoTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("kryoDigital"), prop.getProperty("kryoDigitalTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("kryoJdkgdxds"), prop.getProperty("kryoJdkgdxdsTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("kryoJuniper"), prop.getProperty("kryoJuniperTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("kryoNet"), prop.getProperty("kryoNetTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("kryoRegExodus"), prop.getProperty("kryoRegExodusTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("ktxActors"), prop.getProperty("ktxActorsTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("ktxAi"), prop.getProperty("ktxAiTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("ktxApp"), prop.getProperty("ktxAppTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("ktxArtemis"), prop.getProperty("ktxArtemisTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("ktxAshley"), prop.getProperty("ktxAshleyTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("ktxAssets"), prop.getProperty("ktxAssetsTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("ktxAssetsAsync"), prop.getProperty("ktxAssetsAsyncTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("ktxAsync"), prop.getProperty("ktxAsyncTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("ktxBox2d"), prop.getProperty("ktxBox2dTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("ktxCollections"), prop.getProperty("ktxCollectionsTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("ktxFreetype"), prop.getProperty("ktxFreetypeTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("ktxFreetypeAsync"), prop.getProperty("ktxFreetypeAsyncTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("ktxGraphics"), prop.getProperty("ktxGraphicsTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("ktxI18n"), prop.getProperty("ktxI18nTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("ktxInject"), prop.getProperty("ktxInjectTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("ktxJson"), prop.getProperty("ktxJsonTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("ktxLog"), prop.getProperty("ktxLogTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("ktxMath"), prop.getProperty("ktxMathTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("ktxPreferences"), prop.getProperty("ktxPreferencesTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("ktxReflect"), prop.getProperty("ktxReflectTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("ktxScene2d"), prop.getProperty("ktxScene2dTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("ktxStyle"), prop.getProperty("ktxStyleTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("ktxTiled"), prop.getProperty("ktxTiledTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("ktxVis"), prop.getProperty("ktxVisTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("ktxVisStyle"), prop.getProperty("ktxVisStyleTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("libgdxOboe"), prop.getProperty("libgdxOboeTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("lml"), prop.getProperty("lmlTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("lmlVis"), prop.getProperty("lmlVisTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("lombok"), prop.getProperty("lombokTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("makeSomeNoise"), prop.getProperty("makeSomeNoiseTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("miniaudio"), prop.getProperty("miniaudioTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("noise4j"), prop.getProperty("noise4jTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("pieMenu"), prop.getProperty("pieMenuTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("regExodus"), prop.getProperty("regExodusTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("screenManager"), prop.getProperty("screenManagerTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("shapeDrawer"), prop.getProperty("shapeDrawerTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("simpleGraphs"), prop.getProperty("simpleGraphsTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("spineRuntime"), prop.getProperty("spineRuntimeTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("squidCore"), prop.getProperty("squidCoreTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("squidFreezeCore"), prop.getProperty("squidFreezeCoreTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("squidFreezeGrid"), prop.getProperty("squidFreezeGridTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("squidFreezeOld"), prop.getProperty("squidFreezeOldTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("squidFreezeText"), prop.getProperty("squidFreezeTextTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("squidGlyph"), prop.getProperty("squidGlyphTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("squidGrid"), prop.getProperty("squidGridTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("squidOld"), prop.getProperty("squidOldTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("squidPath"), prop.getProperty("squidPathTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("squidPlace"), prop.getProperty("squidPlaceTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("squidPress"), prop.getProperty("squidPressTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("squidSmooth"), prop.getProperty("squidSmoothTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("squidStoreCore"), prop.getProperty("squidStoreCoreTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("squidStoreGrid"), prop.getProperty("squidStoreGridTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("squidStoreOld"), prop.getProperty("squidStoreOldTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("squidStoreText"), prop.getProperty("squidStoreTextTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("squidText"), prop.getProperty("squidTextTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("squidWorld"), prop.getProperty("squidWorldTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("squidlib"), prop.getProperty("squidlibTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("squidlibExtra"), prop.getProperty("squidlibExtraTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("squidlibUtil"), prop.getProperty("squidlibUtilTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("stripe"), prop.getProperty("stripeTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("tenPatch"), prop.getProperty("tenPatchTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("textratypist"), prop.getProperty("textratypistTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("tinyVG"), prop.getProperty("tinyVGTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("tuningFork"), prop.getProperty("tuningForkTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("typingLabel"), prop.getProperty("typingLabelTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("universalTween"), prop.getProperty("universalTweenTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("utils"), prop.getProperty("utilsTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("utilsBox2d"), prop.getProperty("utilsBox2dTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("visUi"), prop.getProperty("visUiTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("websocket"), prop.getProperty("websocketTip"), "asdf", "asdf");
        addThirdParty(prop.getProperty("websocketSerialization"), prop.getProperty("websocketSerializationTip"), "asdf", "asdf");

        populateScrollTable(null);
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
                (!searchEntry.name.toLowerCase(Locale.ROOT).contains(search))) continue;

            scrollTable.row();
            CheckBox checkBox = new CheckBox(searchEntry.name, skin);
            checkBox.getLabel().setWrap(true);
            checkBox.getLabelCell().growX();
            scrollTable.add(checkBox).left().growX().maxWidth(200);
            addHandListener(checkBox);

            Label label = new Label(searchEntry.description, skin, "description");
            label.setWrap(true);
            scrollTable.add(label).growX().align(Align.left);
            addLabelHighlight(checkBox, label);

            Button button = new Button(skin, "external-link");
            scrollTable.add(button);
            addHandListener(button);
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
