package gdx.liftoff.ui.liftofftables;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
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

    private void populate() {
        clearChildren();
        setBackground(skin.getDrawable("black"));
        pad(20).padLeft(30).padRight(30);

        //third party panel
        defaults().space(30);
        thirdPartyPanel = new ThirdPartyPanel();
        add(thirdPartyPanel).grow().spaceTop(0).maxHeight(550);

        row();
        Table table = new Table();
        add(table).bottom().growX();

        //previous button
        TextButton textButton = new TextButton(prop.getProperty("previous"), skin);
        table.add(textButton).uniformX().fillX();
        addHandListener(textButton);
        onChange(textButton, () -> root.previousTable());

        //empty space between the buttons
        table.add().growX().space(5);

        //next button
        textButton = new TextButton(prop.getProperty("next"), skin);
        table.add(textButton).uniformX().fillX();
        addHandListener(textButton);
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
