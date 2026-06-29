package gdx.liftoff.ui.liftofftables;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import gdx.liftoff.ui.panels.AddOnsPanel;

import static gdx.liftoff.Main.*;

/**
 * This table displays the AddOnsPanel for display in the normal view
 */
public class AddOnsTable extends LiftoffTable {
    public AddOnsTable() {
        populate();
    }

    @Override
    public void populate() {
        clearChildren();
        setBackground(skin.getDrawable("black"));
        pad(SPACE_LARGE).padLeft(SPACE_HUGE).padRight(SPACE_HUGE);

        //add-ons panel
        defaults().space(SPACE_HUGE);
        AddOnsPanel addOnsPanel = new AddOnsPanel(false);
        add(addOnsPanel).grow().spaceTop(0).maxHeight(500);

        row();
        Table table = new Table();
        add(table).bottom().growX();

        //previous button
        TextButton textButton = new TextButton(prop.getProperty("previous"), skin);
        table.add(textButton).uniformX().fillX();
        addHandListener(textButton);
        addTooltip(textButton, Align.top, prop.getProperty("previousTip"));
        onChange(textButton, () -> root.previousTable());

        //an empty space between the cells
        table.add().growX().space(SPACE_SMALL);

        //next button
        textButton = new TextButton(prop.getProperty("next"), skin);
        table.add(textButton).uniformX().fillX();
        addHandListener(textButton);
        addTooltip(textButton, Align.top, prop.getProperty("nextTip"));
        onChange(textButton, () -> root.nextTable());
    }

    @Override
    public void captureKeyboardFocus() {

    }

    @Override
    public void finishAnimation() {

    }
}
