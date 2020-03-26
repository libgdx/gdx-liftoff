package com.github.czyzby.lml.parser.impl.tag.listener;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.IntSet;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractListenerLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Attachable tag. Can be a child of any tag and can have any actor tag children. Adds a {@link InputListener} to the
 * parent actor. Each time the selected keys are pressed (or any key is pressed, if they are not chosen), actors that
 * are children of listener tag will be added to the stage. Useful for "delayed" adding of actors - for example, one can
 * easily define a dialog that will be shown each time a certain key is typed. Note that is not simply an equivalent of
 * {@link com.github.czyzby.lml.parser.impl.tag.macro.InputListenerLmlMacroTag input listener macro}: macro parses
 * template between its tags after event occurs (so actor creation is delayed). When using this tag, actors are created
 * along the rest of the template - they are simply not added to the stage immediately, but otherwise they are parsed
 * like any other actors and can access local variables.
 *
 * <p>
 * Mapped to "onInput", "inputListener".
 *
 * @author MJ
 * @see #setCondition(String) */
public class InputListenerLmlTag extends AbstractListenerLmlTag {
    private KeysListener listener;
    private IntSet keys;

    public InputListenerLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        keys = new IntSet();
        listener = new KeysListener(keys) {
            @Override
            protected void handleEvent(final Actor actor) {
                doOnEvent(actor);
            }
        };
        return super.getNewInstanceOfActor(builder);
    }

    @Override
    protected InputListener getEventListener() {
        return listener;
    }

    /** @param keyCode key code from {@link Keys}. Will trigger the event. If no codes are added, event is processed
     *            after every key. */
    public void addKey(final int keyCode) {
        keys.add(keyCode);
    }

    /** @return direct reference to handled keys.
     * @see #addKey(int) */
    public IntSet getKeys() {
        return keys;
    }

    /** @param combined if true, all keys have to be pressed at the same to process the event.
     * @see #addKey(int) */
    public void setCombined(final boolean combined) {
        listener.setCombined(combined);
    }

    /** Listens to certain keys or key combinations.
     *
     * @author MJ */
    public static abstract class KeysListener extends InputListener {
        private final IntSet keys;
        private boolean combined;
        private final IntSet pressed = new IntSet();

        /** @param keys supported keys. */
        public KeysListener(final IntSet keys) {
            this.keys = keys;
        }

        public void setCombined(final boolean combined) {
            this.combined = combined;
        }

        @Override
        public boolean keyDown(final InputEvent event, final int keycode) {
            if (keys.size == 0) {
                handleEvent(event.getListenerActor());
                return true;
            }
            if (keys.contains(keycode)) {
                if (!combined) {
                    handleEvent(event.getListenerActor());
                    return true;
                }
                pressed.add(keycode);
                if (keys.size == pressed.size) {
                    handleEvent(event.getListenerActor());
                    pressed.clear();
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean keyUp(final InputEvent event, final int keycode) {
            if (keys.contains(keycode)) {
                pressed.remove(keycode);
                return true;
            }
            return false;
        }

        /** @param actor has the listener attached. */
        protected abstract void handleEvent(Actor actor);
    }
}
