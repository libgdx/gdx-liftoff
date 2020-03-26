package com.github.czyzby.lml.vis.parser.impl.attribute.tabbed;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane.TabbedPaneTable;

/** Abstract base for {@link TabbedPane} attributes.
 *
 * @author MJ */
public abstract class AbstractTabbedPaneLmlAttribute implements LmlAttribute<TabbedPaneTable> {
    /** Mock-up {@link Tab} instance. Can be used to retrieve actions that consume a tab. Do not use in actual
     * scenes. */
    public static final Tab MOCK_UP_TAB = new Tab() {
        @Override
        public String getTabTitle() {
            return null;
        }

        @Override
        public Table getContentTable() {
            return null;
        }
    };

    @Override
    public Class<TabbedPaneTable> getHandledType() {
        return TabbedPaneTable.class;
    }

    @Override
    public final void process(final LmlParser parser, final LmlTag tag, final TabbedPaneTable actor,
            final String rawAttributeData) {
        process(parser, tag, actor.getTabbedPane(), rawAttributeData);
    }

    /** @param parser handles LML template parsing.
     * @param tag contains raw tag data. Allows to access actor's parent.
     * @param tabbedPane handled tabbed pane instance.
     * @param rawAttributeData unparsed LML attribute data that should be handled by this attribute processor. Common
     *            data types (string, int, float, boolean, action) are already handled by LML parser implementation, so
     *            make sure to invoke its methods. */
    protected abstract void process(LmlParser parser, LmlTag tag, TabbedPane tabbedPane, String rawAttributeData);
}
