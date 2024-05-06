package gdx.liftoff.ui;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import gdx.liftoff.ui.liftofftables.*;

import static com.badlogic.gdx.math.Interpolation.exp5;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static gdx.liftoff.Main.*;

public class RootTable extends Table {
    private Array<LiftoffTable> tables;
    private int tableIndex;
    public LandingTable landingTable;
    public AddOnsTable addOnsTable;
    public ThirdPartyTable thirdPartyTable;
    public SettingsTable settingsTable;
    public CompleteTable completeTable;
    public QuickSettingsTable quickSettingsTable;

    public RootTable() {
        tables = new Array<>();
        tableIndex = 0;
        pad(SPACE_LARGE);

        landingTable = new LandingTable();
        add(landingTable).prefSize(ROOT_TABLE_PREF_WIDTH, ROOT_TABLE_PREF_HEIGHT);
        landingTable.captureKeyboardFocus();
        landingTable.animate();

        addOnsTable = new AddOnsTable();
        thirdPartyTable = new ThirdPartyTable();
        settingsTable = new SettingsTable();
        completeTable = new CompleteTable();
        quickSettingsTable = new QuickSettingsTable();
        tables.addAll(landingTable, addOnsTable, thirdPartyTable, settingsTable, completeTable, quickSettingsTable);

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

    public LiftoffTable getCurrentTable() {
        return tables.get(tableIndex);
    }

    public void previousTable() {
        transitionTable(false);
    }

    public void nextTable() {
        transitionTable(true);
    }

    public void transitionTable(boolean goToNextTable) {
        int newIndex = goToNextTable ? tableIndex + 1 : tableIndex - 1;
        transitionTable(newIndex, tableIndex - newIndex != 1);
    }

    public void transitionTable(LiftoffTable table, boolean rightToLeftTransition) {
        transitionTable(tables.indexOf(table, true), rightToLeftTransition);
    }

    public void transitionTable(int tableIndex, boolean rightToLeftTransition) {
        LiftoffTable table = tables.get(this.tableIndex);
        table.finishAnimation();
        table.setTouchable(Touchable.disabled);
        stage.setKeyboardFocus(null);

        tableIndex = MathUtils.clamp(tableIndex, 0, tables.size);
        this.tableIndex = tableIndex;
        LiftoffTable newTable = tables.get(tableIndex);
        newTable.populate();

        //animation initial setup
        clearChildren();
        stage.addActor(table);
        add(newTable).prefSize(600, 700);
        newTable.setTouchable(Touchable.disabled);

        float distance = rightToLeftTransition ? stage.getWidth() : -stage.getWidth();

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

    public void fadeOutTable() {
        LiftoffTable table = tables.get(tableIndex);
        table.finishAnimation();
        table.addAction(sequence(
            fadeOut(.5f),
            visible(false)
        ));
    }

    public void fadeInTable() {
        Table table = tables.get(tableIndex);
        table.addAction(sequence(
            delay(.1f),
            visible(true),
            fadeIn(.5f)
        ));
    }
}
