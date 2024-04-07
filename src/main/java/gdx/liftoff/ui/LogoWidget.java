package gdx.liftoff.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.ray3k.stripe.CollapsibleGroup;

import static gdx.liftoff.Main.*;
import static gdx.liftoff.Main.skin;
import static gdx.liftoff.ui.data.Data.liftoffVersion;

public class LogoWidget extends Table {
    public LogoWidget() {
        CollapsibleGroup verticalCollapsibleGroup = new CollapsibleGroup(false);
        add(verticalCollapsibleGroup).minHeight(0);

        Container container = new Container();
        container.minSize(260, 25).maxSize(300, 35).prefWidth(300);
        verticalCollapsibleGroup.addActor(container);

        Image logoImage = new Image(skin, "title-small");
        logoImage.setScaling(Scaling.fit);
        container.setActor(logoImage);
        addTooltip(logoImage, Align.top, prop.getProperty("logoTip"));

        container = new Container();
        verticalCollapsibleGroup.addActor(container);

        row();
        verticalCollapsibleGroup = new CollapsibleGroup(false);
        add(verticalCollapsibleGroup).minWidth(0).right();

        container = new Container();
        container.padBottom(30).padTop(10);
        verticalCollapsibleGroup.addActor(container);

        Label label = new Label(liftoffVersion, skin);
        label.setEllipsis("...");
        container.setActor(label);

        container = new Container();
        verticalCollapsibleGroup.addActor(container);
    }
}
