package gdx.liftoff.ui.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import gdx.liftoff.ui.LogoWidget;

import static gdx.liftoff.Main.*;

public class CompleteButtonsPanel extends Table implements Panel {
    private static final float TOOLTIP_WIDTH = 200;

    public CompleteButtonsPanel() {
        defaults().space(10);
        TextButton textButton = new TextButton(prop.getProperty("newProject"), skin, "big");
        add(textButton);
        addTooltip(textButton, Align.top, TOOLTIP_WIDTH, prop.getProperty("newProjectTip"));
        addHandListener(textButton);
        onChange(textButton, () -> root.transitionTable(root.landingTable, true));

        row();
        Table table = new Table();
        add(table);

        table.defaults().fillX().space(10);
        textButton = new TextButton(prop.getProperty("openIdea"), skin);
        table.add(textButton);
        addHandListener(textButton);

        table.row();
        textButton = new TextButton(prop.getProperty("exit"), skin);
        table.add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> Gdx.app.exit());
    }

    @Override
    public void captureKeyboardFocus() {

    }
}
