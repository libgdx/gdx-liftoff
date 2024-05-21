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
 * clicking the interface. It contains the logo, subtitle, update version and link, project planel, buttons to create
 * projects, and relevant links.
 */
public class LandingTable extends LiftoffTable {
    private Image logoImage;
    private Label subtitleLabel;
    private Label versionLabel;
    private Container updateContainer;
    private TextButton updateButton;
    private ProjectPanel projectPanel;
    private CollapsibleGroup buttonsCollapsibleGroup;
    private SocialPanel socialPanel;
    private Action animationAction;

    public LandingTable() {
        populate();
    }

    @Override
    public void populate() {
        clearChildren();
        setBackground(skin.getDrawable("black"));
        pad(SPACE_LARGE).padLeft(SPACE_HUGE).padRight(SPACE_HUGE);

        defaults().space(30).expandY();
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
        updateContainer = new Container();
        updateContainer.setColor(CLEAR_WHITE);
        table.add(updateContainer);

        updateButton = new TextButton(prop.getProperty("updateAvailable"), skin, "link");
        updateButton.setColor(CLEAR_WHITE);
        updateButton.setDisabled(true);
        updateContainer.setActor(updateButton);
        addHandListener(updateButton);
        addTooltip(updateButton, Align.bottom, prop.getProperty("updateTip"));
        onChange(updateButton, () -> Gdx.net.openURI(prop.getProperty("updateUrl")));

        Container container = new Container();
        verticalCollapsibleGroup.addActor(container);

        //project panel
        row();
        projectPanel = new ProjectPanel(false);
        add(projectPanel).growX().maxWidth(400);

        row();
        buttonsCollapsibleGroup = new CollapsibleGroup(CollapseType.VERTICAL);
        add(buttonsCollapsibleGroup);

        //begin big vertical group
        table = new Table();
        buttonsCollapsibleGroup.addActor(table);

        CollapsibleGroup horizontalCollapsibleGroup = new CollapsibleGroup(CollapseType.HORIZONTAL);
        table.add(horizontalCollapsibleGroup);

        //create new project vertically big button
        TextButton textButton = new TextButton(prop.getProperty("projectOptions"), skin, "big");
        horizontalCollapsibleGroup.addActor(textButton);
        addNewProjectListeners(textButton);
        addTooltip(textButton, Align.top, TOOLTIP_WIDTH, prop.getProperty("newProjectTip"));
        onChange(textButton, () -> root.nextTable());

        //new project vertically big button
        textButton = new TextButton(prop.getProperty("options"), skin, "mid");
        horizontalCollapsibleGroup.addActor(textButton);
        addNewProjectListeners(textButton);
        addTooltip(textButton, Align.top, TOOLTIP_WIDTH, prop.getProperty("newProjectTip"));
        onChange(textButton, () -> root.nextTable());

        //quick project button
        table.row();
        textButton = new TextButton(prop.getProperty("quickProject"), skin, "mid");
        table.add(textButton).fillX().space(SPACE_LARGE);
        addQuickProjectListeners(textButton);
        addTooltip(textButton, Align.top, TOOLTIP_WIDTH, prop.getProperty("quickProjectTip"));
        onChange(textButton, () -> {
            setQuickProjectDefaultUserData();
            root.transitionTable(root.quickSettingsTable, true);
        });

        //begin small vertical group
        table = new Table();
        buttonsCollapsibleGroup.addActor(table);

        //new project vertically small button
        table.defaults().uniformX().fillX();
        textButton = new TextButton(prop.getProperty("options"), skin);
        table.add(textButton);
        addNewProjectListeners(textButton);
        addTooltip(textButton, Align.top, TOOLTIP_WIDTH, prop.getProperty("newProjectTip"));
        onChange(textButton, () -> root.nextTable());

        //quick project vertically small button
        table.row();
        textButton = new TextButton(prop.getProperty("quickProject"), skin);
        table.add(textButton).space(SPACE_LARGE);
        addQuickProjectListeners(textButton);
        addTooltip(textButton, Align.top, TOOLTIP_WIDTH, prop.getProperty("quickProjectTip"));
        onChange(textButton, () -> root.transitionTable(root.quickSettingsTable, true));
        //end vertical groups

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

    private void addQuickProjectListeners(Actor actor) {
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
        buttonsCollapsibleGroup.setColor(CLEAR_WHITE);
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
        addAction(targeting(updateButton, fadeIn(.5f)));
    }
}
