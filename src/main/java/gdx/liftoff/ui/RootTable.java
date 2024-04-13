package gdx.liftoff.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import gdx.liftoff.Main;
import gdx.liftoff.ui.tables.*;

import static com.badlogic.gdx.math.Interpolation.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static gdx.liftoff.Main.*;

public class RootTable extends Table {
    private Array<LiftoffTable> tables;
    private int tableIndex;
    private LandingTable landingTable;
    private AddOnsTable addOnsTable;
    private ThirdPartyTable thirdPartyTable;
    private SettingsTable settingsTable;
    private CompleteTable completeTable;

    public RootTable() {
        pad(20);
        tables = new Array<>();

        landingTable = new LandingTable();
        add(landingTable).prefSize(600, 700);
        landingTable.captureKeyboardFocus();
        landingTable.animate();

        addOnsTable = new AddOnsTable();
        thirdPartyTable = new ThirdPartyTable();
        settingsTable = new SettingsTable();
        completeTable = new CompleteTable();
        tables.addAll(landingTable, addOnsTable, thirdPartyTable, settingsTable, completeTable);

        setTouchable(Touchable.enabled);
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                tables.get(tableIndex).finishAnimation();
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (resizingWindow) tables.get(tableIndex).finishAnimation();
    }

    public void previousTable() {
        transitionTable(false);
    }

    public void nextTable() {
        transitionTable(true);
    }

    public void showHomeTable() {
        transitionTable(0);
    }

    private void transitionTable(boolean goNext) {
        transitionTable(goNext ? tableIndex + 1 : tableIndex - 1);
    }

    private void transitionTable(int tableIndex) {
        LiftoffTable table = tables.get(this.tableIndex);
        table.finishAnimation();
        table.setTouchable(Touchable.disabled);
        stage.setKeyboardFocus(null);

        tableIndex = MathUtils.clamp(tableIndex, 0, tables.size);
        boolean goNext = this.tableIndex - tableIndex != 1;
        this.tableIndex = tableIndex;
        LiftoffTable newTable = tables.get(tableIndex);

        //initial setup
        clearChildren();
        stage.addActor(table);
        add(newTable).prefSize(600, 700);
        newTable.setTouchable(Touchable.disabled);

        float distance = goNext ? stage.getWidth() : -stage.getWidth();

        addAction(sequence(
            //setup on first frame
            targeting(newTable, Actions.moveBy(distance, 0)),
            //move the tables horizontally
            parallel(
                targeting(table, Actions.moveBy(-distance, 0, 1f, exp5)),
                targeting(newTable, Actions.moveBy(-distance, 0, 1f, exp5))
            ),
            //remove the old table
            targeting(table, Actions.removeActor()),
            //enable input on the new table
            run(() -> {
                newTable.setTouchable(Touchable.childrenOnly);
                newTable.captureKeyboardFocus();
            })
        ));
    }
}
