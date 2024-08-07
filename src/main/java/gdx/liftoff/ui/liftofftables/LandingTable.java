package gdx.liftoff.ui.liftofftables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.ray3k.stripe.CollapsibleGroup;
import com.ray3k.stripe.CollapsibleGroup.CollapseType;
import gdx.liftoff.ui.panels.ProjectPanel;
import gdx.liftoff.ui.panels.SocialPanel;

import static com.badlogic.gdx.math.Interpolation.exp10Out;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static gdx.liftoff.Main.*;

/**
 * This table is the first table visible in the app. All the elements animate into view, but may be skipped by the user
 * clicking the interface. It contains the logo, subtitle, update version and link, project panel, a button to create
 * projects, and relevant links.
 */
public class LandingTable extends LiftoffTable {
    private Image logoImage;
    private Label subtitleLabel;
    private Label versionLabel;
    private Container<Actor> updateContainer;
    private TextButton updateButton;
    private ProjectPanel projectPanel;
    private Table buttonsTable;
    private SocialPanel socialPanel;
    private Action animationAction;
    private TextButton bigOptionsButton;
    private TextButton smallOptionsButton;

    public LandingTable() {
        populate();
    }

    @Override
    public void populate() {
        clearChildren();
        setBackground(skin.getDrawable("black"));
        pad(SPACE_LARGE).padLeft(SPACE_HUGE).padRight(SPACE_HUGE);

        defaults().space(SPACE_HUGE);
        Table table = new Table();
        add(table);

        //logo
        logoImage = new Image(skin, "title-small");
        logoImage.setScaling(Scaling.fit);
        table.add(logoImage).minSize(270, 30).maxHeight(50);
        addTooltip(logoImage, Align.top, prop.getProperty("logoTip"));

        table.row();
        CollapsibleGroup verticalCollapsibleGroup = new CollapsibleGroup(CollapseType.VERTICAL);
        table.add(verticalCollapsibleGroup).minWidth(0);

        table = new Table();
        table.padTop(SPACE_LARGE);
        verticalCollapsibleGroup.addActor(table);

        Stack stack = new Stack();
        table.add(stack).minWidth(0);

        //subtitle
        subtitleLabel = new Label(prop.getProperty("subtitle"), skin);
        subtitleLabel.setEllipsis("...");
        subtitleLabel.setAlignment(Align.center);
        subtitleLabel.setVisible(false);
        stack.add(subtitleLabel);

        //version
        versionLabel = new Label("v" + prop.getProperty("liftoffVersion"), skin);
        versionLabel.setEllipsis("...");
        versionLabel.setAlignment(Align.center);
        stack.add(versionLabel);

        //update link
        table.row();
        updateContainer = new Container<>();
        table.add(updateContainer);

        updateButton = new TextButton(prop.getProperty("updateAvailable"), skin, "link");
        checkSetupVersion();
        updateButton.setColor(CLEAR_WHITE);
        updateButton.setDisabled(false);
        updateContainer.setActor(updateButton);

        //project panel
        row();
        projectPanel = new ProjectPanel(false);
        add(projectPanel).growX().maxWidth(400);

        row();
        buttonsTable = new Table();
        add(buttonsTable).top();

        CollapsibleGroup horizontalCollapsibleGroup = new CollapsibleGroup(CollapseType.HORIZONTAL);
        buttonsTable.add(horizontalCollapsibleGroup);

        //project options horizontally big button
        bigOptionsButton = new TextButton(prop.getProperty("projectOptions"), skin, "big");
        horizontalCollapsibleGroup.addActor(bigOptionsButton);
        addNewProjectListeners(bigOptionsButton);
        addTooltip(bigOptionsButton, Align.top, TOOLTIP_WIDTH, prop.getProperty("newProjectTip"));
        onChange(bigOptionsButton, () -> root.nextTable());

        //options horizontally small button
        smallOptionsButton = new TextButton(prop.getProperty("options"), skin, "big");
        horizontalCollapsibleGroup.addActor(smallOptionsButton);
        addNewProjectListeners(smallOptionsButton);
        addTooltip(smallOptionsButton, Align.top, TOOLTIP_WIDTH, prop.getProperty("newProjectTip"));
        onChange(smallOptionsButton, () -> root.nextTable());

        updateOptionsButtons();

        row();
        socialPanel = new SocialPanel(false);
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

    public void animate() {
        //animation initial setup
        logoImage.setColor(CLEAR_WHITE);
        subtitleLabel.setText(prop.getProperty("subtitle"));
        subtitleLabel.setColor(CLEAR_WHITE);
        subtitleLabel.setVisible(true);
        versionLabel.setColor(CLEAR_WHITE);
        updateContainer.setColor(CLEAR_WHITE);
        projectPanel.setColor(CLEAR_WHITE);
        buttonsTable.setColor(CLEAR_WHITE);
        socialPanel.setColor(CLEAR_WHITE);
        bgImage.setColor(CLEAR_WHITE);
        float offsetAmount = 150f;
        setTouchable(Touchable.disabled);
        stage.setKeyboardFocus(null);

        //animation
        animationAction = sequence(
            //setup on the first frame
            run(() -> {
                logoImage.moveBy(0, offsetAmount);
                projectPanel.moveBy(offsetAmount, 0);
                buttonsTable.moveBy(-offsetAmount, 0);
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
                targeting(buttonsTable, delay(.3f, parallel(
                    targeting(buttonsTable, fadeIn(1f)),
                    targeting(buttonsTable, Actions.moveBy(offsetAmount, 0, 1f, exp10Out))
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
                        targeting(updateContainer, delay(.35f, targeting(updateContainer, fadeIn(.5f))))
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
            bgImage.setColor(Color.WHITE);
            removeAction(animationAction);
            populate();
            setTouchable(Touchable.childrenOnly);
            captureKeyboardFocus();
        }
    }

    public void animateUpdateLabel() {
        updateButton.setColor(CLEAR_WHITE);
        updateButton.setDisabled(false);
        addHandListener(updateButton);
        addTooltip(updateButton, Align.bottom, prop.getProperty("updateTip"));
        onChange(updateButton, () -> Gdx.net.openURI(prop.getProperty("updateUrl")));
        addAction(targeting(updateButton, fadeIn(.5f)));
    }

    public void updateOptionsButtons() {
        boolean valid = validateUserProjectData();
        bigOptionsButton.setDisabled(!valid);
        smallOptionsButton.setDisabled(!valid);
    }
}
