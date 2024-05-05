package gdx.liftoff.ui.panels;

/**
 * Tables can implement this interface to cue the root table to trigger a keyboard focus change when there is a
 * transition
 */
public interface Panel {
    void captureKeyboardFocus();
    void populate(boolean fullscreen);
}
