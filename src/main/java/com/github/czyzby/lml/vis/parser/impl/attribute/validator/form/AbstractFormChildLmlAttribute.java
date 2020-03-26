package com.github.czyzby.lml.vis.parser.impl.attribute.validator.form;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.ui.VisFormTable;

/** Abstract base for form children attributes.
 *
 * @author MJ
 *
 * @param <Widget> type of managed children. */
public abstract class AbstractFormChildLmlAttribute<Widget> implements LmlAttribute<Widget> {
    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Widget actor, final String rawAttributeData) {
        final VisFormTable parent = getFormParent(tag);
        if (parent != null) {
            processFormAttribute(parser, tag, parent, actor, rawAttributeData);
        } else {
            parser.throwErrorIfStrict(
                    "This attribute can be attached only to children of form validators. Found form attribute on widget: "
                            + actor);
        }
    }

    /** @param tag original tag of the widget.
     * @return {@link VisFormTable} parent widget or null if not in a form. */
    protected VisFormTable getFormParent(final LmlTag tag) {
        LmlTag parent = tag.getParent();
        while (parent != null) {
            if (parent.getActor() instanceof VisFormTable) {
                return (VisFormTable) parent.getActor();
            }
            parent = parent.getParent();
        }
        return null;
    }

    /** @param parser handles LML template parsing.
     * @param tag contains raw tag data. Allows to access actor's parent.
     * @param parent widget in which the actor is present.
     * @param actor handled actor instance, casted for convenience.
     * @param rawAttributeData unparsed LML attribute data that should be handled by this attribute processor. Common
     *            data types (string, int, float, boolean, action) are already handled by LML parser implementation, so
     *            make sure to invoke its methods. */
    protected abstract void processFormAttribute(LmlParser parser, LmlTag tag, VisFormTable parent, Widget actor,
            String rawAttributeData);
}
