package gdx.liftoff.ui.tables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.ray3k.stripe.CollapsibleGroup;
import gdx.liftoff.ui.panels.ProjectPanel;
import gdx.liftoff.ui.panels.SocialPanel;

import static com.badlogic.gdx.math.Interpolation.exp10Out;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static gdx.liftoff.Main.*;
import static gdx.liftoff.ui.data.Data.*;

public class LandingTable extends Table {
    private Image logoImage;
    private Label subtitleLabel;
    private TextButton updateButton;
    private ProjectPanel projectPanel;
    private CollapsibleGroup buttonsCollapsibleGroup;
    private SocialPanel socialPanel;
    private static final float TOOLTIP_WIDTH = 200;

    public LandingTable() {
        setBackground(skin.getDrawable("black"));
        pad(20).padLeft(30).padRight(30);

        defaults().space(30).expandY();
        Table table = new Table();
        add(table);

        logoImage = new Image(skin, "title-small");
        logoImage.setScaling(Scaling.fit);
        table.add(logoImage).minSize(270, 30).maxHeight(50);
        addTooltip(logoImage, Align.top, logoTooltipDescription);

        table.row();
        CollapsibleGroup verticalCollapsibleGroup = new CollapsibleGroup(false);
        table.add(verticalCollapsibleGroup).minWidth(0);

        table = new Table();
        table.padTop(20);
        verticalCollapsibleGroup.addActor(table);

        subtitleLabel = new Label(liftoffVersion, skin);
        subtitleLabel.setEllipsis("...");
        table.add(subtitleLabel).minWidth(0);

        table.row();
        updateButton = new TextButton("UPDATE AVAILABLE", skin, "link");
        table.add(updateButton);
        addHandListener(updateButton);
        addTooltip(updateButton, Align.bottom, updateTooltipDescription);
        onChange(updateButton, () -> Gdx.net.openURI(updateUrl));

        Container container = new Container();
        verticalCollapsibleGroup.addActor(container);

        row();
        projectPanel = new ProjectPanel();
        add(projectPanel).growX();

        row();
        buttonsCollapsibleGroup = new CollapsibleGroup(false);
        add(buttonsCollapsibleGroup);

        //begin big vertical group
        table = new Table();
        buttonsCollapsibleGroup.addActor(table);

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
        buttonsCollapsibleGroup.addActor(table);

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
        socialPanel = new SocialPanel();
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

    public void animate() {
        //initial setup
        logoImage.setColor(CLEAR_WHITE);
        subtitleLabel.setText(subtitle);
        subtitleLabel.setColor(CLEAR_WHITE);
        updateButton.setColor(CLEAR_WHITE);
        projectPanel.setColor(CLEAR_WHITE);
        buttonsCollapsibleGroup.setColor(CLEAR_WHITE);
        socialPanel.setColor(CLEAR_WHITE);
        bgImage.setColor(CLEAR_WHITE);
        float offsetAmount = 150f;
        Gdx.input.setInputProcessor(null);
        stage.setKeyboardFocus(null);

        Action action = sequence(
            //setup on the first frame
            run(() -> {
                logoImage.moveBy(0, offsetAmount);
                projectPanel.moveBy(offsetAmount, 0);
                buttonsCollapsibleGroup.moveBy(-offsetAmount, 0);
                socialPanel.moveBy(offsetAmount, 0);
            }),
            //fade in bg image
            targeting(bgImage, fadeIn(.5f, exp10Out)),
            //fade in/translate logo image
            parallel(
                targeting(logoImage, fadeIn(1f)),
                targeting(logoImage, Actions.moveBy(0, -offsetAmount, 1f, exp10Out))
            ),
            //fade in subtitle
            targeting(subtitleLabel, fadeIn(.5f)),
            delay(.25f),
            parallel(
                //fade in project panel
                targeting(projectPanel, delay(0, parallel(
                    targeting(projectPanel, fadeIn(.5f)),
                    targeting(projectPanel, Actions.moveBy(-offsetAmount, 0, 1f, exp10Out))
                ))),

                //fade in buttons
                targeting(buttonsCollapsibleGroup, delay(.3f, parallel(
                    targeting(buttonsCollapsibleGroup, fadeIn(1f)),
                    targeting(buttonsCollapsibleGroup, Actions.moveBy(offsetAmount, 0, 1f, exp10Out))
                ))),

                //fade in social panel
                targeting(socialPanel, delay(.6f, parallel(
                    targeting(socialPanel, fadeIn(1f)),
                    targeting(socialPanel, Actions.moveBy(-offsetAmount, 0, 1f, exp10Out))
                )))
            ),
            //reset input
            run(() -> {
                projectPanel.captureKeyboardFocus();
                Gdx.input.setInputProcessor(stage);
            }),
            //fade transition subtitle to version
            delay(1.5f),
            targeting(subtitleLabel, fadeOut(.5f)),
            run(() -> subtitleLabel.setText(liftoffVersion)),
            parallel(
                targeting(subtitleLabel, fadeIn(.5f)),
                targeting(updateButton, fadeIn(.5f))
            )
        );
        addAction(action);
    }
}
