package gdx.liftoff.ui.panels;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.ray3k.stripe.CollapsibleGroup;
import gdx.liftoff.ui.data.Data;
import gdx.liftoff.ui.dialogs.PlatformsDialog;

import static gdx.liftoff.Main.*;
import static gdx.liftoff.ui.data.Data.*;

public class AddOnsPanel extends Table {
    public AddOnsPanel() {
        Label label = new Label("ADD-ONS", skin, "header");
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

        label = new Label("PLATFORMS", skin, "field");
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

        label = new Label("LANGUAGES", skin, "field");
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

        //Extensions
        subTable = new Table();
        subTable.pad(10);
        subTable.setBackground(skin.getDrawable("button-outline-up-10"));
        table.add(subTable).prefWidth(150);

        label = new Label("EXTENSIONS", skin, "field");
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

        row();
        table = new Table();
        add(table).growX().spaceTop(20);

        table.defaults().space(20);
        label = new Label("TEMPLATE", skin, "field");
        table.add(label);

        TextButton textButton = new TextButton("CLASSIC", skin, "field");
        textButton.getLabel().setAlignment(Align.left);
        table.add(textButton).growX().minWidth(150);
        addHandListener(textButton);

        CollapsibleGroup collapsibleGroup = new CollapsibleGroup(true);
        table.add(collapsibleGroup).minWidth(0);

        textButton = new TextButton("CHOOSE", skin);
        collapsibleGroup.addActor(textButton);
        addHandListener(textButton);

        Container container = new Container();
        collapsibleGroup.addActor(container);
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
