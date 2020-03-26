package com.github.czyzby.lml.parser.impl.attribute;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Allows to disable {@code Disableable} widgets using {@code Disableable#setDisabled(boolean)} method. Expects a
 * boolean. By default, mapped to "disabled", "disable" attribute names.
 *
 * @author MJ */
public class DisabledLmlAttribute implements LmlAttribute<Actor> {
    @Override
    public Class<Actor> getHandledType() {
        return Actor.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final String rawAttributeData) {
        if (actor instanceof Disableable) {
            ((Disableable) actor).setDisabled(parser.parseBoolean(rawAttributeData, actor));
        } else {
            parser.throwErrorIfStrict(
                    "This widget cannot be disabled, as it does not implement Disableable interface. Received disabled attribute on tag: "
                            + tag.getTagName() + " with actor: " + actor);
        }
    }
}
