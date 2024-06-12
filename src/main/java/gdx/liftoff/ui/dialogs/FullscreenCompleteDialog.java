package gdx.liftoff.ui.dialogs;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.Scaling;
import com.ray3k.stripe.CollapsibleGroup;
import com.ray3k.stripe.CollapsibleGroup.CollapseType;
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.ScaleContainer;
import gdx.liftoff.Main;
import gdx.liftoff.ui.panels.CompleteButtonsPanel;
import gdx.liftoff.ui.panels.CompletePanel;
import gdx.liftoff.ui.panels.GeneratingPanel;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static gdx.liftoff.Main.*;

/**
 * Dialog shown when in fullscreen layout mode and the user clicks the generate button. The layout scales up if the
 * available space is larger than 1920x1080.
 */
public class FullscreenCompleteDialog extends PopTable {
    public static FullscreenCompleteDialog fullscreenCompleteDialog;

    public FullscreenCompleteDialog(boolean showGeneration) {
        super(skin.get("fullscreen", WindowStyle.class));
        fullscreenCompleteDialog = this;
        setFillParent(true);
        pad(20);

        //collapsible group that alternates between screen scaled scrollpane and a fit scaled container based on available space
        CollapsibleGroup dualCollapsibleGroup = new CollapsibleGroup(CollapseType.BOTH);
        add(dualCollapsibleGroup).grow();

        Table contentTable = new Table();
        ScaleContainer scaleContainer = new ScaleContainer(Scaling.fit, contentTable);
        scaleContainer.setPrefSize(1920, 1080);
        scaleContainer.setMinSize(1920, 1080);
        dualCollapsibleGroup.addActor(scaleContainer);
        createPanels(contentTable, showGeneration);

        contentTable = new Table();
        createPanels(contentTable, showGeneration);

        ScrollPane scrollPane = new ScrollPane(contentTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setFlickScroll(false);
        dualCollapsibleGroup.addActor(scrollPane);
        addScrollFocusListener(scrollPane);
    }

    private void createPanels(Table contentTable, boolean showGeneration) {
        //generating panel is displayed first and is alternated with the complete panel upon completion of the animation
        GeneratingPanel generatingPanel = new GeneratingPanel(true);

        Table table = new Table();
        contentTable.stack(generatingPanel, table).grow();

        //complete panel
        table.defaults().space(SPACE_MEDIUM);
        CompletePanel completePanel = new CompletePanel(true);
        table.add(completePanel);

        //buttons for the complete panel while in fullscreen
        table.row();
        CompleteButtonsPanel completeButtonsPanel = new CompleteButtonsPanel(this, true);
        table.add(completeButtonsPanel);

        //animation initial setup
        table.setColor(CLEAR_WHITE);
        table.setTouchable(Touchable.disabled);
        generatingPanel.setColor(CLEAR_WHITE);

        //animation
        if (showGeneration) {
            addAction(sequence(
                targeting(generatingPanel, fadeIn(.5f)),
                delay(1f),
                new Action() {
                    @Override
                    public boolean act(float v) {
                        if (generatingProject) return false;
                        else {
                            completePanel.populate(true);
                            return true;
                        }
                    }
                },
                targeting(generatingPanel, fadeOut(.3f)),
                targeting(table, fadeIn(.3f)),
                targeting(table, touchable(Touchable.enabled))
            ));
        } else {
            addAction(sequence(
                targeting(table, fadeIn(.3f)),
                targeting(table, touchable(Touchable.enabled))
            ));
        }

        contentTable.row();
        Label label = new Label("v" + prop.getProperty("liftoffVersion"), skin);
        contentTable.add(label).expandX().right();
    }

    public static void show() {
        show(true);
    }

    public static void show(boolean showGeneration) {
        FullscreenCompleteDialog fullscreenDialog = new FullscreenCompleteDialog(showGeneration);
        fullscreenDialog.show(stage);
    }
}
