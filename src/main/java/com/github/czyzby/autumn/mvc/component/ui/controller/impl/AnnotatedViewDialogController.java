package com.github.czyzby.autumn.mvc.component.ui.controller.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewDialogController;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewDialogShower;
import com.github.czyzby.autumn.mvc.stereotype.ViewDialog;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.github.czyzby.lml.util.LmlUserObject;
import com.github.czyzby.lml.util.LmlUtilities;

/** Wraps around an object annotated with {@link ViewDialog}.
 *
 * @author MJ */
public class AnnotatedViewDialogController extends AbstractAnnotatedController implements ViewDialogController {
    private final ViewDialog dialogData;
    private final InterfaceService interfaceService;
    private final ViewDialogShower shower;
    private final ActionContainer actionContainer;
    private final String id;

    private Window dialog;

    public AnnotatedViewDialogController(final ViewDialog dialogData, final Object wrappedObject,
            final InterfaceService interfaceService) {
        super(wrappedObject);
        this.dialogData = dialogData;
        this.interfaceService = interfaceService;

        shower = wrappedObject instanceof ViewDialogShower ? (ViewDialogShower) wrappedObject : null;
        actionContainer = wrappedObject instanceof ActionContainer ? (ActionContainer) wrappedObject : null;
        id = Strings.isWhitespace(dialogData.id()) ? wrappedObject.getClass().getSimpleName() : dialogData.id();
    }

    @Override
    public void show(final Stage stage) {
        injectStage(stage);
        if (dialog == null || !isCachingInstance()) {
            prepareDialogInstance();
        }
        doBeforeShow();
        showDialog(stage);
    }

    private void showDialog(final Stage stage) {
        final LmlUserObject userObject = LmlUtilities.getOptionalLmlUserObject(dialog);
        if (userObject != null && userObject.getStageAttacher() != null) {
            userObject.getStageAttacher().attachToStage(dialog, stage);
        } else if (dialog instanceof Dialog) {
            ((Dialog) dialog).show(stage);
        } else {
            // Simplified copy of Dialog#show:
            stage.addActor(dialog);
            dialog.setPosition(Math.round((stage.getWidth() - dialog.getWidth()) / 2f),
                    Math.round((stage.getHeight() - dialog.getHeight()) / 2f));
            dialog.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.4f, Interpolation.fade)));
        }
    }

    private void doBeforeShow() {
        if (shower != null) {
            shower.doBeforeShow(dialog);
        }
    }

    /** @return true if an instance of the dialog is currently initiated and available. */
    public boolean isInitiated() {
        return dialog != null;
    }

    /** @return true if dialog actor instance is cached and reused on each showing. */
    public boolean isCachingInstance() {
        return dialogData.cacheInstance();
    }

    /** Creates instance of the managed dialog actor. */
    public void prepareDialogInstance() {
        final LmlParser parser = interfaceService.getParser();
        if (actionContainer != null) {
            parser.getData().addActionContainer(getId(), actionContainer);
        }
        dialog = (Window) parser.createView(wrappedObject, Gdx.files.internal(dialogData.value())).first();
        if (actionContainer != null) {
            parser.getData().removeActionContainer(getId());
        }
    }

    @Override
    public void destroyDialog() {
        if (dialog != null) {
            if (dialog.getStage() != null) {
                if (dialog instanceof Dialog) {
                    ((Dialog) dialog).hide();
                } else {
                    // Simplified version of Dialog#hide:
                    dialog.addAction(
                            Actions.sequence(Actions.fadeOut(0.4f, Interpolation.fade), Actions.removeActor()));
                }
            }
            dialog = null;
        }
    }

    @Override
    public String getId() {
        return id;
    }
}
