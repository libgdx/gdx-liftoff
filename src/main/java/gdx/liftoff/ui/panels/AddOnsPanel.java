package gdx.liftoff.ui.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.ray3k.stripe.CollapsibleGroup;
import com.ray3k.stripe.SmashGroup;
import gdx.liftoff.ui.dialogs.ExtensionsDialog;
import gdx.liftoff.ui.dialogs.LanguagesDialog;
import gdx.liftoff.ui.dialogs.PlatformsDialog;
import gdx.liftoff.ui.dialogs.TemplatesDialog;

import static gdx.liftoff.Main.*;
import static gdx.liftoff.ui.data.Data.*;

public class AddOnsPanel extends Table {
    public AddOnsPanel() {
        Label label = new Label(prop.getProperty("add-ons"), skin, "header");
        add(label).space(30);

        row();
        Table table = new Table();
        add(table).grow();

        //Platforms
        table.defaults().space(10).grow().minHeight(150);
        Table subTable = new Table();
        subTable.pad(10);
        subTable.setBackground(skin.getDrawable("button-outline-up-10"));
        table.add(subTable).prefWidth(150);

        label = new Label(prop.getProperty("platforms"), skin, "field");
        label.setEllipsis("...");
        subTable.add(label).minWidth(0);

        subTable.row();
        Table scrollTable = new Table();
        ScrollPane scrollPane = new ScrollPane(scrollTable, skin);
        subTable.add(scrollPane).grow().padTop(10);
        populateAddOnTable(scrollTable, platformsNames);

        subTable.row();
        Button button = new Button(skin, "plus");
        subTable.add(button).right().padRight(5);
        addHandListener(button);
        onChange(button, PlatformsDialog::show);

        //Languages
        subTable = new Table();
        subTable.pad(10);
        subTable.setBackground(skin.getDrawable("button-outline-up-10"));
        table.add(subTable).prefWidth(150);

        label = new Label(prop.getProperty("languages"), skin, "field");
        label.setEllipsis("...");
        subTable.add(label).minWidth(0);

        subTable.row();
        scrollTable = new Table();
        scrollPane = new ScrollPane(scrollTable, skin);
        subTable.add(scrollPane).grow().padTop(10);
        populateAddOnTable(scrollTable, languagesNames);

        subTable.row();
        button = new Button(skin, "plus");
        subTable.add(button).right().padRight(5);
        addHandListener(button);
        onChange(button, LanguagesDialog::show);

        //Extensions
        subTable = new Table();
        subTable.pad(10);
        subTable.setBackground(skin.getDrawable("button-outline-up-10"));
        table.add(subTable).prefWidth(150);

        label = new Label(prop.getProperty("extensions"), skin, "field");
        label.setEllipsis("...");
        subTable.add(label).minWidth(0);

        subTable.row();
        scrollTable = new Table();
        scrollPane = new ScrollPane(scrollTable, skin);
        subTable.add(scrollPane).grow().padTop(10);
        populateAddOnTable(scrollTable, extensionsNames);

        subTable.row();
        button = new Button(skin, "plus");
        subTable.add(button).right().padRight(5);
        addHandListener(button);
        onChange(button, ExtensionsDialog::show);

        //template
        row();
        table = new Table();
        add(table).growX().spaceTop(20);

        label = new Label(prop.getProperty("template"), skin, "field");
        table.add(label).space(20);

        Stack stack = new Stack();
        table.add(stack).growX();

        Table chooseTable = new Table();
        stack.add(chooseTable);

        SmashGroup smashGroup = new SmashGroup(true);
        smashGroup.space(20);
        stack.add(smashGroup);

        TextButton chooseFieldButton = new TextButton("CLASSIC", skin, "field");
        chooseFieldButton.getLabel().setAlignment(Align.left);
        smashGroup.setFirstActor(chooseFieldButton);
        smashGroup.getFirstContainer().minWidth(150);

        TextButton chooseButton = new TextButton(prop.getProperty("choose"), skin);
        smashGroup.setSecondActor(chooseButton);

        Container chooseContainer = new Container();
        chooseContainer.setTouchable(Touchable.enabled);
        stack.add(chooseContainer);
        addHandListener(chooseContainer);
        onClick(chooseContainer, TemplatesDialog::show);
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

    private void populateAddOnTable(Table table, Array<String> names) {
        table.clearChildren();
        table.top();

        table.defaults().growX().space(5);
        for (String name : names) {
            Label label = new Label(name, skin);
            table.add(label);
            table.row();
        }
    }
}
