package com.github.czyzby.lml.vis.parser.impl.attribute.listview;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.ListView;

/** See {@link ListView#setHeader(Actor)}. Expects a boolean. If true is passed, actor becomes list view's header and
 * will not be added as a view item. Can be used only in list view's children. Mapped to "header".
 *
 * @author MJ */
public class HeaderLmlAttribute extends ListViewChildLmlAttribute {
    @Override
    protected void process(final LmlParser parser, final LmlTag tag, final Actor actor, final ListView<?> listView,
            final String rawAttributeData) {
        if (parser.parseBoolean(rawAttributeData, actor)) {
            listView.setHeader(actor);
        }
    }
}
