package gdx.liftoff.ui.dialogs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.ray3k.stripe.PopTable;

import static gdx.liftoff.Main.*;

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
        CheckBox checkBox = addPlatform(table, prop.getProperty("core"), prop.getProperty("coreTip"));
        buttonGroup.add(checkBox);
        PopTable pop = addTooltip(checkBox, Align.top, prop.getProperty("coreMandatoryTip"));
        onClick(checkBox, () -> pop.show(stage));

        addPlatform(table, prop.getProperty("lwjgl3"), prop.getProperty("lwjgl3Tip"));
        addPlatform(table, prop.getProperty("android"), prop.getProperty("androidTip"));
        addPlatform(table, prop.getProperty("ios"), prop.getProperty("iosTip"));
        addPlatform(table, prop.getProperty("html"), prop.getProperty("htmlTip"));

        scrollTable.row();
        label = new Label(prop.getProperty("secondaryPlatforms"), skin, "field");
        scrollTable.add(label).spaceTop(30);

        scrollTable.row();
        table = new Table();
        scrollTable.add(table).spaceTop(10).growX();

        table.defaults().left().spaceLeft(20);
        addPlatform(table, prop.getProperty("headless"), prop.getProperty("headlessTip"));
        addPlatform(table, prop.getProperty("teavm"), prop.getProperty("teavmTip"));
        addPlatform(table, prop.getProperty("lwjgl2"), prop.getProperty("lwjgl2"));
        addPlatform(table, prop.getProperty("server"), prop.getProperty("serverTip"));
        addPlatform(table, prop.getProperty("shared"), prop.getProperty("sharedTip"));
        addPlatform(table, prop.getProperty("ios-moe"), prop.getProperty("ios-moeTip"));

        row();
        TextButton textButton = new TextButton(prop.getProperty("ok"), skin);
        add(textButton).prefWidth(140).spaceTop(20);
        addHandListener(textButton);
        onChange(textButton, () -> {
            hide();
        });
    }

    private static GlyphLayout layout = new GlyphLayout();
    private CheckBox addPlatform(Table table, String labelString, String description) {
        table.row();
        CheckBox checkBox = new CheckBox(labelString, skin);
        table.add(checkBox);
        addHandListener(checkBox);

        Label label = new Label(description, skin, "description");
        label.setEllipsis("...");
        label.setAlignment(Align.left);
        layout.setText(label.getStyle().font, description);
        table.add(label).growX().minWidth(0).prefWidth(layout.width + 5);
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
        return checkBox;
    }

    public static void show() {
        PlatformsDialog dialog = new PlatformsDialog();
        dialog.show(stage);
    }
}
