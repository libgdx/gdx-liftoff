package com.github.czyzby.lml.parser.impl.attribute;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** If true, some widgets might parse plain text between their tags differently. For example: <blockquote>
 *
 * <pre>
 * &lt;label&gt;First.
 *      Second.&lt;/label&gt;
 * &lt;label multiline=true&gt;First.
 *      Second.&lt;/label&gt;
 * </pre>
 *
 * </blockquote>By default, labels are single-line, so first label's text would be "First.Second.". Thanks to multiline
 * property, second label's text would be "First.\nSecond.". This attribute expects a boolean. By default, it is mapped
 * to "multiline" attribute name. While the mechanism used is common for all widgets, note that it will work only for
 * labels, text buttons, image text buttons and text areas (might work on text fields, too: the new line char is
 * appended, but text fields do not seem to support it by default).
 *
 * @author MJ */
public class MultilineLmlAttribute implements LmlAttribute<Actor> {
    @Override
    public Class<Actor> getHandledType() {
        return Actor.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final String rawAttributeData) {
        LmlUtilities.getLmlUserObject(actor).setData(Boolean.valueOf(parser.parseBoolean(rawAttributeData, actor)));
    }
}
