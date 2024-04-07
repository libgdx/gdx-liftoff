package gdx.liftoff.ui.tables;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import gdx.liftoff.ui.LogoWidget;
import gdx.liftoff.ui.panels.AddOnsPanel;

import static gdx.liftoff.Main.*;

public class AddOnsTable extends LiftoffTable {
    public AddOnsTable() {
        populate();
    }

    private void populate() {
        clearChildren();
        setBackground(skin.getDrawable("black"));
        pad(20).padLeft(30).padRight(30);

        defaults().space(30);
        add(new LogoWidget()).top().spaceBottom(0);

        row();
        AddOnsPanel addOnsPanel = new AddOnsPanel();
        add(addOnsPanel).grow().spaceTop(0);

        row();
        Table table = new Table();
        add(table).bottom().growX();

        TextButton textButton = new TextButton(prop.getProperty("previous"), skin);
        table.add(textButton).uniformX().fillX();
        addHandListener(textButton);
        onChange(textButton, () -> root.previousTable());

        table.add().growX().space(5);

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
