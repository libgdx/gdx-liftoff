package gdx.liftoff.ui.dialogs;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.ray3k.stripe.CollapsibleGroup;
import com.ray3k.stripe.CollapsibleGroup.CollapseType;
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.ScaleContainer;
import gdx.liftoff.ui.UserData;

import static gdx.liftoff.Main.*;

/**
 * The dialog shown when the user clicks the platforms list in the add-ons panel
 */
public class PlatformsDialog extends PopTable  {
    private static GlyphLayout layout = new GlyphLayout();

    public PlatformsDialog(boolean fullscreen) {
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
        Label label = new Label(prop.getProperty("platforms"), skin, "header");
        contentTable.add(label);

        //scrollable area includes primary and secondary platforms
        contentTable.row();
        Table scrollTable = new Table();
        scrollTable.pad(SPACE_SMALL);
        ScrollPane scrollPane = new ScrollPane(scrollTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFlickScroll(false);
        contentTable.add(scrollPane).spaceTop(SPACE_LARGE);
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

        //ok button
        contentTable.row();
        TextButton textButton = new TextButton(prop.getProperty("ok"), skin);
        contentTable.add(textButton).prefWidth(140).spaceTop(SPACE_LARGE);
        addHandListener(textButton);
        onChange(textButton, () -> {
            hide();
        });
    }

    /**
     * Convenience method to add platforms to the table
     * @param table The table to add the widgets to
     * @param platformName The name of the platform
     * @param description A short description of the platform
     * @return The CheckBox created for the platform
     */
    private CheckBox addPlatform(Table table, String platformName, String description) {
        table.row();
        CheckBox checkBox = new CheckBox(platformName, skin);
        checkBox.setChecked(UserData.platforms.contains(platformName, false));
        checkBox.left();
        table.add(checkBox).growX();
        addHandListener(checkBox);
        onChange(checkBox, () -> {
            if (checkBox.isChecked() && !UserData.platforms.contains(platformName, false)) UserData.platforms.add(platformName);
            else UserData.platforms.removeValue(platformName, false);
        });

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
    public static void show(boolean fullscreen, Runnable onHideRunnable) {
        PlatformsDialog dialog = new PlatformsDialog(fullscreen);
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
