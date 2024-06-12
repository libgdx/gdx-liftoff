package gdx.liftoff.ui.liftofftables;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * LiftoffTables are used in the normal workflow of the app. These participate in the animations initiated in the
 * RootTable. The abstract methods allow the tables to respond to requests to capture the keyboard focus and to finish
 * any internal animations early.
 */
public abstract class LiftoffTable extends Table {
    public abstract void captureKeyboardFocus();

    public abstract void finishAnimation();

    public abstract void populate();
}
