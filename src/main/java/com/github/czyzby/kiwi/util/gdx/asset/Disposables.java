package com.github.czyzby.kiwi.util.gdx.asset;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.kiwi.util.common.UtilitiesClass;

/** Provides some simple utility methods for disposable objects.
 *
 * @author MJ */
public class Disposables extends UtilitiesClass {
    private Disposables() {
    }

    /** Performs null check and disposes of an asset.
     *
     * @param disposable will be disposed of (if it exists). */
    public static void disposeOf(final Disposable disposable) {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    /** Performs null checks and disposes of assets.
     *
     * @param disposables will be disposed of (if they exist). */
    public static void disposeOf(final Disposable... disposables) {
        if (disposables != null) {
            for (final Disposable disposable : disposables) {
                disposeOf(disposable);
            }
        }
    }

    /** Performs null checks and disposes of assets.
     *
     * @param disposables will be disposed of (if they exist). */
    public static void disposeOf(final Iterable<? extends Disposable> disposables) {
        if (disposables != null) {
            for (final Disposable disposable : disposables) {
                disposeOf(disposable);
            }
        }
    }

    /** Performs null checks and disposes of assets.
     *
     * @param disposables its values will be disposed of (if they exist). Can be null. */
    public static void disposeOf(final ObjectMap<?, ? extends Disposable> disposables) {
        if (disposables != null) {
            for (final Disposable disposable : disposables.values()) {
                disposeOf(disposable);
            }
        }
    }

    /** Performs null checks and disposes of assets.
     *
     * @param disposables its values will be disposed of (if they exist). Can be null. */
    public static void disposeOf(final Map<?, ? extends Disposable> disposables) {
        if (disposables != null) {
            for (final Disposable disposable : disposables.values()) {
                disposeOf(disposable);
            }
        }
    }

    /** Performs null check and disposes of an asset. Ignores exceptions.
     *
     * @param disposable will be disposed of (if it exists). */
    public static void gracefullyDisposeOf(final Disposable disposable) {
        try {
            if (disposable != null) {
                disposable.dispose();
            }
        } catch (final Throwable exception) {
            Gdx.app.error("WARN", "Unable to dispose: " + disposable + ". Ignored.", exception);
        }
    }

    /** Performs null checks and disposes of assets. Ignores exceptions.
     *
     * @param disposables will be disposed of (if they exist). */
    public static void gracefullyDisposeOf(final Disposable... disposables) {
        if (disposables != null) {
            for (final Disposable disposable : disposables) {
                gracefullyDisposeOf(disposable);
            }
        }
    }

    /** Performs null checks and disposes of assets. Ignores exceptions.
     *
     * @param disposables will be disposed of (if they exist). */
    public static void gracefullyDisposeOf(final Iterable<? extends Disposable> disposables) {
        if (disposables != null) {
            for (final Disposable disposable : disposables) {
                gracefullyDisposeOf(disposable);
            }
        }
    }

    /** Performs null checks and disposes of assets. Ignores exceptions.
     *
     * @param disposables its values will be disposed of (if they exist). Can be null. */
    public static void gracefullyDisposeOf(final ObjectMap<?, ? extends Disposable> disposables) {
        if (disposables != null) {
            for (final Disposable disposable : disposables.values()) {
                gracefullyDisposeOf(disposable);
            }
        }
    }

    /** Performs null checks and disposes of assets. Ignores exceptions.
     *
     * @param disposables its values will be disposed of (if they exist). Can be null. */
    public static void gracefullyDisposeOf(final Map<?, ? extends Disposable> disposables) {
        if (disposables != null) {
            for (final Disposable disposable : disposables.values()) {
                gracefullyDisposeOf(disposable);
            }
        }
    }
}
