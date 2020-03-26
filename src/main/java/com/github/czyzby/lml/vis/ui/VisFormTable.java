package com.github.czyzby.lml.vis.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.github.czyzby.kiwi.util.gdx.collection.pooled.PooledList;
import com.kotcrab.vis.ui.util.form.SimpleFormValidator;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

/** Represents a {@link SimpleFormValidator}'s widgets container. Additionally to features of {@link VisTable},
 * maintains a reference to an internal form validator. Meant to store actors that represent a form. Automatically adds
 * {@link VisValidatableTextField} to form.
 *
 * @author MJ */
public class VisFormTable extends VisTable {
    private final SimpleFormValidator formValidator = createFormValidator();

    /** @return a new instance of {@link SimpleFormValidator}, managed by this table. */
    protected SimpleFormValidator createFormValidator() {
        return new SimpleFormValidator(null, null);
    }

    /** @return internal {@link SimpleFormValidator} instance. */
    public SimpleFormValidator getFormValidator() {
        return formValidator;
    }

    @Override
    public <T extends Actor> Cell<T> add(final T actor) {
        if (actor instanceof VisValidatableTextField) {
            formValidator.add((VisValidatableTextField) actor);
        } else if (actor instanceof Group) {
            findValidatables((Group) actor);
        }
        return super.add(actor);
    }

    /** @param actor will be searched recursively. All {@link VisValidatableTextField}s will be added to form. */
    public void findValidatables(final Group actor) {
        final PooledList<Group> groupsToCheck = PooledList.newList();
        groupsToCheck.add(actor);
        while (groupsToCheck.isNotEmpty()) {
            final Group group = groupsToCheck.removeFirst();
            for (final Actor child : group.getChildren()) {
                if (child instanceof VisValidatableTextField) {
                    formValidator.add((VisValidatableTextField) child);
                } else if (child instanceof Group) {
                    groupsToCheck.add((Group) child);
                }
            }
        }
    }

    /** See {@link SimpleFormValidator#setMessageLabel(Label)}.
     *
     * @param label will show form errors. */
    public void setMessageLabel(final Label label) {
        formValidator.setMessageLabel(label);
    }

    /** See {@link SimpleFormValidator#setSuccessMessage(String)}.
     *
     * @param message will be shown if there are no errors in the form. */
    public void setSuccessMessage(final String message) {
        formValidator.setSuccessMessage(message);
    }

    /** See {@link SimpleFormValidator#addDisableTarget(Disableable)}.
     *
     * @param disableable will be disabled if any errors are found in the form. */
    public void addWidgetToDisable(final Disableable disableable) {
        formValidator.addDisableTarget(disableable);
    }

    /** See {@link SimpleFormValidator#checked(Button, String)}.
     *
     * @param button must be checked.
     * @param errorMessage displayed if button is not checked. */
    public void addCheckedFormButton(final Button button, final String errorMessage) {
        formValidator.checked(button, errorMessage);
    }

    /** See {@link SimpleFormValidator#unchecked(Button, String)}.
     *
     * @param button must be unchecked.
     * @param errorMessage displayed if button is checked. */
    public void addUncheckedFormButton(final Button button, final String errorMessage) {
        formValidator.unchecked(button, errorMessage);
    }
}
