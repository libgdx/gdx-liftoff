package gdx.liftoff.ui.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.ray3k.stripe.CollapsibleGroup;
import gdx.liftoff.ui.LogoWidget;

import static gdx.liftoff.Main.*;

/**
 * The panel that displays the result of project generation
 */
public class CompletePanel extends Table implements Panel {
    public CompletePanel() {
        //logo
        defaults().space(10);
        LogoWidget logo = new LogoWidget();
        add(logo);

        //title
        row();
        Label label = new Label(prop.getProperty("complete"), skin, "header");
        add(label);

        //scrollable area includes the output label only
        row();
        Table scrollTable = new Table();
        scrollTable.pad(5);
        ScrollPane scrollPane = new ScrollPane(scrollTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setFlickScroll(false);
        scrollPane.setScrollingDisabled(true, false);
        add(scrollPane).growX().maxWidth(350).maxHeight(300);
        addScrollFocusListener(scrollPane);

        //output label
        Label outputLabel = new Label(prop.getProperty("generationEnd") + "\n" + prop.getProperty("generationEnd"), skin, "description");
        outputLabel.setWrap(true);
        scrollTable.add(outputLabel).grow();

        //scroll to the bottom
        addAction(Actions.delay(.3f, Actions.run(() -> {
            scrollPane.setScrollPercentY(1);
        })));
    }

    @Override
    public void captureKeyboardFocus() {

    }
}
