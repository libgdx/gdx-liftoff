package com.github.czyzby.lml.scene2d.ui.reflected;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/** A specialized {@link Table} that manages an internal {@link ButtonGroup}. All children that extend {@link Button}
 * class are automatically appended to the button group instance; appends other children like a regular table. Group is
 * accessible through {@link #getButtonGroup()} and cannot be changed.
 *
 * @author MJ */
public class ButtonTable extends Table {
    private final ButtonGroup<Button> group;

    /** @param skin will be used to construct labels out of plain text. */
    public ButtonTable(final Skin skin) {
        this(skin, new ButtonGroup<Button>());
    }

    /** @param skin skin will be used to construct labels out of plain text.
     * @param group internally managed by the table. Will contain all {@link Button} children of the table. */
    public ButtonTable(final Skin skin, final ButtonGroup<Button> group) {
        super(skin);
        this.group = group;
    }

    /** @return internally managed {@link ButtonGroup}. */
    public ButtonGroup<Button> getButtonGroup() {
        return group;
    }

    @Override
    public <T extends Actor> Cell<T> add(final T actor) {
        if (actor instanceof Button) {
            final Button button = (Button) actor;
            button.setProgrammaticChangeEvents(false); // Making sure listeners are not invoked.
            group.add(button); // Might modify checked status.
            button.setProgrammaticChangeEvents(true); // Default value is true, no getter.
        }
        return super.add(actor);
    }
}
