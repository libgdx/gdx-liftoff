package com.ray3k.stripe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class PopTable extends Table {
    private Stage stage;
    private final Image stageBackground;
    private WidgetGroup group;
    private final static Vector2 temp = new Vector2();
    private boolean hideOnUnfocus;
    private int attachEdge;
    private int attachAlign;
    private boolean keepSizedWithinStage;
    private boolean automaticallyResized;
    private boolean keepCenteredInWindow;
    private boolean fillParent;
    private Actor attachToActor;
    private float attachOffsetX;
    private float attachOffsetY;
    private boolean modal;
    private boolean hidden;
    private PopTableStyle style;
    private Array<InputListener> keyInputListeners;
    Actor previousKeyboardFocus, previousScrollFocus;
    private FocusListener focusListener;
    private DragListener dragListener;
    private Actor highlightActor;
    private float highlightAlpha = 1f;
    private boolean draggable;
    private boolean suppressKeyInputListeners;
    private boolean attachToMouse;
    private int attachToMouseAlignment;

    public PopTable() {
        this(new PopTableStyle());
    }

    public PopTable(Skin skin) {
        this(skin.get(PopTableStyle.class));
        setSkin(skin);
    }

    public PopTable(Skin skin, String style) {
        this(skin.get(style, PopTableStyle.class));
        setSkin(skin);
    }

    public PopTable(WindowStyle style) {
        this(new PopTableStyle(style));
    }

    public PopTable(PopTableStyle style) {
        setTouchable(Touchable.enabled);

        stageBackground = new Image(style.stageBackground);
        stageBackground.setFillParent(true);

        setBackground(style.background);

        attachEdge = Align.bottom;
        attachAlign = Align.bottom;
        keepSizedWithinStage = true;
        automaticallyResized = true;
        keepCenteredInWindow = false;
        setModal(false);
        setHideOnUnfocus(false);
        hidden = true;
        this.style = style;

        keyInputListeners = new Array<InputListener>();

        focusListener = new FocusListener() {
            public void keyboardFocusChanged (FocusEvent event, Actor actor, boolean focused) {
                if (!focused) focusChanged(event);
            }

            public void scrollFocusChanged (FocusEvent event, Actor actor, boolean focused) {
                if (!focused) focusChanged(event);
            }

            private void focusChanged (FocusEvent event) {
                if (modal && stage != null && stage.getRoot().getChildren().size > 0
                    && stage.getRoot().getChildren().peek() == group) { // PopTable is top most actor.

                    Actor newFocusedActor = event.getRelatedActor();
                    if (newFocusedActor != null && !newFocusedActor.isDescendantOf(PopTable.this)
                        && !(newFocusedActor.equals(previousKeyboardFocus) || newFocusedActor.equals(previousScrollFocus))) {
                        event.cancel();
                    }
                }
            }
        };

        dragListener = new DragListener() {
            float startX, startY;
            float offsetX, offsetY;
            boolean canDrag;
            {
                setTapSquareSize(0);
            }

            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                if (draggable) {
                    startX = PopTable.this.getX();
                    startY = PopTable.this.getY();
                    offsetX = x;
                    offsetY = y;
                    canDrag = event.getTarget() == PopTable.this;
                }
            }

            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                if (canDrag) {
                    setPosition(startX + x - offsetX, startY + y - offsetY);

                    if (keepSizedWithinStage && getX() < 0) {
                        temp.set(Gdx.input.getX(), Gdx.input.getY());
                        getStage().getViewport().unproject(temp);

                        offsetX = temp.x;
                        if (offsetX < 0) offsetX = 0;
                        setX(0);
                    }

                    if (keepSizedWithinStage && getX() + getWidth() > getStage().getWidth()) {
                        temp.set(Gdx.input.getX(), Gdx.input.getY());
                        getStage().getViewport().unproject(temp);

                        offsetX = getWidth() - (stage.getWidth() - temp.x);
                        if (offsetX > getWidth()) offsetX = getWidth();
                        setX(getStage().getWidth() - getWidth());
                    }

                    if (keepSizedWithinStage && getY() < 0) {
                        temp.set(Gdx.input.getX(), Gdx.input.getY());
                        getStage().getViewport().unproject(temp);

                        offsetY = temp.y;
                        if (offsetY < 0) offsetY = 0;
                        setY(0);
                    }

                    if (keepSizedWithinStage && getY() + getHeight() > getStage().getHeight()) {
                        temp.set(Gdx.input.getX(), Gdx.input.getY());
                        getStage().getViewport().unproject(temp);

                        offsetY = getHeight() - (stage.getHeight() - temp.y);
                        if (offsetY > getHeight()) offsetY = getHeight();
                        setY(getStage().getHeight() - getHeight());
                    }

                    startX = PopTable.this.getX();
                    startY = PopTable.this.getY();
                }
            }
        };
        addListener(dragListener);
    }

    public void setStyle(PopTableStyle style) {
        if (style == null) throw new NullPointerException("style cannot be null");
        this.style = style;
        setBackground(style.background);
        stageBackground.setDrawable(style.stageBackground);
    }

    public void setStyle(WindowStyle style) {
        setStyle(new PopTableStyle(style));
    }

    public void setStageBackground(Drawable drawable) {
        stageBackground.setDrawable(drawable);
    }

    public Drawable getStageBackground() {
        return stageBackground.getDrawable();
    }

    private void alignToActorEdge(Actor actor,  int edge, int alignment) {
        alignToActorEdge(actor, edge, alignment, 0, 0);
    }

    private void alignToActorEdge(Actor actor, int edge, int alignment, float offsetX, float offsetY) {
        float widgetX;
        switch (edge) {
            case Align.left:
            case Align.bottomLeft:
            case Align.topLeft:
                widgetX = 0;
                break;
            case Align.right:
            case Align.bottomRight:
            case Align.topRight:
                widgetX = actor.getWidth();
                break;
            default:
                widgetX = actor.getWidth() / 2f;
                break;
        }

        float widgetY;
        switch (edge) {
            case Align.bottom:
            case Align.bottomLeft:
            case Align.bottomRight:
                widgetY = 0;
                break;
            case Align.top:
            case Align.topLeft:
            case Align.topRight:
                widgetY = actor.getHeight();
                break;
            default:
                widgetY = actor.getHeight() / 2f;
                break;
        }

        switch (alignment) {
            case Align.bottom:
            case Align.top:
            case Align.center:
                widgetX -= getWidth() / 2;
                break;

            case Align.left:
            case Align.bottomLeft:
            case Align.topLeft:
                widgetX -= getWidth();
                break;
        }

        switch (alignment) {
            case Align.right:
            case Align.left:
            case Align.center:
                widgetY -= getHeight() / 2;
                break;

            case Align.bottom:
            case Align.bottomLeft:
            case Align.bottomRight:
                widgetY -= getHeight();
                break;
        }

        widgetX += offsetX;
        widgetY += offsetY;

        temp.set(widgetX, widgetY);
        actor.localToStageCoordinates(temp);
        setPosition(MathUtils.round(temp.x), MathUtils.round(temp.y));
    }

    public void moveToInsideStage() {
        if (getStage() != null) {
            if (getX() < 0) setX(0);
            else if (getX() + getWidth() > getStage().getWidth()) setX(getStage().getWidth() - getWidth());

            if (getY() < 0) setY(0);
            else if (getY() + getHeight() > getStage().getHeight()) setY(getStage().getHeight() - getHeight());
        }
    }

    private void resizeWindowWithinStage() {
        if (getWidth() > stage.getWidth()) {
            setWidth(stage.getWidth());
            invalidateHierarchy();
        }

        if (getHeight() > stage.getHeight()) {
            setHeight(stage.getHeight());
            invalidateHierarchy();
        }

        moveToInsideStage();
    }

    public boolean isOutsideStage() {
        return getX() < 0 || getX() + getWidth() > getStage().getWidth() || getY() < 0 || getY() + getHeight() > getStage().getHeight();
    }

    public void hide() {
        hide(fadeOut(.2f));
    }

    public void hide(Action action) {
        if (!hidden) {
            group.setTouchable(Touchable.disabled);
            hidden = true;
            if (action != null) group.addAction(sequence(action, Actions.removeActor()));
            else group.addAction(Actions.removeActor());
            fire(new TableHiddenEvent());
            for (InputListener inputListener : keyInputListeners) {
                stage.removeListener(inputListener);
            }

            stage.removeListener(focusListener);
            if (previousKeyboardFocus != null && previousKeyboardFocus.getStage() == null) previousKeyboardFocus = null;
            Actor actor = stage.getKeyboardFocus();
            if (actor == null || actor.isDescendantOf(this)) stage.setKeyboardFocus(previousKeyboardFocus);

            if (previousScrollFocus != null && previousScrollFocus.getStage() == null) previousScrollFocus = null;
            actor = stage.getScrollFocus();
            if (actor == null || actor.isDescendantOf(this)) stage.setScrollFocus(previousScrollFocus);
        }
    }

    public void show(Stage stage) {

        Action action = sequence(alpha(0), fadeIn(.2f));
        this.show(stage, action);
    }

    public void show(Stage stage, Action action) {
        hidden = false;
        this.stage = stage;
        group = new WidgetGroup();
        group.setColor(1, 1, 1, 0);
        group.setFillParent(true);
        group.setTouchable(hideOnUnfocus ? Touchable.enabled : Touchable.childrenOnly);
        group.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (hideOnUnfocus && event.getTarget() == group) {
                    hide();
                }
                return false;
            }
        });
        stage.addActor(group);

        group.addActor(stageBackground);
        group.addActor(this);

        pack();

        if (keepSizedWithinStage) {
            resizeWindowWithinStage();
        }

        setPosition((int) (stage.getWidth() / 2f - getWidth() / 2f), (int) (stage.getHeight() / 2f - getHeight() / 2f));

        if (action != null) group.addAction(action);
        fire(new TableShownEvent());

        previousKeyboardFocus = null;
        Actor actor = stage.getKeyboardFocus();
        if (actor != null && !actor.isDescendantOf(this)) previousKeyboardFocus = actor;

        previousScrollFocus = null;
        actor = stage.getScrollFocus();
        if (actor != null && !actor.isDescendantOf(this)) previousScrollFocus = actor;
        stage.addListener(focusListener);

        if (!suppressKeyInputListeners) for (InputListener inputListener : keyInputListeners) {
            stage.addListener(inputListener);
        }
    }

    public static class PopTableStyle {
        /*Optional*/
        public Drawable background, stageBackground;

        public PopTableStyle() {

        }

        public PopTableStyle(PopTableStyle style) {
            background = style.background;
            stageBackground = style.stageBackground;
        }

        public PopTableStyle(WindowStyle style) {
            background = style.background;
            stageBackground = style.stageBackground;
        }
    }

    public static class TableShownEvent extends Event {

    }

    public static class TableHiddenEvent extends Event {

    }

    public static abstract class TableShowHideListener implements EventListener {
        @Override
        public boolean handle(Event event) {
            if (event instanceof TableHiddenEvent) {
                tableHidden(event);
                return true;
            } else if (event instanceof TableShownEvent) {
                tableShown(event);
                return true;
            } else {
                return false;
            }
        }

        public abstract void tableShown(Event event);
        public abstract void tableHidden(Event event);
    }

    public boolean isHideOnUnfocus() {
        return hideOnUnfocus;
    }

    public void setHideOnUnfocus(boolean hideOnUnfocus) {
        this.hideOnUnfocus = hideOnUnfocus;
        if (group != null) group.setTouchable(hideOnUnfocus ? Touchable.enabled : Touchable.disabled);
    }

    public int getAttachEdge() {
        return attachEdge;
    }

    public int getAttachAlign() {
        return attachAlign;
    }

    public float getAttachOffsetX() {
        return attachOffsetX;
    }

    public void setAttachOffsetX(float attachOffsetX) {
        this.attachOffsetX = attachOffsetX;
    }

    public float getAttachOffsetY() {
        return attachOffsetY;
    }

    public void setAttachOffsetY(float attachOffsetY) {
        this.attachOffsetY = attachOffsetY;
    }

    public boolean isKeepSizedWithinStage() {
        return keepSizedWithinStage;
    }

    public void setKeepSizedWithinStage(boolean keepSizedWithinStage) {
        this.keepSizedWithinStage = keepSizedWithinStage;
    }

    public boolean isAutomaticallyResized() {
        return automaticallyResized;
    }

    public void setAutomaticallyResized(boolean automaticallyResized) {
        this.automaticallyResized = automaticallyResized;
    }

    public boolean isKeepCenteredInWindow() {
        return keepCenteredInWindow;
    }

    public void setKeepCenteredInWindow(boolean keepCenteredInWindow) {
        this.keepCenteredInWindow = keepCenteredInWindow;
    }

    public Actor getAttachToActor() {
        return attachToActor;
    }

    public void attachToActor() {
        attachToActor(attachToActor, attachEdge, attachAlign, attachOffsetX, attachOffsetY);
    }

    public void attachToActor(Actor attachToActor) {
        attachToActor(attachToActor, attachEdge, attachAlign, attachOffsetX, attachOffsetY);
    }

    public void attachToActor(Actor attachToActor, int edge, int align) {
        attachToActor(attachToActor, edge, align, attachOffsetX, attachOffsetY);
    }

    public void attachToActor(Actor attachToActor, int edge, int align, float offsetX, float offsetY) {
        alignToActorEdge(attachToActor, edge, align, offsetX, offsetY);
        this.attachToActor = attachToActor;
        this.attachEdge = edge;
        this.attachAlign = align;
        attachOffsetX = offsetX;
        attachOffsetY = offsetY;
    }

    public void removeAttachToActor() {
        attachToActor = null;
    }

    public boolean isModal() {
        return modal;
    }

    public void setModal(boolean modal) {
        this.modal = modal;
        stageBackground.setTouchable(modal ? Touchable.enabled : Touchable.disabled);
    }

    public boolean isHidden() {
        return hidden;
    }

    /**
     * Returns the actor to be highlighted when this PopTable is drawn. To see the effect, ensure that the style's
     * stageBackground is set appropriately.
     * @see PopTable#setHighlightAlpha(float)
     * @return
     */
    public Actor getHighlightActor() {
        return highlightActor;
    }

    /**
     * Sets the actor to be highlighted when this PopTable is drawn. To see the effect, ensure that the style's
     * stageBackground is set appropriately.
     * @param highlightActor
     * @see PopTable#setHighlightAlpha(float)
     */
    public void setHighlightActor(Actor highlightActor) {
        this.highlightActor = highlightActor;
    }

    /**
     * Returns the alpha of the highlighted actor.
     * @see PopTable#setHighlightActor(Actor)
     * @return
     */
    public float getHighlightAlpha() {
        return highlightAlpha;
    }

    /**
     * Sets the alpha of the highlighted actor.
     * @see PopTable#setHighlightActor(Actor)
     * @param highlightAlpha
     */
    public void setHighlightAlpha(float highlightAlpha) {
        this.highlightAlpha = highlightAlpha;
    }

    public boolean isDraggable() {
        return draggable;
    }

    /**
     * Allows the PopTable to be dragged by clicking/dragging directly on the widget.
     * @param draggable
     */
    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    public boolean isAttachToMouse() {
        return attachToMouse;
    }

    public void setAttachToMouse(boolean attachToMouse) {
        setAttachToMouse(attachToMouse, Align.bottomLeft);
    }

    public void setAttachToMouse(boolean attachToMouse, int alignment) {
        this.attachToMouse = attachToMouse;
        attachToMouseAlignment = alignment;
    }

    public PopTableStyle getStyle() {
        return style;
    }

    @Override
    public void validate() {
        if (fillParent) {
            setPosition(0, 0);
        } else {
            if (automaticallyResized) {
                float centerX = getX(Align.center);
                float centerY = getY(Align.center);
                if (isKeepSizedWithinStage()) setSize(Math.min(getPrefWidth(), stage.getWidth()), Math.min(getPrefHeight(), stage.getHeight()));
                else setSize(getPrefWidth(), getPrefHeight());
                super.validate();
                if (isKeepSizedWithinStage()) setSize(Math.min(getPrefWidth(), stage.getWidth()), Math.min(getPrefHeight(), stage.getHeight()));
                else setSize(getPrefWidth(), getPrefHeight());
                setPosition(centerX, centerY, Align.center);
                setPosition(MathUtils.floor(getX()), MathUtils.floor(getY()));
            }

            if (keepCenteredInWindow) {
                float x = getStage().getWidth() / 2f;
                float y = getStage().getHeight() / 2f;
                setPosition(x, y, Align.center);
                setPosition(MathUtils.floor(getX()), MathUtils.floor(getY()));
            }

            if (attachToActor != null && attachToActor.getStage() != null) {
                alignToActorEdge(attachToActor, attachEdge, attachAlign, attachOffsetX, attachOffsetY);
            }

            if (keepSizedWithinStage) {
                resizeWindowWithinStage();
            }
        }

        super.validate();
    }

    @Override
    public void setFillParent(boolean fillParent) {
        super.setFillParent(fillParent);
        this.fillParent = fillParent;
    }

    private static final Vector2 mousePosition = new Vector2();
    @Override
    public void act(float delta) {
        super.act(delta);
        if (attachToMouse) {
            mousePosition.set(Gdx.input.getX(), Gdx.input.getY());
            group.screenToLocalCoordinates(mousePosition);
            setPosition(mousePosition.x, mousePosition.y, attachToMouseAlignment);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (highlightActor != null) {
            highlightActor.draw(batch, parentAlpha * highlightAlpha);
        }
        super.draw(batch, parentAlpha);
    }

    /**
     * Returns the parent Group that this PopTable belongs to. This Group contains the stage background and is necessary
     * to capture unfocus clicks and to enable modal dialogs.
     * @return
     */
    public Group getParentGroup() {
        return group;
    }

    public void suppressKeyInputListeners(boolean suppress) {
        suppressKeyInputListeners = suppress;
        if (getStage() != null) for (InputListener keyListener : keyInputListeners) {
            if (suppress) getStage().removeListener(keyListener);
            else getStage().addListener(keyListener);
        }
    }

    public PopTable key(final int key, final KeyListener keyListener) {
        InputListener keyInputListener = new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == key) {
                    keyListener.keyed();
                    return true;
                } else return super.keyDown(event, keycode);
            }
        };
        keyInputListeners.add(keyInputListener);
        if (getStage() != null && !suppressKeyInputListeners) getStage().addListener(keyInputListener);
        return this;
    }

    public interface KeyListener {
        void keyed();
    }
}
