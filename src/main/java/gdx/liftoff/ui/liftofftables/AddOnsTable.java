package gdx.liftoff.ui.liftofftables;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import gdx.liftoff.ui.panels.AddOnsPanel;

import static gdx.liftoff.Main.*;

/**
 * This table displays the AddOnsPanel for display in the normal view
 */
public class AddOnsTable extends LiftoffTable {
    public AddOnsTable() {
        populate();
    }

    private void populate() {
        clearChildren();
        setBackground(skin.getDrawable("black"));
        pad(20).padLeft(30).padRight(30);

        //add-ons panel
        defaults().space(30);
        AddOnsPanel addOnsPanel = new AddOnsPanel();
        add(addOnsPanel).grow().spaceTop(0).maxHeight(500);

        row();
        Table table = new Table();
        add(table).bottom().growX();

        //previous button
        TextButton textButton = new TextButton(prop.getProperty("previous"), skin);
        table.add(textButton).uniformX().fillX();
        addHandListener(textButton);
        onChange(textButton, () -> root.previousTable());

        //an empty space between the cells
        table.add().growX().space(5);

        //next button
        textButton = new TextButton(prop.getProperty("next"), skin);
        table.add(textButton).uniformX().fillX();
        addHandListener(textButton);
        onChange(textButton, () -> root.nextTable());
    }

    @Override
    public void captureKeyboardFocus() {

    }

    @Override
    public void finishAnimation() {

    }
}
