package gdx.liftoff.ui.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.ray3k.stripe.CollapsibleGroup;
import com.ray3k.stripe.CollapsibleGroup.CollapseType;
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.ScaleContainer;
import gdx.liftoff.ui.UserData;

import static gdx.liftoff.Main.*;

/**
 * The dialog shown when the user clicks the template button in the add-ons panel
 */
public class TemplatesDialog extends PopTable  {
    private static GlyphLayout layout = new GlyphLayout();

    public TemplatesDialog(boolean fullscreen) {
        setStyle(skin.get("dialog", WindowStyle.class));
        setKeepCenteredInWindow(true);
        setHideOnUnfocus(true);

        if (fullscreen) {
            CollapsibleGroup collapsibleGroup = new CollapsibleGroup(CollapseType.BOTH);
            add(collapsibleGroup).grow();

            Table contentTable = new Table();
            populate(contentTable);

            Container container = new Container(contentTable);
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
        contentTable.clearChildren();
        contentTable.pad(SPACE_LARGE).padTop(SPACE_HUGE).padBottom(SPACE_HUGE);

        //title
        Label label = new Label(prop.getProperty("templates"), skin, "header");
        contentTable.add(label);

        //scrollable area includes basic templates, third-party templates, and links
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

        table.defaults().left().space(SPACE_SMALL);
        ButtonGroup buttonGroup = new ButtonGroup();

        //basic templates title
        table.row();
        label = new Label(prop.getProperty("officialTemplates"), skin, "field");
        label.setTouchable(Touchable.enabled);
        table.add(label).minWidth(0).spaceBottom(SPACE_MEDIUM).colspan(2).growX();
        addTooltip(label, Align.top, prop.getProperty("officialTemplatesTip"));

        //basic templates
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

        //third-party templates title
        table.row();
        label = new Label(prop.getProperty("thirdPartyTemplates"), skin, "field");
        label.setTouchable(Touchable.enabled);
        label.setEllipsis("...");
        table.add(label).minWidth(0).spaceTop(SPACE_LARGE).spaceBottom(SPACE_MEDIUM).colspan(2).growX();
        addTooltip(label, Align.top, prop.getProperty("officialTemplatesTip"));

        //third-party templates
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
        scrollTable.add(table).spaceTop(SPACE_HUGE).growX();

        table.defaults().space(SPACE_SMALL).expandX();
        label = new Label(prop.getProperty("links"), skin, "field");
        table.add(label).left();

        //propose a template
        table.defaults().left().padLeft(SPACE_MEDIUM);
        table.row();

        TextButton textButton = new TextButton(prop.getProperty("templatesLink"), skin, "link");
        textButton.getLabel().setAlignment(Align.left);
        table.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> Gdx.net.openURI(prop.getProperty("issues")));

        //ok button
        contentTable.row();
        textButton = new TextButton("OK", skin);
        contentTable.add(textButton).prefWidth(140).spaceTop(SPACE_LARGE);
        addHandListener(textButton);
        onChange(textButton, () -> {
            hide();
        });
    }

    private void addTemplate(Table table, ButtonGroup buttonGroup, String labelText, String description) {
        addTemplate(table, buttonGroup, labelText, description, false);
    }

    /**
     * A convenience method to add a template to the table
     * @param table The table to add the widgets
     * @param buttonGroup The button group that the checkbox should belong to
     * @param templateName The name of the template
     * @param description A short description of the template
     * @param showGuiTip When set to true, a tooltip is shown that signifies that the widget depends on the default
     *                   GUI Skin
     */
    private void addTemplate(Table table, ButtonGroup buttonGroup, String templateName, String description, boolean showGuiTip) {
        table.row();
        CheckBox checkBox = new CheckBox(templateName, skin, "radio");
        checkBox.setChecked(UserData.template.equals(templateName));
        checkBox.left();
        table.add(checkBox).spaceRight(SPACE_MEDIUM).growX();
        buttonGroup.add(checkBox);
        addHandListener(checkBox);
        if (showGuiTip) addTooltip(checkBox, Align.top, prop.getProperty("templatesStar"));
        onChange(checkBox, () -> UserData.template = templateName);

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

    public static void show(boolean fullscreen, Runnable onHideRunnable) {
        TemplatesDialog dialog = new TemplatesDialog(fullscreen);
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
