package gdx.liftoff.ui.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.Align;
import com.ray3k.stripe.CollapsibleGroup;
import com.ray3k.stripe.PopTable;

import static gdx.liftoff.Main.*;

public class TemplatesDialog extends PopTable  {
    public TemplatesDialog() {
        setStyle(skin.get("dialog", WindowStyle.class));
        setKeepCenteredInWindow(true);
        setHideOnUnfocus(true);
        pad(20).padTop(30).padBottom(30);

        Label label = new Label(prop.getProperty("templates"), skin, "header");
        add(label);

        row();
        Table scrollTable = new Table();
        scrollTable.pad(5);
        ScrollPane scrollPane = new ScrollPane(scrollTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFlickScroll(false);
        add(scrollPane).grow().spaceTop(20);
        addScrollFocusListener(scrollPane);
        stage.setScrollFocus(scrollPane);

        scrollTable.defaults().left();
        Table table = new Table();
        table.left();
        scrollTable.add(table).spaceTop(10).growX();

        table.defaults().left().space(5);
        ButtonGroup buttonGroup = new ButtonGroup();

        table.row();
        label = new Label(prop.getProperty("officialTemplates"), skin, "field");
        label.setTouchable(Touchable.enabled);
        table.add(label).minWidth(0).spaceBottom(10).colspan(2).growX();
        addTooltip(label, Align.top, prop.getProperty("officialTemplatesTip"));

        addTemplate(table, buttonGroup, prop.getProperty("classic"), prop.getProperty("classicTip"));
        addTemplate(table, buttonGroup, prop.getProperty("applicationAdapter"), prop.getProperty("applicationAdapterTip"));
        addTemplate(table, buttonGroup, prop.getProperty("applicationListener"), prop.getProperty("applicationListenerTip"));
        addTemplate(table, buttonGroup, prop.getProperty("emptyTemplate"), prop.getProperty("emptyTemplateTip"));
        addTemplate(table, buttonGroup, prop.getProperty("gameTemplate"), prop.getProperty("gameTemplateTip"));
        addTemplate(table, buttonGroup, prop.getProperty("inputProcessor"), prop.getProperty("inputProcessorTip"));
        addTemplate(table, buttonGroup, prop.getProperty("kotlinClassicTemplate"), prop.getProperty("kotlinClassicTemplateTip"));
        addTemplate(table, buttonGroup, prop.getProperty("kotlinTemplate"), prop.getProperty("kotlinTemplateTip"));
        addTemplate(table, buttonGroup, prop.getProperty("scene2dTemplate"), prop.getProperty("scene2dTemplateTip"));
        addTemplate(table, buttonGroup, prop.getProperty("superKoalio"), prop.getProperty("superKoalioTip"));

        table.row();
        label = new Label(prop.getProperty("thirdPartyTemplates"), skin, "field");
        label.setTouchable(Touchable.enabled);
        label.setEllipsis("...");
        table.add(label).minWidth(0).spaceTop(20).spaceBottom(10).colspan(2).growX();
        addTooltip(label, Align.top, prop.getProperty("officialTemplatesTip"));

        addTemplate(table, buttonGroup, prop.getProperty("ktxTemplate"), prop.getProperty("ktxTemplateTip"));
        addTemplate(table, buttonGroup, prop.getProperty("lmlKiwiInputTemplate"), prop.getProperty("lmlKiwiInputTemplateTip"));
        addTemplate(table, buttonGroup, prop.getProperty("lmlKiwiTemplate"), prop.getProperty("lmlKiwiTemplateTip"));
        addTemplate(table, buttonGroup, prop.getProperty("lmlMvcBasicTemplate"), prop.getProperty("lmlMvcBasicTemplateTip"));
        addTemplate(table, buttonGroup, prop.getProperty("lmlMvcBox2dTemplate"), prop.getProperty("lmlMvcBox2dTemplateTip"));
        addTemplate(table, buttonGroup, prop.getProperty("lmlMvcVisTemplate"), prop.getProperty("lmlMvcVisTemplateTip"));
        addTemplate(table, buttonGroup, prop.getProperty("lmlTemplate"), prop.getProperty("lmlTemplateTip"));
        addTemplate(table, buttonGroup, prop.getProperty("noise4jTemplate"), prop.getProperty("noise4jTemplateTip"));
        addTemplate(table, buttonGroup, prop.getProperty("visUiBasicTemplate"), prop.getProperty("visUiBasicTemplateTip"));
        addTemplate(table, buttonGroup, prop.getProperty("visUiShowcaseTemplate"), prop.getProperty("visUiShowcaseTemplateTip"));

        //links
        scrollTable.row();
        table = new Table();
        scrollTable.add(table).spaceTop(30).growX();

        table.defaults().space(5);
        label = new Label("LINKS", skin, "field");
        table.add(label).left();

        //gdx-pay
        table.defaults().left().padLeft(10);
        table.row();
        CollapsibleGroup collapsibleGroup = new CollapsibleGroup(true);
        table.add(collapsibleGroup);

        TextButton textButton = new TextButton(prop.getProperty("gdxPayLink"), skin, "link");
        textButton.getLabel().setAlignment(Align.left);
        collapsibleGroup.addActor(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> Gdx.net.openURI(prop.getProperty("gdxPayUrl")));

        Container container = new Container();
        container.left();
        collapsibleGroup.addActor(container);

        textButton = new TextButton(prop.getProperty("gdxPayLinkSmall"), skin, "link");
        textButton.getLabel().setAlignment(Align.left);
        container.setActor(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> Gdx.net.openURI(prop.getProperty("gdxPayUrl")));

        row();
        textButton = new TextButton("OK", skin);
        add(textButton).prefWidth(140).spaceTop(20);
        addHandListener(textButton);
        onChange(textButton, () -> {
            hide();
        });
    }

    private static GlyphLayout layout = new GlyphLayout();
    private void addTemplate(Table table, ButtonGroup buttonGroup, String labelText, String description) {
        table.row();
        CheckBox checkBox = new CheckBox(labelText, skin, "radio");
        table.add(checkBox).spaceRight(10);
        buttonGroup.add(checkBox);
        addHandListener(checkBox);

        Label label = new Label(description, skin, "description");
        label.setEllipsis("...");
        layout.setText(label.getStyle().font, description);
        table.add(label).prefWidth(layout.width).minWidth(0).growX();
    }

    public static void show() {
        TemplatesDialog dialog = new TemplatesDialog();
        dialog.show(stage);
    }
}
