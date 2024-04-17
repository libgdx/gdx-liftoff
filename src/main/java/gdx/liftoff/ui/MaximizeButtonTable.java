package gdx.liftoff.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.ray3k.stripe.DualCollapsibleGroup;
import gdx.liftoff.ui.dialogs.FullscreenDialog;

import static gdx.liftoff.Main.*;

public class MaximizeButtonTable extends Table {
    public MaximizeButtonTable() {
        top().right().pad(10);
        DualCollapsibleGroup dualCollapsibleGroup = new DualCollapsibleGroup();
        add(dualCollapsibleGroup);

        Container container = new Container();
        container.minSize(ROOT_TABLE_PREF_WIDTH + 70, ROOT_TABLE_PREF_HEIGHT + 70);
        dualCollapsibleGroup.addActor(container);

        Table table = new Table();
        table.top().right();
        container.setActor(table);

        Button button = new Button(skin, "maximize");
        table.add(button);
        addHandListener(button);
        onChange(button, FullscreenDialog::show);

        container = new Container();
        dualCollapsibleGroup.addActor(container);
    }
}
