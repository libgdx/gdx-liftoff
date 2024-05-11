package gdx.liftoff.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.ray3k.stripe.CollapsibleGroup;
import com.ray3k.stripe.CollapsibleGroup.CollapseType;
import gdx.liftoff.Main;
import gdx.liftoff.ui.dialogs.FullscreenCompleteDialog;
import gdx.liftoff.ui.dialogs.FullscreenDialog;

import static gdx.liftoff.Main.*;

/**
 * A simple table to overlay the rest of the UI. This contains the maximize button and version. It implements a reactive
 * layout which only display the widgets if the window is large enough to show the root table at its preferred size with
 * some padding.
 */
public class OverlayTable extends Table {
    public OverlayTable() {
        pad(SPACE_MEDIUM);

        CollapsibleGroup dualCollapsibleGroup = new CollapsibleGroup(CollapseType.BOTH);
        add(dualCollapsibleGroup).grow();

        //a content container that only cares about the minHeight
        Container container = new Container();
        container.minSize(0, ROOT_TABLE_PREF_HEIGHT + 70);
        container.fill();
        dualCollapsibleGroup.addActor(container);
        container.setActor(createContentTable());

        //a content container that only cares about the minWidth
        container = new Container();
        container.minSize(ROOT_TABLE_PREF_WIDTH + 140, 0);
        container.fill();
        dualCollapsibleGroup.addActor(container);
        container.setActor(createContentTable());

        container = new Container();
        dualCollapsibleGroup.addActor(container);
    }

    private Table createContentTable() {
        Table table = new Table();

        //maximize
        Button button = new Button(skin, "maximize");
        table.add(button).expand().top().right();
        addHandListener(button);
        onChange(button, () -> {
            pref.putBoolean("startMaximized", true);
            pref.flush();

            Main.maximizeWindow();
            fadeOut();
            Gdx.app.postRunnable(() -> {
                root.fadeOutTable();
                if (root.getCurrentTable() == root.completeTable) FullscreenCompleteDialog.show(false);
                else FullscreenDialog.show();
            });
        });

        //version
        table.row();
        Label label = new Label("v" + prop.getProperty("liftoffVersion"), skin);
        table.add(label).expand().bottom().right();

        return table;
    }

    public void fadeOut() {
        addAction(Actions.fadeOut(.5f));
    }

    public void fadeIn() {
        addAction(Actions.fadeIn(.5f));
    }
}
