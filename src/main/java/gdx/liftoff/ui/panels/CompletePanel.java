package gdx.liftoff.ui.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.ray3k.stripe.CollapsibleGroup;
import gdx.liftoff.ui.LogoWidget;

import static gdx.liftoff.Main.*;

public class CompletePanel extends Table implements Panel {
    private static final float TOOLTIP_WIDTH = 200;

    public CompletePanel() {
        defaults().space(10);
        LogoWidget logo = new LogoWidget();
        add(logo);

        row();
        Label label = new Label(prop.getProperty("complete"), skin, "header");
        add(label);

        row();
        Table scrollTable = new Table();
        scrollTable.pad(5);
        ScrollPane scrollPane = new ScrollPane(scrollTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setFlickScroll(false);
        scrollPane.setScrollingDisabled(true, false);
        add(scrollPane).growX().maxWidth(350).maxHeight(300);
        addScrollFocusListener(scrollPane);

        Label outputLabel = new Label(prop.getProperty("generationEnd") + "\n" + prop.getProperty("generationEnd"), skin, "description");
        outputLabel.setWrap(true);
        scrollTable.add(outputLabel).grow();

        row();
        TextButton textButton = new TextButton(prop.getProperty("newProject"), skin, "big");
        add(textButton);
        addTooltip(textButton, Align.top, TOOLTIP_WIDTH, prop.getProperty("newProjectTip"));
        addHandListener(textButton);
        onChange(textButton, () -> root.showHomeTable());

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
