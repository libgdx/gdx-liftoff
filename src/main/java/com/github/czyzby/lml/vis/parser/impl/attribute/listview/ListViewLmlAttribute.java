package com.github.czyzby.lml.vis.parser.impl.attribute.listview;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.ListView;
import com.kotcrab.vis.ui.widget.ListView.ListViewTable;

/** Abstract base for {@link ListView} tag attributes.
 *
 * @author MJ */
public abstract class ListViewLmlAttribute implements LmlAttribute<ListViewTable<?>> {
    @Override
    @SuppressWarnings("unchecked")
    public Class<ListViewTable<?>> getHandledType() {
        return (Class<ListViewTable<?>>) (Object) ListViewTable.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final ListViewTable<?> actor,
            final String rawAttributeData) {
        final ListView<?> listView = actor.getListView();
        process(parser, tag, actor, listView, rawAttributeData);
    }

    /** @param parser handles LML template parsing.
     * @param tag contains raw tag data. Allows to access actor's parent.
     * @param mainTable list view's main table.
     * @param listView actual list view instance.
     * @param rawAttributeData unparsed LML attribute data that should be handled by this attribute processor. Common
     *            data types (string, int, float, boolean, action) are already handled by LML parser implementation, so
     *            make sure to invoke its methods. */
    protected abstract void process(LmlParser parser, LmlTag tag, ListViewTable<?> mainTable, ListView<?> listView,
            String rawAttributeData);
}
