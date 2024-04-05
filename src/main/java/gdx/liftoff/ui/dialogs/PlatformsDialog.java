package gdx.liftoff.ui.dialogs;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.Align;
import com.ray3k.stripe.PopTable;

import static gdx.liftoff.Main.*;
import static gdx.liftoff.ui.data.Data.*;

public class PlatformsDialog extends PopTable  {
    public PlatformsDialog() {
        setStyle(skin.get("dialog", WindowStyle.class));
        setKeepCenteredInWindow(true);
        setHideOnUnfocus(true);
        pad(20).padTop(30).padBottom(30);

        Label label = new Label(prop.getProperty("platforms"), skin, "header");
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
        label = new Label(prop.getProperty("primaryPlatforms"), skin, "field");
        scrollTable.add(label).spaceTop(30);

        scrollTable.row();
        Table table = new Table();
        scrollTable.add(table).spaceTop(10).growX();

        table.defaults().left().spaceLeft(20);
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.setMinCheckCount(1);
        CheckBox checkBox = new CheckBox(prop.getProperty("core"), skin);
        table.add(checkBox);
        buttonGroup.add(checkBox);
        addHandListener(checkBox);

        addDescriptionLabel(prop.getProperty("coreTip"), table);

        table.row();
        checkBox = new CheckBox(prop.getProperty("lwjgl3"), skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescriptionLabel(prop.getProperty("lwjgl3Tip"), table);

        table.row();
        checkBox = new CheckBox(prop.getProperty("android"), skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescriptionLabel(prop.getProperty("androidTip"), table);

        table.row();
        checkBox = new CheckBox(prop.getProperty("ios"), skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescriptionLabel(prop.getProperty("iosTip"), table);

        table.row();
        checkBox = new CheckBox(prop.getProperty("html"), skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescriptionLabel(prop.getProperty("htmlTip"), table);

        scrollTable.row();
        label = new Label(prop.getProperty("secondaryPlatforms"), skin, "field");
        scrollTable.add(label).spaceTop(30);

        scrollTable.row();
        table = new Table();
        scrollTable.add(table).spaceTop(10).growX();

        table.defaults().left().spaceLeft(20);
        checkBox = new CheckBox(prop.getProperty("headless"), skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescriptionLabel(prop.getProperty("headlessTip"), table);

        table.row();
        table.defaults().left().spaceLeft(20);
        checkBox = new CheckBox(prop.getProperty("teavm"), skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescriptionLabel(prop.getProperty("teavmTip"), table);

        table.row();
        checkBox = new CheckBox(prop.getProperty("lwjgl2"), skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescriptionLabel(prop.getProperty("lwjgl2"), table);

        table.row();
        checkBox = new CheckBox(prop.getProperty("server"), skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescriptionLabel(prop.getProperty("serverTip"), table);

        table.row();
        checkBox = new CheckBox(prop.getProperty("shared"), skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescriptionLabel(prop.getProperty("sharedTip"), table);

        table.row();
        checkBox = new CheckBox(prop.getProperty("ios-moe"), skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescriptionLabel(prop.getProperty("ios-moeTip"), table);

        row();
        TextButton textButton = new TextButton(prop.getProperty("ok"), skin);
        add(textButton).prefWidth(140).spaceTop(20);
        addHandListener(textButton);
        onChange(textButton, () -> {
            hide();
        });
    }

    private static GlyphLayout layout = new GlyphLayout();
    private void addDescriptionLabel(String description, Table table) {
        Label label = new Label(description, skin, "description");
        label.setEllipsis("...");
        label.setAlignment(Align.left);
        layout.setText(label.getStyle().font, description);
        table.add(label).growX().minWidth(0).prefWidth(layout.width + 5);
    }

    public static void show() {
        PlatformsDialog dialog = new PlatformsDialog();
        dialog.show(stage);
    }
}
