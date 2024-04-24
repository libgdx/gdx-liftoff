package gdx.liftoff.ui.panels;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.ray3k.stripe.CollapsibleGroup;
import com.ray3k.stripe.CollapsibleGroup.CollapseType;

import static gdx.liftoff.Main.prop;
import static gdx.liftoff.Main.skin;

/**
 * The loading animation displayed before the project is generated
 */
public class GeneratingPanel extends Table implements Panel {
    public GeneratingPanel() {
        //animation image
        Image image = new Image(skin,  "loading-anim");
        add(image);

        row();
        CollapsibleGroup collapsibleGroup = new CollapsibleGroup(CollapseType.HORIZONTAL);
        add(collapsibleGroup).growX().spaceTop(30);

        //generating text big
        Label label = new Label(prop.getProperty("generating"), skin, "button-big");
        label.setAlignment(Align.center);
        collapsibleGroup.addActor(label);

        //generating text small
        label = new Label(prop.getProperty("generating"), skin, "button-mid");
        label.setAlignment(Align.center);
        collapsibleGroup.addActor(label);
    }

    @Override
    public void captureKeyboardFocus() {

    }
}
