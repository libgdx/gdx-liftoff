package gdx.liftoff.ui.tables;

import static gdx.liftoff.Main.skin;

public class AddOnsTable extends LiftoffTable {
    public AddOnsTable() {
        setBackground(skin.getDrawable("black"));
        pad(20).padLeft(30).padRight(30);
    }

    @Override
    public void captureKeyboardFocus() {

    }

    @Override
    public void finishAnimation() {

    }
}
