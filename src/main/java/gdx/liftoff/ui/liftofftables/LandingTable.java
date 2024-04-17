package gdx.liftoff.ui.liftofftables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
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

public class LandingTable extends LiftoffTable {
    private Image logoImage;
    private Label subtitleLabel;
    private Label versionLabel;
    private TextButton updateButton;
    private ProjectPanel projectPanel;
    private CollapsibleGroup buttonsCollapsibleGroup;
    private SocialPanel socialPanel;
    private static final float TOOLTIP_WIDTH = 200;
    private Action animationAction;

    public LandingTable() {
        populate();
    }

    private void populate() {
        clearChildren();
        setBackground(skin.getDrawable("black"));
        pad(20).padLeft(30).padRight(30);

        defaults().space(30).expandY();
        Table table = new Table();
        add(table);

        logoImage = new Image(skin, "title-small");
        logoImage.setScaling(Scaling.fit);
        table.add(logoImage).minSize(270, 30).maxHeight(50);
        addTooltip(logoImage, Align.top, prop.getProperty("logoTip"));

        table.row();
        CollapsibleGroup verticalCollapsibleGroup = new CollapsibleGroup(false);
        table.add(verticalCollapsibleGroup).minWidth(0);

        table = new Table();
        table.padTop(20);
        verticalCollapsibleGroup.addActor(table);

        Stack stack = new Stack();
        table.add(stack).minWidth(0);

        subtitleLabel = new Label(prop.getProperty("subtitle"), skin);
        subtitleLabel.setEllipsis("...");
        subtitleLabel.setAlignment(Align.center);
        subtitleLabel.setVisible(false);
        stack.add(subtitleLabel);

        versionLabel = new Label(liftoffVersion, skin);
        versionLabel.setEllipsis("...");
        versionLabel.setAlignment(Align.center);
        stack.add(versionLabel);

        table.row();
        updateButton = new TextButton(prop.getProperty("updateAvailable"), skin, "link");
        table.add(updateButton);
        addHandListener(updateButton);
        addTooltip(updateButton, Align.bottom, prop.getProperty("updateTip"));
        onChange(updateButton, () -> Gdx.net.openURI(prop.getProperty("updateUrl")));

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

        TextButton textButton = new TextButton(prop.getProperty("createNewProject"), skin, "big");
        horizontalCollapsibleGroup.addActor(textButton);
        addNewProjectListeners(textButton);
        addTooltip(textButton, Align.top, TOOLTIP_WIDTH, prop.getProperty("newProjectTip"));
        onChange(textButton, () -> root.nextTable());

        textButton = new TextButton(prop.getProperty("newProject"), skin, "mid");
        horizontalCollapsibleGroup.addActor(textButton);
        addNewProjectListeners(textButton);
        addTooltip(textButton, Align.top, TOOLTIP_WIDTH, prop.getProperty("newProjectTip"));
        onChange(textButton, () -> root.nextTable());

        table.row();
        textButton = new TextButton(prop.getProperty("quickProject"), skin, "mid");
        table.add(textButton).fillX().space(20);
        addQuickProjectListeners(textButton);
        addTooltip(textButton, Align.top, TOOLTIP_WIDTH, prop.getProperty("quickProjectTip"));
        onChange(textButton, () -> root.transitionTable(root.quickSettingsTable, true));

        //begin small vertical group
        table = new Table();
        buttonsCollapsibleGroup.addActor(table);

        table.defaults().uniformX().fillX();
        textButton = new TextButton(prop.getProperty("newProject"), skin);
        table.add(textButton);
        addNewProjectListeners(textButton);
        addTooltip(textButton, Align.top, TOOLTIP_WIDTH, prop.getProperty("newProjectTip"));
        onChange(textButton, () -> root.nextTable());

        table.row();
        textButton = new TextButton(prop.getProperty("quickProject"), skin);
        table.add(textButton).space(20);
        addQuickProjectListeners(textButton);
        addTooltip(textButton, Align.top, TOOLTIP_WIDTH, prop.getProperty("quickProjectTip"));
        onChange(textButton, () -> root.transitionTable(root.quickSettingsTable, true));
        //end vertical groups

        row();
        socialPanel = new SocialPanel();
        add(socialPanel).right();
    }

    @Override
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
        subtitleLabel.setText(prop.getProperty("subtitle"));
        subtitleLabel.setColor(CLEAR_WHITE);
        subtitleLabel.setVisible(true);
        versionLabel.setColor(CLEAR_WHITE);
        updateButton.setColor(CLEAR_WHITE);
        projectPanel.setColor(CLEAR_WHITE);
        buttonsCollapsibleGroup.setColor(CLEAR_WHITE);
        socialPanel.setColor(CLEAR_WHITE);
        bgImage.setColor(CLEAR_WHITE);
        float offsetAmount = 150f;
        setTouchable(Touchable.disabled);
        stage.setKeyboardFocus(null);

        animationAction = sequence(
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
                ))),

                //fade transition subtitle to version
                targeting(subtitleLabel, delay(.5f, sequence(
                    targeting(subtitleLabel, fadeOut(.5f)),
                    targeting(subtitleLabel, visible(false)),
                    parallel(
                        targeting(versionLabel, fadeIn(.5f)),
                        targeting(updateButton, delay(.35f, targeting(updateButton, fadeIn(.5f))))
                    )
                )))
            ),
            //reset input
            run(() -> {
                setTouchable(Touchable.childrenOnly);
                projectPanel.captureKeyboardFocus();
            })
        );
        Gdx.app.postRunnable(() -> addAction(animationAction));
    }

    @Override
    public void finishAnimation() {
        if (animationAction != null && getActions().contains(animationAction, true)) {
            removeAction(animationAction);
            populate();
            setTouchable(Touchable.childrenOnly);
            captureKeyboardFocus();
        }
    }
}
