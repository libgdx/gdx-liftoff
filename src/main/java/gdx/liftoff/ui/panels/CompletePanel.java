package gdx.liftoff.ui.panels;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import gdx.liftoff.ui.LogoWidget;
import gdx.liftoff.ui.UserData;

import static gdx.liftoff.Main.*;

/**
 * The panel that displays the result of project generation
 */
public class CompletePanel extends Table implements Panel {
    public CompletePanel(boolean fullscreen) {
        populate(fullscreen);
    }

    @Override
    public void populate(boolean fullscreen) {
        //logo
        defaults().space(SPACE_MEDIUM);
        LogoWidget logo = new LogoWidget();
        add(logo);

        //title
        row();
        Label label = new Label(prop.getProperty("complete"), skin, "header");
        add(label);

        //scrollable area includes the output label only
        row();
        Table scrollTable = new Table();
        scrollTable.pad(SPACE_SMALL);
        ScrollPane scrollPane = new ScrollPane(scrollTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setFlickScroll(false);
        scrollPane.setScrollingDisabled(true, false);
        add(scrollPane).growX().maxWidth(350).maxHeight(300);
        addScrollFocusListener(scrollPane);

        //output label
        Label outputLabel = new Label(UserData.log, skin, "descriptionWithMarkup");
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
