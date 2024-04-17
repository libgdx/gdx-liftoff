package gdx.liftoff.ui.liftofftables;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public abstract class LiftoffTable extends Table {
    public abstract void captureKeyboardFocus();
    public abstract void finishAnimation();
}
