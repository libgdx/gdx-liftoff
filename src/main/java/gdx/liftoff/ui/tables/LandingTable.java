package gdx.liftoff.ui.tables;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.ray3k.stripe.CollapsibleGroup;
import gdx.liftoff.ui.panels.ProjectPanel;
import gdx.liftoff.ui.panels.SocialPanel;

import static gdx.liftoff.Main.*;
import static gdx.liftoff.ui.data.Data.*;

public class LandingTable extends Table {
    private ProjectPanel projectPanel;
    private static final float TOOLTIP_WIDTH = 200;

    public LandingTable() {
        setBackground(skin.getDrawable("black"));
        pad(20).padLeft(30).padRight(30);

        defaults().space(30).expandY();
        Table table = new Table();
        add(table);

        Image image = new Image(skin, "title-small");
        image.setScaling(Scaling.fit);
        table.add(image).minSize(270, 30).maxHeight(50);
        addTooltip(image, Align.top, logoTooltipDescription);

        table.row();
        CollapsibleGroup verticalCollapsibleGroup = new CollapsibleGroup(false);
        table.add(verticalCollapsibleGroup).minWidth(0);

        Container container = new Container();
        container.padTop(20);
        container.minWidth(0);
        verticalCollapsibleGroup.addActor(container);

        Label label = new Label("a modern setup tool for libGDX Gradle projects", skin);
        label.setEllipsis("...");
        container.setActor(label);

        container = new Container();
        verticalCollapsibleGroup.addActor(container);

        row();
        projectPanel = new ProjectPanel();
        add(projectPanel).growX();

        row();
        verticalCollapsibleGroup = new CollapsibleGroup(false);
        add(verticalCollapsibleGroup);

        //begin big vertical group
        table = new Table();
        verticalCollapsibleGroup.addActor(table);

        CollapsibleGroup horizontalCollapsibleGroup = new CollapsibleGroup(true);
        table.add(horizontalCollapsibleGroup);

        TextButton textButton = new TextButton("CREATE NEW PROJECT", skin, "big");
        horizontalCollapsibleGroup.addActor(textButton);
        addNewProjectListeners(textButton);
        addTooltip(textButton, Align.top, TOOLTIP_WIDTH, newProjectTooltipDescription);

        textButton = new TextButton("NEW PROJECT", skin, "mid");
        horizontalCollapsibleGroup.addActor(textButton);
        addNewProjectListeners(textButton);
        addTooltip(textButton, Align.top, TOOLTIP_WIDTH, newProjectTooltipDescription);

        table.row();
        textButton = new TextButton("QUICK PROJECT", skin, "mid");
        table.add(textButton).fillX().space(20);
        addQuickProjectListeners(textButton);
        addTooltip(textButton, Align.top, TOOLTIP_WIDTH, quickProjectTooltipDescription);

        //begin small vertical group
        table = new Table();
        verticalCollapsibleGroup.addActor(table);

        table.defaults().uniformX().fillX();
        textButton = new TextButton("NEW PROJECT", skin);
        table.add(textButton);
        addNewProjectListeners(textButton);
        addTooltip(textButton, Align.top, TOOLTIP_WIDTH, newProjectTooltipDescription);

        table.row();
        textButton = new TextButton("QUICK PROJECT", skin);
        table.add(textButton).space(20);
        addQuickProjectListeners(textButton);
        addTooltip(textButton, Align.top, TOOLTIP_WIDTH, quickProjectTooltipDescription);
        //end vertical groups

        row();
        SocialPanel socialPanel = new SocialPanel();
        add(socialPanel).right();
    }

    public void captureKeyboardFocus() {
        projectPanel.captureKeyboardFocus();
    }

    private void addNewProjectListeners(Actor actor) {
        addHandListener(actor);
        onChange(actor, () -> {

        });
    }

    private void addQuickProjectListeners(Actor actor) {
        addHandListener(actor);
        onChange(actor, () -> {

        });
    }
}
