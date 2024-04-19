package gdx.liftoff.ui.dialogs;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.Scaling;
import com.ray3k.stripe.DualCollapsibleGroup;
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.ScaleContainer;
import gdx.liftoff.Main;
import gdx.liftoff.ui.LogoWidget;
import gdx.liftoff.ui.panels.*;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static gdx.liftoff.Main.*;

public class FullscreenDialog extends PopTable {
    private static final float SPACING = 30;

    public FullscreenDialog() {
        super(skin.get("fullscreen", WindowStyle.class));
        setFillParent(true);
        pad(20);

        DualCollapsibleGroup dualCollapsibleGroup = new DualCollapsibleGroup();
        add(dualCollapsibleGroup).grow();

        Table contentTable = new Table();
        ScaleContainer scaleContainer = new ScaleContainer(Scaling.fit, contentTable);
        scaleContainer.setPrefSize(1920, 1080);
        scaleContainer.setMinSize(1920, 1080);
        dualCollapsibleGroup.addActor(scaleContainer);
        createPanels(contentTable);

        contentTable = new Table();
        createPanels(contentTable);

        ScrollPane scrollPane = new ScrollPane(contentTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setFlickScroll(false);
        dualCollapsibleGroup.addActor(scrollPane);
        addScrollFocusListener(scrollPane);
    }

    private void createPanels(Table contentTable) {
        contentTable.defaults().space(SPACING);
        Table table = new Table();
        contentTable.add(table).growX();
        table.setTransform(true);

        table.add().expandX();

        LogoWidget logoWidget = new LogoWidget();
        table.add(logoWidget).minHeight(Value.prefHeight);

        Button button = new Button(skin, "restore");
        table.add(button).expandX().right().top();
        addHandListener(button);
        onChange(button, () -> {
            hide();
            root.fadeInTable();
            Main.restoreWindow();
        });

        contentTable.row();
        table = new Table();
        contentTable.add(table);

        table.defaults().space(10);
        Label label = new Label(prop.getProperty("newProject"), skin, "header");
        table.add(label);

        table.row();
        ProjectPanel projectPanel = new ProjectPanel();
        table.add(projectPanel);
        projectPanel.captureKeyboardFocus();

        contentTable.row();
        table = new Table();
        contentTable.add(table).growY();

        table.defaults().space(SPACING).uniformX().growY().maxHeight(500);
        AddOnsPanel addOnsPanel = new AddOnsPanel();
        table.add(addOnsPanel);

        ThirdPartyPanel thirdPartyPanel = new ThirdPartyPanel();
        table.add(thirdPartyPanel);

        Table subTable = new Table();
        subTable.top();
        table.add(subTable);

        subTable.defaults().space(20);
        SettingsPanel settingsPanel = new SettingsPanel();
        subTable.add(settingsPanel);

        subTable.row();
        PathsPanel pathsPanel = new PathsPanel();
        subTable.add(pathsPanel);

        contentTable.row();
        TextButton textButton = new TextButton(prop.getProperty("generate"), skin, "big");
        contentTable.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> hide(sequence(
            fadeOut(.3f),
            run(FullscreenCompleteDialog::show)
        )));
    }

    public static void show() {
        FullscreenDialog fullscreenDialog = new FullscreenDialog();
        fullscreenDialog.show(stage);
    }
}
