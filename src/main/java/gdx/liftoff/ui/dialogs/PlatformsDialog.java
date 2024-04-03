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

        Label label = new Label("PLATFORMS", skin, "header");
        add(label);

        row();
        Table scrollTable = new Table();
        ScrollPane scrollPane = new ScrollPane(scrollTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFlickScroll(false);
        add(scrollPane).grow().spaceTop(20);
        addScrollFocusListener(scrollPane);
        stage.setScrollFocus(scrollPane);

        scrollTable.defaults().left();
        label = new Label("PRIMARY PLATFORMS", skin, "field");
        scrollTable.add(label).spaceTop(30);

        scrollTable.row();
        Table table = new Table();
        scrollTable.add(table).spaceTop(10).growX();

        table.defaults().left().spaceLeft(20);
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.setMinCheckCount(1);
        CheckBox checkBox = new CheckBox("CORE", skin);
        table.add(checkBox);
        buttonGroup.add(checkBox);
        addHandListener(checkBox);

        addDescriptionLabel(coreDescription, table);

        table.row();
        checkBox = new CheckBox("DESKTOP", skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescriptionLabel(desktopDescription, table);

        table.row();
        checkBox = new CheckBox("ANDROID", skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescriptionLabel(androidDescription, table);

        table.row();
        checkBox = new CheckBox("iOS", skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescriptionLabel(iosDescription, table);

        table.row();
        checkBox = new CheckBox("HTML", skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescriptionLabel(htmlDescription, table);

        scrollTable.row();
        label = new Label("SECONDARY PLATFORMS", skin, "field");
        scrollTable.add(label).spaceTop(30);

        scrollTable.row();
        table = new Table();
        scrollTable.add(table).spaceTop(10).growX();

        table.defaults().left().spaceLeft(20);
        checkBox = new CheckBox("HEADLESS", skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescriptionLabel(headlessDescription, table);

        table.row();
        table.defaults().left().spaceLeft(20);
        checkBox = new CheckBox("HTML (TEAVM)", skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescriptionLabel(htmlTeavmDescription, table);

        table.row();
        checkBox = new CheckBox("DESKTOP (LEGACY)", skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescriptionLabel(desktopLegacyDescription, table);

        table.row();
        checkBox = new CheckBox("SERVER", skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescriptionLabel(serverDescription, table);

        table.row();
        checkBox = new CheckBox("SHARED", skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescriptionLabel(sharedDescription, table);

        table.row();
        checkBox = new CheckBox("iOS MULTI-OS ENGINE", skin);
        table.add(checkBox);
        addHandListener(checkBox);

        addDescriptionLabel(iosMultiosDescription, table);

        row();
        TextButton textButton = new TextButton("OK", skin);
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
