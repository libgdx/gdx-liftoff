package com.github.czyzby.autumn.context;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.kiwi.util.gdx.asset.StatefulDisposable;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;

/** Disposable object that should be invoked when the context is meant to be destroyed - usually this means when
 * application is closing. Its {@link #dispose()} methods invokes action registered with {@link #dispose()}, usually by
 * annotation processors. These actions might include invoking finalizing component methods or disposing of heavy
 * objects.
 *
 * @author MJ */
public class ContextDestroyer implements StatefulDisposable {
    private Array<Runnable> destructionActions = GdxArrays.newArray();
    private boolean destroyed;

    @Override
    public boolean isDisposed() {
        return destroyed;
    }

    /** @param destructionAction will be invoked when {@link #dispose()} is called. Will be ignored, if context is
     *            already destroyed. */
    public void addAction(final Runnable destructionAction) {
        if (!destroyed) {
            destructionActions.add(destructionAction);
        }
    }

    @Override
    public void dispose() {
        if (GdxArrays.isNotEmpty(destructionActions)) {
            executeDestructionActions();
            destructionActions = null;
            destroyed = true;
        }
    }

    private void executeDestructionActions() {
        for (final Runnable action : destructionActions) {
            try {
                action.run();
            } catch (final Exception exception) {
                Gdx.app.error("ERROR", "Unable to process destruction action.", exception);
            }
        }
    }
}
