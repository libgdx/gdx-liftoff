package com.github.czyzby.lml.parser.impl.action;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.github.czyzby.lml.parser.action.StageAttacher;

/** Default implementation of {@link StageAttacher}. Allows to specify initial position according to stage's size.
 *
 * @author MJ */
public class DefaultStageAttacher implements StageAttacher {
    /** Centers the widget on the stage. */
    public static final StageAttacher CENTERING_STAGE_ATTACHER = new StageAttacher() {
        // Since DefaultStageAttacher is mutable, this kind of call delegating makes it safe to use and impossible to
        // mutate without reflection.
        private final StageAttacher protectedAttacher = new DefaultStageAttacher();

        @Override
        public void attachToStage(final Actor actor, final Stage stage) {
            protectedAttacher.attachToStage(actor, stage);
        }
    };

    private float x, y;
    private PositionConverter xConverter, yConverter;

    /** Creates a new stage attacher that will center the widget on the stage. */
    public DefaultStageAttacher() {
        this(0f, 0f, StandardPositionConverter.CENTER, StandardPositionConverter.CENTER);
    }

    public DefaultStageAttacher(final float x, final float y, final PositionConverter xConverter,
            final PositionConverter yConverter) {
        this.x = x;
        this.y = y;
        this.xConverter = xConverter;
        this.yConverter = yConverter;
    }

    @Override
    public void attachToStage(final Actor actor, final Stage stage) {
        if (actor instanceof Dialog) {
            ((Dialog) actor).show(stage);
        }
        if (actor instanceof Layout) {
            ((Layout) actor).pack();
        }
        actor.setPosition(xConverter.convertX(x, stage, actor), yConverter.convertY(y, stage, actor));
    }

    /** @param x initial X value to parse. */
    public void setX(final float x) {
        this.x = x;
    }

    /** @param y initial Y value to parse. */
    public void setY(final float y) {
        this.y = y;
    }

    /** @param xConverter converts X position. */
    public void setXConverter(final PositionConverter xConverter) {
        this.xConverter = xConverter;
    }

    /** @param yConverter converts Y position. */
    public void setYConverter(final PositionConverter yConverter) {
        this.yConverter = yConverter;
    }

    /** Allows to converts float values to a position on stage.
     *
     * @author MJ */
    public static interface PositionConverter {
        /** @param x value to convert.
         * @param stage has the actor.
         * @param actor needs an initial position.
         * @return converted X position. */
        public abstract float convertX(float x, Stage stage, Actor actor);

        /** @param y value to convert.
         * @param stage has the actor.
         * @param actor needs an initial position.
         * @return converted Y position. */
        public abstract float convertY(float y, Stage stage, Actor actor);

    }

    /** Allows to converts float values to a position on stage.
     *
     * @author MJ */
    public static enum StandardPositionConverter implements PositionConverter {
        /** Ignores float value and centers the actor on the stage according to their sizes. */
        CENTER {
            @Override
            public float convertX(final float x, final Stage stage, final Actor actor) {
                return (int) (stage.getWidth() / 2f - actor.getWidth() / 2f);
            }

            @Override
            public float convertY(final float y, final Stage stage, final Actor actor) {
                return (int) (stage.getHeight() / 2f - actor.getHeight() / 2f);
            }
        },
        /** Returns the passed float values, effectively using absolute position values. */
        ABSOLUTE {
            @Override
            public float convertX(final float x, final Stage stage, final Actor actor) {
                return x;
            }

            @Override
            public float convertY(final float y, final Stage stage, final Actor actor) {
                return y;
            }
        },
        /** Treats the passed values as percents of stage size. */
        PERCENT {
            @Override
            public float convertX(final float x, final Stage stage, final Actor actor) {
                return (int) (stage.getWidth() * x);
            }

            @Override
            public float convertY(final float y, final Stage stage, final Actor actor) {
                return (int) (stage.getHeight() * y);
            }
        };
    }
}
