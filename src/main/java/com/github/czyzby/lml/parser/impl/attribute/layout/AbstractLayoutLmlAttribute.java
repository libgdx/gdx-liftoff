package com.github.czyzby.lml.parser.impl.attribute.layout;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Abstract base for {@link Layout} attributes.
 *
 * @author MJ */
public abstract class AbstractLayoutLmlAttribute implements LmlAttribute<Actor> {
    @Override
    public Class<Actor> getHandledType() {
        return Actor.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final String rawAttributeData) {
        if (actor instanceof Layout) {
            process(parser, tag, (Layout) actor, actor, rawAttributeData);
        } else {
            parser.throwErrorIfStrict(this.getClass().getSimpleName()
                    + " can be added only to actors that implement Layout interface. Tag: " + tag.getTagName()
                    + " with actor: " + actor + " cannot have this attribute.");
        }
    }

    /** @param parser handles LML template parsing.
     * @param tag contains raw tag data. Allows to access actor's parent.
     * @param layout casted actor.
     * @param actor handled actor instance. Implements {@link Layout}.
     * @param rawAttributeData unparsed LML attribute data that should be handled by this attribute processor. Common
     *            data types (string, int, float, boolean, action) are already handled by LML parser implementation, so
     *            make sure to invoke its methods. */
    protected abstract void process(LmlParser parser, LmlTag tag, Layout layout, Actor actor, String rawAttributeData);
}
