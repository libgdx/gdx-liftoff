package gdx.liftoff.ui.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.Align;
import com.ray3k.stripe.PopTable;

import static gdx.liftoff.Main.*;
import static gdx.liftoff.ui.data.Data.*;

public class ExtensionsDialog extends PopTable  {
    public ExtensionsDialog() {
        setStyle(skin.get("dialog", WindowStyle.class));
        setKeepCenteredInWindow(true);
        setHideOnUnfocus(true);
        pad(20).padTop(30).padBottom(30);

        Label label = new Label("EXTENSIONS", skin, "header");
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

        //ashley
        table.row();
        CheckBox checkBox = new CheckBox("Ashley", skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescription(table, ashleyDescription, ashleyURL);

        //box2DLights
        table.row();
        checkBox = new CheckBox("Box2DLights", skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescription(table, box2DLightsDescription, box2DLightsURL);

        //ai
        table.row();
        checkBox = new CheckBox("Ai", skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescription(table, aiDescription, aiURL);

        //box2D
        table.row();
        checkBox = new CheckBox("Box2D", skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescription(table, box2DDescription, box2DURL);

        //bullet
        table.row();
        checkBox = new CheckBox("Bullet", skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescription(table, bulletDescription, bulletURL);

        //controllers
        table.row();
        checkBox = new CheckBox("Controllers", skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescription(table, controllersDescription, controllersURL);

        //freetype
        table.row();
        checkBox = new CheckBox("Freetype", skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescription(table, freetypeDescription, freetypeURL);

        //tools
        table.row();
        checkBox = new CheckBox("Tools", skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescription(table, toolsDescription, toolsURL);

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
        TextButton textButton = new TextButton(gdxPayLinkText, skin, "link");
        textButton.getLabel().setAlignment(Align.left);
        table.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> Gdx.net.openURI(gdxPayLinkURL));

        row();
        textButton = new TextButton("OK", skin);
        add(textButton).prefWidth(140).spaceTop(20);
        addHandListener(textButton);
        onChange(textButton, () -> {
            hide();
        });
    }

    private static GlyphLayout layout = new GlyphLayout();
    private void addDescription(Table table, String description, String url) {
        Table subTable = new Table();
        table.add(subTable);

        subTable.defaults().space(10);
        Label label = new Label(description, skin, "description");
        label.setEllipsis("...");
        layout.setText(label.getStyle().font, description);
        subTable.add(label).prefWidth(layout.width).minWidth(0);

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
