package gdx.liftoff.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import gdx.liftoff.ui.tables.LandingTable;

public class RootTable extends Table {
    public RootTable() {
        pad(20);

        LandingTable landingTable = new LandingTable();
        add(landingTable).prefSize(600, 700);
        landingTable.captureKeyboardFocus();
    }
}
