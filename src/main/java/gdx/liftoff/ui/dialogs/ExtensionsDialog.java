package gdx.liftoff.ui.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.ray3k.stripe.CollapsibleGroup;
import com.ray3k.stripe.PopTable;

import static gdx.liftoff.Main.*;

public class ExtensionsDialog extends PopTable  {
    public ExtensionsDialog() {
        setStyle(skin.get("dialog", WindowStyle.class));
        setKeepCenteredInWindow(true);
        setHideOnUnfocus(true);
        pad(20).padTop(30).padBottom(30);

        Label label = new Label(prop.getProperty("extensions"), skin, "header");
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

        table.defaults().left().spaceLeft(10);
        addExtension(table, prop.getProperty("ashley"), prop.getProperty("ashleyTip"), prop.getProperty("ashleyUrl"));
        addExtension(table, prop.getProperty("box2dlights"), prop.getProperty("box2dlightsTip"), prop.getProperty("gdx-box2dlightsUrl"));
        addExtension(table, prop.getProperty("gdx-ai"), prop.getProperty("gdx-aiTip"), prop.getProperty("gdx-aiUrl"));
        addExtension(table, prop.getProperty("gdx-box2d"), prop.getProperty("gdx-box2dTip"), prop.getProperty("gdx-box2dUrl"));
        addExtension(table, prop.getProperty("gdx-bullet"), prop.getProperty("gdx-bulletTip"), prop.getProperty("gdx-bulletUrl"));
        addExtension(table, prop.getProperty("gdx-controllers"), prop.getProperty("gdx-controllersTip"), prop.getProperty("gdx-controllersUrl"));
        addExtension(table, prop.getProperty("gdx-freetype"), prop.getProperty("gdx-freetypeTip"), prop.getProperty("gdx-freetypeUrl"));
        addExtension(table, prop.getProperty("gdx-tools"), prop.getProperty("gdx-toolsTip"), prop.getProperty("gdx-toolsUrl"));

        //links
        scrollTable.row();
        table = new Table();
        scrollTable.add(table).spaceTop(30).growX();

        table.defaults().space(5);
        label = new Label(prop.getProperty("links"), skin, "field");
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
    private void addExtension(Table table, String labelText, String description, String url) {
        table.row();
        CheckBox checkBox = new CheckBox(labelText, skin);
        table.add(checkBox);
        addHandListener(checkBox);

        Table subTable = new Table();
        table.add(subTable);

        subTable.defaults().space(10);
        Label label = new Label(description, skin, "description");
        label.setEllipsis("...");
        layout.setText(label.getStyle().font, description);
        subTable.add(label).prefWidth(layout.width).minWidth(0);
        addLabelHighlight(checkBox, label);

        Button button = new Button(skin, "external-link");
        subTable.add(button);
        addHandListener(button);
        onChange(button, () -> Gdx.net.openURI(url));
    }

    public static void show() {
        ExtensionsDialog dialog = new ExtensionsDialog();
        dialog.show(stage);
    }
}
