package gdx.liftoff.ui.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
        addTemplate(table, buttonGroup, prop.getProperty("scene2dTemplate"), prop.getProperty("scene2dTemplateTip"), true);
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
        addTemplate(table, buttonGroup, prop.getProperty("lmlMvcBasicTemplate"), prop.getProperty("lmlMvcBasicTemplateTip"), true);
        addTemplate(table, buttonGroup, prop.getProperty("lmlMvcBox2dTemplate"), prop.getProperty("lmlMvcBox2dTemplateTip"));
        addTemplate(table, buttonGroup, prop.getProperty("lmlMvcVisTemplate"), prop.getProperty("lmlMvcVisTemplateTip"));
        addTemplate(table, buttonGroup, prop.getProperty("lmlTemplate"), prop.getProperty("lmlTemplateTip"), true);
        addTemplate(table, buttonGroup, prop.getProperty("noise4jTemplate"), prop.getProperty("noise4jTemplateTip"));
        addTemplate(table, buttonGroup, prop.getProperty("visUiBasicTemplate"), prop.getProperty("visUiBasicTemplateTip"));
        addTemplate(table, buttonGroup, prop.getProperty("visUiShowcaseTemplate"), prop.getProperty("visUiShowcaseTemplateTip"));

        //links
        scrollTable.row();
        table = new Table();
        scrollTable.add(table).spaceTop(30).growX();

        table.defaults().space(5).expandX();
        label = new Label("LINKS", skin, "field");
        table.add(label).left();

        //propose a template
        table.defaults().left().padLeft(10);
        table.row();

        TextButton textButton = new TextButton(prop.getProperty("templatesLink"), skin, "link");
        textButton.getLabel().setAlignment(Align.left);
        table.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> Gdx.net.openURI(prop.getProperty("issues")));

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
        addTemplate(table, buttonGroup, labelText, description, false);
    }

    private void addTemplate(Table table, ButtonGroup buttonGroup, String labelText, String description, boolean showGuiTip) {
        table.row();
        CheckBox checkBox = new CheckBox(labelText, skin, "radio");
        checkBox.left();
        table.add(checkBox).spaceRight(10).growX();
        buttonGroup.add(checkBox);
        addHandListener(checkBox);
        if (showGuiTip) addTooltip(checkBox, Align.topLeft, prop.getProperty("templatesStar"));

        Label label = new Label(description, skin, "description");
        label.setEllipsis("...");
        label.setTouchable(Touchable.enabled);
        layout.setText(label.getStyle().font, description);
        table.add(label).prefWidth(layout.width).minWidth(0).growX();
        label.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (pointer == -1) {
                    label.setColor(skin.getColor("red"));
                    checkBox.fire(event);
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                if (pointer == -1) {
                    label.setColor(Color.WHITE);
                    checkBox.fire(event);
                }
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                checkBox.setChecked(!checkBox.isChecked());
            }
        });

        checkBox.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) label.setColor(skin.getColor("red"));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer == -1) label.setColor(skin.getColor("white"));
            }
        });
    }

    public static void show() {
        TemplatesDialog dialog = new TemplatesDialog();
        dialog.show(stage);
    }
}
