package com.github.czyzby.lml.vis.parser.impl.attribute.tabbed;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.parser.impl.tag.TabbedPaneLmlTag;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;

/** See {@link TabbedPaneLmlTag#setAttachDefaultListener(boolean)}. Mapped to "defaultListener",
 * "attachDefaultListener".
 *
 * @author MJ */
public class AttachDefaultTabListenerLmlAttribute extends AbstractTabbedPaneLmlAttribute {
    @Override
    protected void process(final LmlParser parser, final LmlTag tag, final TabbedPane tabbedPane,
            final String rawAttributeData) {
        if (tag instanceof TabbedPaneLmlTag) {
            ((TabbedPaneLmlTag) tag).setAttachDefaultListener(parser.parseBoolean(rawAttributeData, tabbedPane));
        } else {
            parser.throwErrorIfStrict(
                    "Unexpected tag type for tabbed pane tag. Expected: TabbedPaneLmlTag, got: " + tag.getClass());
        }
    }
}
