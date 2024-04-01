package com.ray3k.stripe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.ray3k.stripe.PopTable.PopTableStyle;
import com.ray3k.stripe.PopTable.TableShowHideListener;

public class PopTableHoverListener extends InputListener {
    protected PopTable popTable;
    public boolean hideOnExit;
    public float delay;
    public Actor attachedActor;
    private final int align;
    private final int edge;
    private Action showTableAction;
    private boolean dismissed;

    public PopTableHoverListener(int edge, int align, Skin skin) {
        this(edge, align, findStyleInSkin(skin));
    }

    public PopTableHoverListener(int edge, int align, Skin skin, String style) {
        this(edge, align, findStyleInSkin(skin, style));
    }

    public PopTableHoverListener(int edge, int align, PopTableStyle style) {
        hideOnExit = true;
        delay = .5f;
        popTable = new PopTable(style);
        popTable.setModal(false);
        popTable.setHideOnUnfocus(true);
        popTable.setTouchable(Touchable.disabled);
        this.edge = edge;
        this.align = align;
        popTable.addListener(new TableShowHideListener() {
            @Override
            public void tableShown(Event event) {
                PopTableHoverListener.this.tableShown(event);
            }

            @Override
            public void tableHidden(Event event) {
                PopTableHoverListener.this.tableHidden(event);
            }
        });
    }

    private static PopTableStyle findStyleInSkin(Skin skin) {
        return findStyleInSkin(skin, "default");
    }

    private static PopTableStyle findStyleInSkin(Skin skin, String style) {
        if (skin.has(style, PopTableStyle.class)) return skin.get(style, PopTableStyle.class);
        else if (skin.has(style, WindowStyle.class)) return new PopTableStyle(skin.get(style, WindowStyle.class));
        else return new PopTableStyle();
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        Stage stage = event.getListenerActor().getStage();
        Actor actor = event.getListenerActor();

        if (Gdx.input.isButtonPressed(Buttons.LEFT) || Gdx.input.isButtonPressed(Buttons.RIGHT) || Gdx.input.isButtonPressed(Buttons.MIDDLE)) return;
        if (pointer != -1 || !popTable.isHidden() || dismissed) return;
        if (fromActor != null && actor.isAscendantOf(fromActor)) return;
        if (actor instanceof Disableable && ((Disableable) actor).isDisabled()) return;

        if (showTableAction == null) {
            showTableAction = Actions.delay(delay, Actions.run(() -> {
                popTable.show(stage);
                popTable.toFront();
                popTable.getParentGroup().setTouchable(Touchable.disabled);
                popTable.attachToActor(attachedActor != null ? attachedActor : actor, edge, align);
                showTableAction = null;
            }));
            actor.addAction(showTableAction);
        }

        popTable.moveToInsideStage();
    }

    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        if (pointer != -1 || !hideOnExit) return;
        if (toActor != null && event.getListenerActor().isAscendantOf(toActor)) return;
        Actor actor = event.getListenerActor();

        if (!popTable.isHidden()) popTable.hide();
        dismissed = false;
        if (showTableAction != null) {
            actor.removeAction(showTableAction);
            showTableAction = null;
        }
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        Actor actor = event.getListenerActor();
        dismissed = true;
        popTable.hide();
        if (showTableAction != null) {
            actor.removeAction(showTableAction);
            showTableAction = null;
        }
        return false;
    }

    public PopTable getPopTable() {
        return popTable;
    }

    /**
     * Override this method to be performed when the popTable is hidden or dismissed.
     * @param event The event associated with the table being shown.
     */
    public void tableShown(Event event) {

    }

    /**
     * Override this method to be performed when the popTable is hidden or dismissed.
     * @param event The event associated with the table being hidden.
     */
    public void tableHidden(Event event) {

    }
}
