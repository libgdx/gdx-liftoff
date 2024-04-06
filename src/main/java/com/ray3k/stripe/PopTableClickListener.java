package com.ray3k.stripe;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.utils.Align;
import com.ray3k.stripe.PopTable.PopTableStyle;
import com.ray3k.stripe.PopTable.TableShowHideListener;

public class PopTableClickListener extends ClickListener {
    protected PopTable popTable;
    private final int edge;
    private final int align;
    public Actor attachedActor;

    public PopTableClickListener() {
        this (Align.bottom, Align.bottom, new PopTableStyle());
    }

    public PopTableClickListener(Skin skin) {
        this(Align.bottom, Align.bottom, findStyleInSkin(skin));
    }

    public PopTableClickListener(Skin skin, String style) {
        this(Align.bottom, Align.bottom, findStyleInSkin(skin, style));
    }

    public PopTableClickListener(int edge, int align, Skin skin) {
        this(edge, align, findStyleInSkin(skin));
    }

    public PopTableClickListener(int edge, int align, Skin skin, String style) {
        this(edge, align, findStyleInSkin(skin, style));
    }

    public PopTableClickListener(int edge, int align, PopTableStyle style) {
        popTable = new PopTable(style);
        popTable.setHideOnUnfocus(true);
        this.edge = edge;
        this.align = align;
        popTable.addListener(new TableShowHideListener() {
            @Override
            public void tableShown(Event event) {
                PopTableClickListener.this.tableShown(event);
            }

            @Override
            public void tableHidden(Event event) {
                PopTableClickListener.this.tableHidden(event);
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
    public void clicked(InputEvent event, float x, float y) {
        super.clicked(event, x, y);
        Stage stage = event.getListenerActor().getStage();
        Actor actor = event.getListenerActor();

        if (actor instanceof Disableable) {
            if (((Disableable) actor).isDisabled()) return;
        }

        popTable.show(stage);

        popTable.attachToActor(attachedActor != null ? attachedActor : actor, edge, align);

        popTable.moveToInsideStage();
    }

    public PopTable getPopTable() {
        return popTable;
    }

    /**
     * Override this method to be performed when the popTable is displayed.
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
