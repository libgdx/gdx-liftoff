package com.github.czyzby.lml.vis.parser.impl.attribute.listview;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.ListView;
import com.kotcrab.vis.ui.widget.ListView.ItemClickListener;
import com.kotcrab.vis.ui.widget.ListView.ListViewTable;

/** See {@link ListView#setItemClickListener(ItemClickListener)}. Expects ID of an action (method invocation marker is
 * optional) consuming an Object, which will represent an item in the list view. Mapped to "itemListener",
 * "itemClickListener".
 *
 * @author MJ */
public class ItemClickListenerLmlAttribute extends ListViewLmlAttribute {
    @Override
    @SuppressWarnings("unchecked")
    protected void process(final LmlParser parser, final LmlTag tag, final ListViewTable<?> mainTable,
            final ListView<?> listView, final String rawAttributeData) {
        final ActorConsumer<?, Object> listener = parser.parseAction(rawAttributeData);
        if (listener == null) {
            parser.throwError("Invalid action ID: " + rawAttributeData
                    + ". Expected an action reference for item click listener, found no fitting action.");
        }
        ((ListView<Object>) listView).setItemClickListener(new ItemClickListener<Object>() {
            @Override
            public void clicked(final Object item) {
                listener.consume(item);
            }
        });
    }
}
