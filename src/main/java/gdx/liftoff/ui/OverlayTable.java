package gdx.liftoff.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.ray3k.stripe.CollapsibleGroup;
import com.ray3k.stripe.CollapsibleGroup.CollapseType;
import gdx.liftoff.Main;
import gdx.liftoff.ui.dialogs.FullscreenDialog;

import static gdx.liftoff.Main.*;

/**
 * A simple table to overlay the rest of the UI. This contains the maximize button and version.
 */
public class OverlayTable extends Table {
    public OverlayTable() {
        top().right().pad(SPACE_MEDIUM);
        CollapsibleGroup dualCollapsibleGroup = new CollapsibleGroup(CollapseType.BOTH);
        add(dualCollapsibleGroup);

        Container container = new Container();
        container.minSize(ROOT_TABLE_PREF_WIDTH + 70, ROOT_TABLE_PREF_HEIGHT + 70);
        dualCollapsibleGroup.addActor(container);

        Table table = new Table();
        table.top().right();
        container.setActor(table);

        //maximize
        Button button = new Button(skin, "maximize");
        table.add(button);
        addHandListener(button);
        onChange(button, () -> {
            Main.maximizeWindow();
            Gdx.app.postRunnable(() -> {
                root.fadeOutTable();
                FullscreenDialog.show();
            });
        });

        container = new Container();
        dualCollapsibleGroup.addActor(container);

        //todo:version
    }
}
