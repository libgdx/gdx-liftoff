package com.github.czyzby.lml.vis.parser.impl.tag;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractGroupLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.layout.FloatingGroup;

/** Handles {@link FloatingGroup} widgets. Appends plain text as label children. Does not force any specific widget
 * order - honors widgets' position and size settings. Setting "prefHeight" and "prefWidth" (or "prefSize") is advised.
 * Mapped to "floatingGroup".
 *
 * @author MJ */
public class FloatingGroupLmlTag extends AbstractGroupLmlTag {
    public FloatingGroupLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected FloatingGroup getNewInstanceOfGroup(final LmlActorBuilder builder) {
        return new FloatingGroup();
    }
}
