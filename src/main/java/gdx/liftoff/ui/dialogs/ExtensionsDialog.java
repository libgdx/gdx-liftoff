package gdx.liftoff.ui.dialogs;

import com.badlogic.gdx.Gdx;
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
 * The extensions dialog displayed when the user clicks the extensions list in the add-ons panel.
 */
public class ExtensionsDialog extends PopTable  {
    private static GlyphLayout layout = new GlyphLayout();

    public ExtensionsDialog(boolean fullscreen) {
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
        contentTable.pad(SPACE_LARGE).padTop(SPACE_HUGE).padBottom(SPACE_HUGE);

        //title label
        Label label = new Label(prop.getProperty("extensions"), skin, "header");
        contentTable.add(label);

        //scrollable area including list of extensions and links
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

        //list of extensions
        scrollTable.defaults().left();
        Table table = new Table();
        table.left();
        scrollTable.add(table).spaceTop(SPACE_MEDIUM).growX();

        table.defaults().left().spaceLeft(SPACE_MEDIUM);
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
        scrollTable.add(table).spaceTop(SPACE_HUGE).growX();

        table.defaults().space(SPACE_SMALL);
        label = new Label(prop.getProperty("links"), skin, "field");
        table.add(label).left();

        //gdx-pay link
        table.defaults().left().padLeft(SPACE_MEDIUM);
        table.row();
        CollapsibleGroup collapsibleGroup = new CollapsibleGroup(CollapseType.HORIZONTAL);
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

        //OK button to close the dialog
        contentTable.row();
        textButton = new TextButton("OK", skin);
        contentTable.add(textButton).prefWidth(140).spaceTop(SPACE_LARGE);
        addHandListener(textButton);
        onChange(textButton, () -> hide());
    }

    /**
     * Convenience method that adds an extension to the given table
     * @param table The table to add widgets to
     * @param extensionName The name of the extension
     * @param description A short description of the extension
     * @param url The URL pointing to the home page of the extension
     */
    private void addExtension(Table table, String extensionName, String description, String url) {
        //checkbox
        table.row();
        CheckBox checkBox = new CheckBox(extensionName, skin);
        checkBox.setChecked(UserData.extensions.contains(extensionName, false));
        table.add(checkBox);
        addHandListener(checkBox);
        onChange(checkBox, () -> {
            if (checkBox.isChecked() && !UserData.extensions.contains(extensionName, false)) UserData.extensions.add(extensionName);
            else UserData.extensions.removeValue(extensionName, false);
        });

        Table subTable = new Table();
        table.add(subTable);

        //description
        subTable.defaults().space(SPACE_MEDIUM);
        Label label = new Label(description, skin, "description");
        label.setEllipsis("...");
        layout.setText(label.getStyle().font, description);
        subTable.add(label).prefWidth(layout.width).minWidth(0);
        addLabelHighlight(checkBox, label);

        //link
        Button button = new Button(skin, "external-link");
        subTable.add(button);
        addHandListener(button);
        onChange(button, () -> Gdx.net.openURI(url));
    }

    /**
     * Convenience method to display the dialog on the stage
     */
    public static void show(boolean fullscreen, Runnable onHideRunnable) {
        ExtensionsDialog dialog = new ExtensionsDialog(fullscreen);
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
