package gdx.liftoff.ui.tables;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.ray3k.stripe.CollapsibleGroup;
import gdx.liftoff.ui.panels.AddOnsPanel;

import static gdx.liftoff.Main.*;
import static gdx.liftoff.ui.data.Data.liftoffVersion;

public class AddOnsTable extends LiftoffTable {
    public AddOnsTable() {
        populate();
    }

    private void populate() {
        clearChildren();
        setBackground(skin.getDrawable("black"));
        pad(20).padLeft(30).padRight(30);

        defaults().space(30);
        Table table = new Table();
        add(table).top().spaceBottom(0);

        CollapsibleGroup verticalCollapsibleGroup = new CollapsibleGroup(false);
        table.add(verticalCollapsibleGroup).minHeight(0);

        Container container = new Container();
        container.minSize(260, 25).maxSize(300, 35).prefWidth(300);
        verticalCollapsibleGroup.addActor(container);

        Image logoImage = new Image(skin, "title-small");
        logoImage.setScaling(Scaling.fit);
        container.setActor(logoImage);
        addTooltip(logoImage, Align.top, prop.getProperty("logoTip"));

        container = new Container();
        verticalCollapsibleGroup.addActor(container);

        table.row();
        verticalCollapsibleGroup = new CollapsibleGroup(false);
        table.add(verticalCollapsibleGroup).minWidth(0).padTop(10).right();

        container = new Container();
        container.padBottom(30);
        verticalCollapsibleGroup.addActor(container);

        Label label = new Label(liftoffVersion, skin);
        label.setEllipsis("...");
        container.setActor(label);

        container = new Container();
        verticalCollapsibleGroup.addActor(container);

        row();
        AddOnsPanel addOnsPanel = new AddOnsPanel();
        add(addOnsPanel).growX().growY().spaceTop(0);

        row();
        table = new Table();
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
