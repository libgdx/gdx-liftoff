package gdx.liftoff.ui.liftofftables;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import gdx.liftoff.ui.panels.ThirdPartyPanel;

import static gdx.liftoff.Main.*;

/**
 * This table displays the third party panel in the normal workflow.
 */
public class ThirdPartyTable extends LiftoffTable {
    private ThirdPartyPanel thirdPartyPanel;

    public ThirdPartyTable() {
        populate();
    }

    public void populate() {
        clearChildren();
        setBackground(skin.getDrawable("black"));
        pad(SPACE_LARGE).padLeft(SPACE_HUGE).padRight(SPACE_HUGE);

        //third party panel
        defaults().space(SPACE_HUGE);
        thirdPartyPanel = new ThirdPartyPanel(false);
        add(thirdPartyPanel).grow().spaceTop(0).maxHeight(550);

        row();
        Table table = new Table();
        add(table).bottom().growX();

        //previous button
        TextButton textButton = new TextButton(prop.getProperty("previous"), skin);
        table.add(textButton).uniformX().fillX();
        addHandListener(textButton);
        addTooltip(textButton, Align.top, prop.getProperty("previousTip"));
        onChange(textButton, () -> root.previousTable());

        //empty space between the buttons
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
        thirdPartyPanel.captureKeyboardFocus();
    }

    @Override
    public void finishAnimation() {

    }
}
