package gdx.liftoff.ui.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.ray3k.stripe.CollapsibleGroup;
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.SmashGroup;
import gdx.liftoff.ui.dialogs.GradleDialog;
import gdx.liftoff.ui.dialogs.TemplatesDialog;

import static gdx.liftoff.Main.*;

public class PathsPanel extends Table implements Panel {

    public PathsPanel() {
        defaults().space(10);
        Label label = new Label(prop.getProperty("destinationPrompt"), skin, "field");
        label.setEllipsis("...");
        add(label).minWidth(0);

        addField("C:/users/yourname/gamename/");

        row();
        label = new Label(prop.getProperty("androidSdkPrompt"), skin, "field");
        label.setEllipsis("...");
        add(label).minWidth(0);

        addField("C:/users/yourname/android/");
    }

    private void addField(String text) {
        Stack stack = new Stack();
        add(stack).growX();

        Table chooseTable = new Table();
        stack.add(chooseTable);

        SmashGroup smashGroup = new SmashGroup(true);
        smashGroup.space(20);
        stack.add(smashGroup);

        TextButton browseFieldButton = new TextButton(text, skin, "field");
        browseFieldButton.getLabel().setAlignment(Align.left);
        browseFieldButton.getLabel().setEllipsis("...");
        browseFieldButton.getLabelCell().minWidth(0);
        smashGroup.setFirstActor(browseFieldButton);
        smashGroup.getFirstContainer().minWidth(50).prefWidth(150);

        TextButton browseButton = new TextButton(prop.getProperty("browse"), skin);
        smashGroup.setSecondActor(browseButton);

        Container chooseContainer = new Container();
        chooseContainer.setTouchable(Touchable.enabled);
        stack.add(chooseContainer);
        addHandListener(chooseContainer);
        onClick(chooseContainer, TemplatesDialog::show);
        chooseContainer.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                browseButton.fire(event);
                browseFieldButton.fire(event);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                browseButton.fire(event);
                browseFieldButton.fire(event);
            }
        });
    }

    @Override
    public void captureKeyboardFocus() {

    }
}
