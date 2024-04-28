package gdx.liftoff.ui.dialogs;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.Align;
import com.ray3k.stripe.PopTable;

import static gdx.liftoff.Main.*;

/**
 * The dialog shown when the user clicks the platforms list in the add-ons panel
 */
public class PlatformsDialog extends PopTable  {
    private static GlyphLayout layout = new GlyphLayout();

    //todo:Need to add fullscreen option that puts contents in a scalingGroup
    public PlatformsDialog() {
        setStyle(skin.get("dialog", WindowStyle.class));
        setKeepCenteredInWindow(true);
        setHideOnUnfocus(true);
        pad(SPACE_LARGE).padTop(SPACE_HUGE).padBottom(SPACE_HUGE);

        //title
        Label label = new Label(prop.getProperty("platforms"), skin, "header");
        add(label);

        //scrollable area includes primary and secondary platforms
        row();
        Table scrollTable = new Table();
        scrollTable.pad(SPACE_SMALL);
        ScrollPane scrollPane = new ScrollPane(scrollTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFlickScroll(false);
        add(scrollPane).grow().spaceTop(SPACE_LARGE);
        addScrollFocusListener(scrollPane);
        stage.setScrollFocus(scrollPane);

        //primary platforms title
        scrollTable.defaults().left();
        label = new Label(prop.getProperty("primaryPlatforms"), skin, "field");
        scrollTable.add(label).spaceTop(SPACE_HUGE);

        scrollTable.row();
        Table table = new Table();
        scrollTable.add(table).spaceTop(SPACE_MEDIUM).growX();

        //primary platforms
        //manually add core to a button group of one to enforce that it is always checked
        table.defaults().left().spaceLeft(SPACE_LARGE);
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

        //secondary platforms title
        scrollTable.row();
        label = new Label(prop.getProperty("secondaryPlatforms"), skin, "field");
        scrollTable.add(label).spaceTop(SPACE_HUGE);

        scrollTable.row();
        table = new Table();
        scrollTable.add(table).spaceTop(SPACE_MEDIUM).growX();

        //secondary platforms
        table.defaults().left().spaceLeft(SPACE_LARGE);
        addPlatform(table, prop.getProperty("headless"), prop.getProperty("headlessTip"));
        addPlatform(table, prop.getProperty("teavm"), prop.getProperty("teavmTip"));
        addPlatform(table, prop.getProperty("lwjgl2"), prop.getProperty("lwjgl2Tip"));
        addPlatform(table, prop.getProperty("server"), prop.getProperty("serverTip"));
        addPlatform(table, prop.getProperty("shared"), prop.getProperty("sharedTip"));
        addPlatform(table, prop.getProperty("ios-moe"), prop.getProperty("ios-moeTip"));

        //ok button
        row();
        TextButton textButton = new TextButton(prop.getProperty("ok"), skin);
        add(textButton).prefWidth(140).spaceTop(SPACE_LARGE);
        addHandListener(textButton);
        onChange(textButton, () -> {
            hide();
        });
    }

    /**
     * Convenience method to add platforms to the table
     * @param table
     * @param labelString
     * @param description
     * @return
     */
    private CheckBox addPlatform(Table table, String labelString, String description) {
        table.row();
        CheckBox checkBox = new CheckBox(labelString, skin);
        checkBox.left();
        table.add(checkBox).growX();
        addHandListener(checkBox);

        Label label = new Label(description, skin, "description");
        label.setEllipsis("...");
        label.setAlignment(Align.left);
        layout.setText(label.getStyle().font, description);
        table.add(label).growX().minWidth(0).prefWidth(layout.width + 5);
        addLabelHighlight(checkBox, label);
        return checkBox;
    }

    /**
     * Convenience method to show the table on the stage
     */
    public static void show() {
        PlatformsDialog dialog = new PlatformsDialog();
        dialog.show(stage);
    }
}
