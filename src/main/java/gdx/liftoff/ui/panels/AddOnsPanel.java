package gdx.liftoff.ui.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.ray3k.stripe.SmashGroup;
import gdx.liftoff.ui.UserData;
import gdx.liftoff.ui.dialogs.*;

import java.util.Collection;
import java.util.Locale;

import static gdx.liftoff.Main.*;

/**
 * The table including the lists of platforms, languages, and extensions as well as a button to choose the template
 */
public class AddOnsPanel extends Table implements Panel {
    public AddOnsPanel(boolean fullscreen) {
        populate(fullscreen);
    }

    public void populate(boolean fullscreen) {
        clearChildren();
        Label label = new Label(prop.getProperty("add-ons"), skin, "header");
        add(label).space(SPACE_HUGE);

        row();
        Table table = new Table();
        add(table).grow();

        //platforms
        table.defaults().space(SPACE_MEDIUM).grow().minHeight(150).minWidth(90);
        Button button = new Button(skin, "card-plus");
        table.add(button).prefWidth(150);
        addHandListener(button);
        addTooltip(button, Align.top, prop.getProperty("platformsTip"));
        onChange(button, () -> PlatformsDialog.show(fullscreen, () -> {
            populate(fullscreen);
            if (fullscreen && FullscreenDialog.fullscreenDialog != null) {
                FullscreenDialog.fullscreenDialog.populate();
            }
        }));

        //platforms title
        label = new Label(prop.getProperty("platforms"), skin, "field");
        label.setEllipsis("...");
        button.add(label).minWidth(0);

        //platforms list
        button.row();
        Table scrollTable = new Table();
        ScrollPane scrollPane = new ScrollPane(scrollTable, skin);
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        button.add(scrollPane).grow().padTop(SPACE_MEDIUM);
        createButtons(scrollTable, UserData.platforms, true);
        addScrollFocusListener(scrollPane);

        //languages
        button = new Button(skin, "card-plus");
        table.add(button).prefWidth(150);
        addHandListener(button);
        addTooltip(button, Align.top, prop.getProperty("languagesTip"));
        onChange(button, () -> LanguagesDialog.show(fullscreen, () -> {
            populate(fullscreen);
            if (fullscreen && FullscreenDialog.fullscreenDialog != null) {
                FullscreenDialog.fullscreenDialog.populate();
            }
        }));

        //languages title
        label = new Label(prop.getProperty("languages"), skin, "field");
        label.setEllipsis("...");
        button.add(label).minWidth(0);

        //languages list
        button.row();
        scrollTable = new Table();
        scrollPane = new ScrollPane(scrollTable, skin);
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        button.add(scrollPane).grow().padTop(SPACE_MEDIUM);
        createButtons(scrollTable, UserData.languages, true);
        addScrollFocusListener(scrollPane);

        //extensions
        button = new Button(skin, "card-plus");
        table.add(button).prefWidth(150);
        addHandListener(button);
        addTooltip(button, Align.top, prop.getProperty("extensionsTip"));
        onChange(button, () -> ExtensionsDialog.show(fullscreen, () -> {
            populate(fullscreen);
            if (fullscreen && FullscreenDialog.fullscreenDialog != null) {
                FullscreenDialog.fullscreenDialog.populate();
            }
        }));

        //extensions title
        label = new Label(prop.getProperty("extensions"), skin, "field");
        label.setEllipsis("...");
        button.add(label).minWidth(0);

        //extensions list
        button.row();
        scrollTable = new Table();
        scrollPane = new ScrollPane(scrollTable, skin);
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        button.add(scrollPane).grow().padTop(SPACE_MEDIUM);
        createButtons(scrollTable, UserData.extensions, true);
        addScrollFocusListener(scrollPane);

        //template
        row();
        table = new Table();
        add(table).growX().spaceTop(SPACE_LARGE);

        //template title
        label = new Label(prop.getProperty("template"), skin, "field");
        table.add(label).space(SPACE_LARGE);
        addTooltip(label, Align.top, TOOLTIP_WIDTH, prop.getProperty("templateTip"));

        Stack stack = new Stack();
        table.add(stack).growX();

        Table chooseTable = new Table();
        stack.add(chooseTable);

        SmashGroup smashGroup = new SmashGroup(true);
        smashGroup.space(SPACE_LARGE);
        stack.add(smashGroup);

        //template button
        TextButton chooseFieldButton = new TextButton(prop.getProperty(UserData.template), skin, "select");
        chooseFieldButton.getLabel().setAlignment(Align.left);
        smashGroup.setFirstActor(chooseFieldButton);
        smashGroup.getFirstContainer().minWidth(150);
        addTooltip(chooseFieldButton, label, Align.top, TOOLTIP_WIDTH, prop.getProperty("templateTip"));

        //template choose button
        TextButton chooseButton = new TextButton(prop.getProperty("choose"), skin);
        smashGroup.setSecondActor(chooseButton);
        addTooltip(chooseButton, label, Align.top, TOOLTIP_WIDTH, prop.getProperty("templateTip"));

        Container<Actor> chooseContainer = new Container<>();
        chooseContainer.setTouchable(Touchable.enabled);
        stack.add(chooseContainer);
        addHandListener(chooseContainer);
        onClick(chooseContainer, () -> TemplatesDialog.show(fullscreen, () -> populate(fullscreen)));
        chooseContainer.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                chooseButton.fire(event);
                chooseFieldButton.fire(event);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                chooseButton.fire(event);
                chooseFieldButton.fire(event);
            }
        });
    }

    /**
     * Convenience method to populate each add-ons button with a list of add-ons
     *
     * @param table
     * @param names
     */
    private void createButtons(Table table, Collection<String> names, boolean localize) {
        table.clearChildren();
        table.top();

        table.defaults().growX().space(SPACE_SMALL);
        for (String name : names) {
            name = localize ? prop.getProperty(name, name) : name;
            Label label = new Label(name, skin);
            label.setEllipsis("...");
            table.add(label).minWidth(0).prefWidth(0).growX();
            table.row();
        }
    }

    @Override
    public void captureKeyboardFocus() {

    }
}
