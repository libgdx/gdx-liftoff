package com.github.czyzby.kiwi.util.gdx.scene2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.github.czyzby.kiwi.util.common.UtilitiesClass;

/** Contains common methods for Scene2D actors.
 *
 * @author MJ */
public class Actors extends UtilitiesClass {
    /** Name of default style, commonly used by widgets constructed with {@link Skin}, but without a specified style
     * name. */
    public static final String DEFAULT_STYLE = "default";
    /** Name of default vertical style, commonly used by widgets that have separate horizontal and vertical styles,
     * constructed with {@link Skin}, but without a specified style name. */
    public static final String DEFAULT_VERTICAL_STYLE = "default-vertical";
    /** Name of default horizontal style, commonly used by widgets that have separate horizontal and vertical styles,
     * constructed with {@link Skin}, but without a specified style name. */
    public static final String DEFAULT_HORIZONTAL_STYLE = "default-horizontal";

    private Actors() {
    }

    /** @param actor if the actor implements {@link Layout}, {@link Layout#pack()} will be called. Null-safe. */
    public static void pack(final Object actor) {
        if (actor instanceof Layout) {
            ((Layout) actor).pack();
        }
    }

    /** Null-safe utility for adding multiple actors to the stage.
     *
     * @param stage will contain the actors.
     * @param actors will be added to the stage. Can be null. */
    public static void addActors(final Stage stage, final Iterable<Actor> actors) {
        if (actors != null) {
            for (final Actor actor : actors) {
                stage.addActor(actor);
            }
        }
    }

    /** Null-safe position update.
     *
     * @param actor will be centered on its assigned stage according to their sizes. Can be null. */
    public static void centerActor(final Actor actor) {
        centerActor(actor, actor.getStage());
    }

    /** Null-safe position update.
     *
     * @param actor will be centered on the given stage according to their sizes. Can be null.
     * @param stage can be null. */
    public static void centerActor(final Actor actor, final Stage stage) {
        if (actor != null && stage != null) {
            actor.setPosition((int) (stage.getWidth() / 2f - actor.getWidth() / 2f),
                    (int) (stage.getHeight() / 2f - actor.getHeight() / 2f));
        }
    }

    /** When called BEFORE resizing the stage, moves the actor to match the same aspect ratio as before. Useful for
     * windows and dialogs in screen viewports.
     *
     * @param actor will be repositioned. Can be null, method invocation will be ignored.
     * @param newScreenSizeInStageCoords screen coords processed by stage. */
    public static void updateActorPosition(final Actor actor, final Vector2 newScreenSizeInStageCoords) {
        if (actor != null) {
            updateActorPosition(actor, actor.getStage(), newScreenSizeInStageCoords);
        }
    }

    /** When called BEFORE resizing the stage, moves the actor to match the same aspect ratio as before. Useful for
     * windows and dialogs in screen viewports.
     *
     * @param actor will be repositioned.
     * @param stage has to be before resizing.
     * @param newScreenSizeInStageCoords screen coords processed by stage. */
    public static void updateActorPosition(final Actor actor, final Stage stage,
            final Vector2 newScreenSizeInStageCoords) {
        if (actor != null && stage != null) {
            actor.setPosition(
                    (int) ((actor.getX() + actor.getWidth() / 2f) / stage.getWidth() * newScreenSizeInStageCoords.x
                            - actor.getWidth() / 2f),
                    (int) ((actor.getY() + actor.getHeight() / 2f) / stage.getHeight() * newScreenSizeInStageCoords.y
                            - actor.getHeight() / 2f));
        }
    }

    /** Null-safe check if the actor has a stage. Useful especially for dialogs.
     *
     * @param actor can be null.
     * @return true if actor is not null and has a stage. */
    public static boolean isShown(final Actor actor) {
        return actor != null && actor.getStage() != null;
    }

    /** Null-safe method to hide dialogs.
     *
     * @param dialog will be hidden. Can be null. */
    public static void hide(final Dialog dialog) {
        if (dialog != null) {
            dialog.hide();
        }
    }

    /** Null-safe method for clearing keyboard focus.
     *
     * @param actor if is not null, will extract its stage. If stage is not null, will clear its keyboard focused actor,
     *            if one is present. */
    public static void clearKeyboardFocus(final Actor actor) {
        if (actor != null) {
            clearKeyboardFocus(actor.getStage());
        }
    }

    /** Null-safe method for clearing keyboard focus.
     *
     * @param stage will have its keyboard focused actor cleared, if one is present. Can be null. */
    public static void clearKeyboardFocus(final Stage stage) {
        if (stage != null) {
            stage.setKeyboardFocus(null);
        }
    }

    /** Null-safe method for setting keyboard focus.
     *
     * @param actor if is not null and has a stage, will be set as stage's keyboard focused actor. */
    public static void setKeyboardFocus(final Actor actor) {
        if (actor != null && actor.getStage() != null) {
            actor.getStage().setKeyboardFocus(actor);
        }
    }

    /** Null-safe method that clears a Cell of a Table.
     *
     * @param tableCell can be null. Will have its actor removed and will be reset. */
    public static void clearCell(final Cell<?> tableCell) {
        if (tableCell != null) {
            tableCell.clearActor();
            tableCell.reset();
        }
    }

    /** Null-safe alpha setting method.
     *
     * @param stage its root actor's alpha will be modified, affecting all actors in the stage. Can be null.
     * @param alpha will replace current actor's alpha. */
    public static void setAlpha(final Stage stage, final float alpha) {
        if (stage != null) {
            setAlpha(stage.getRoot(), alpha);
        }
    }

    /** Null-safe alpha setting method.
     *
     * @param stage its root actor's alpha will become {@code 1f}, affecting all actors in the stage. Can be null. */
    public static void setMaxAlpha(final Stage stage) {
        if (stage != null) {
            setAlpha(stage.getRoot(), 1f);
        }
    }

    /** Null-safe alpha setting method.
     *
     * @param stage its root actor's alpha will become {@code 0f}, affecting all actors in the stage. Can be null. */
    public static void setMinAlpha(final Stage stage) {
        if (stage != null) {
            setAlpha(stage.getRoot(), 0f);
        }
    }

    /** Null-safe alpha setting method.
     *
     * @param actor its {@link Color}'s alpha will become {@code 1f}. Can be null. */
    public static void setMaxAlpha(final Actor actor) {
        setAlpha(actor, 1f);
    }

    /** Null-safe alpha setting method.
     *
     * @param actor its {@link Color}'s alpha will become {@code 0f}. Can be null. */
    public static void setMinAlpha(final Actor actor) {
        setAlpha(actor, 0f);
    }

    /** Null-safe alpha setting method.
     *
     * @param actor its {@link Color}'s alpha will be modified. Can be null.
     * @param alpha will become actor's alpha. */
    public static void setAlpha(final Actor actor, final float alpha) {
        if (actor != null) {
            actor.getColor().a = alpha;
        }
    }
}
