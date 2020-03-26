package com.github.czyzby.lml.vis.parser.impl.tag.builder;

import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.kotcrab.vis.ui.util.adapter.ListAdapter;

/** Allows to build {@link com.kotcrab.vis.ui.widget.ListView} widgets.
 *
 * @author MJ */
public class ListViewLmlActorBuilder extends LmlActorBuilder {
    private ListAdapter<?> listAdapter;

    /** @return view's custom list adapter. Might be null. */
    public ListAdapter<?> getListAdapter() {
        return listAdapter;
    }

    /** @param listAdapter custom list adapter. Null to clear. */
    public void setListAdapter(final ListAdapter<?> listAdapter) {
        this.listAdapter = listAdapter;
    }
}
