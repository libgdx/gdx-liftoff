package com.github.czyzby.lml.vis.parser.impl.attribute.building;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlBuildingAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.parser.impl.tag.builder.ListViewLmlActorBuilder;
import com.kotcrab.vis.ui.util.adapter.ListAdapter;

/** Allows to use a custom {@link ListAdapter} in a {@link com.kotcrab.vis.ui.widget.ListView}. Expects an action ID
 * (invocation marker optional) returning a list adapter instance. This attribute is parsed BEFORE the list view is
 * created, so the action should not expect any parameters. Mapped to "adapter", "listAdapter".
 *
 * @author MJ */
public class ListAdapterLmlAttribute implements LmlBuildingAttribute<ListViewLmlActorBuilder> {
    @Override
    public Class<ListViewLmlActorBuilder> getBuilderType() {
        return ListViewLmlActorBuilder.class;
    }

    @Override
    public boolean process(final LmlParser parser, final LmlTag tag, final ListViewLmlActorBuilder builder,
            final String rawAttributeData) {
        @SuppressWarnings("unchecked") final ActorConsumer<ListAdapter<?>, Object> adapterProducer = (ActorConsumer<ListAdapter<?>, Object>) parser
                .parseAction(rawAttributeData);
        if (adapterProducer == null) {
            parser.throwErrorIfStrict(
                    "ListView adapter attribute expects a valid action returning custom ListAdapter instance. No action found for ID: "
                            + rawAttributeData);
        } else {
            final ListAdapter<?> adapter = adapterProducer.consume(null);
            builder.setListAdapter(adapter);
        }
        return FULLY_PARSED;
    }
}
