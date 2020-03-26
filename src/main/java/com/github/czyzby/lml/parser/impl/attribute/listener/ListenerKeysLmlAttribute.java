package com.github.czyzby.lml.parser.impl.attribute.listener;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.IntSet;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractListenerLmlTag;
import com.github.czyzby.lml.parser.impl.tag.listener.InputListenerLmlTag;
import com.github.czyzby.lml.scene2d.ui.reflected.ActorStorage;

/** See {@link InputListenerLmlTag#addKey(int)}. Expends an array of key names matching values returned by
 * {@link Keys#toString(int)} or actual, exact int values of key codes. Mapped to "keys".
 *
 * @author MJ */
public class ListenerKeysLmlAttribute extends AbstractListenerLmlAttribute {
    @Override
    protected void process(final LmlParser parser, final AbstractListenerLmlTag tag, final ActorStorage actor,
            final String rawAttributeData) {
        if (!(tag instanceof InputListenerLmlTag)) {
            parser.throwErrorIfStrict("'keys' attribute can be used only for input listeners.");
            return;
        }
        final IntSet keys = ((InputListenerLmlTag) tag).getKeys();
        processKeysAttribute(parser, actor, rawAttributeData, keys);
    }

    /** Utility.
     *
     * @param parser parses template.
     * @param actor contains the listener.
     * @param rawAttributeData unparsed attribute data.
     * @param keys handled keys set. */
    public static void processKeysAttribute(final LmlParser parser, final Actor actor, final String rawAttributeData,
            final IntSet keys) {
        final String[] keyNames = parser.parseArray(rawAttributeData, actor);
        for (final String keyName : keyNames) {
            final int key = Keys.valueOf(keyName);
            if (key <= Keys.UNKNOWN) {
                if (Strings.isInt(keyName)) {
                    keys.add(Integer.parseInt(keyName));
                } else {
                    parser.throwErrorIfStrict("Unable to determine key for name: " + keyName
                            + ". Note that key name should match the EXACT name from Keys class (see Keys#valueOf(String)) or be the desired int value of key code.");
                }
            } else {
                keys.add(key);
            }
        }
    }
}
