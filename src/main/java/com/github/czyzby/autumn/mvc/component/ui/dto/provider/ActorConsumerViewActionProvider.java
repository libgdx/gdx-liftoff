package com.github.czyzby.autumn.mvc.component.ui.dto.provider;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;

/** Wraps around an {@link com.github.czyzby.lml.parser.action.ActorConsumer}.
 *
 * @author MJ */
public class ActorConsumerViewActionProvider extends AbstractViewActionProvider {
    private final String id;
    private final ActorConsumer<?, ?> actorConsumer;

    public ActorConsumerViewActionProvider(final String id, final ActorConsumer<?, ?> actorConsumer,
            final String[] viewIds) {
        super(viewIds);
        this.id = id;
        this.actorConsumer = actorConsumer;
    }

    @Override
    protected void register(final LmlParser parser) {
        parser.getData().addActorConsumer(id, actorConsumer);
    }

    @Override
    protected void unregister(final LmlParser parser) {
        parser.getData().removeActorConsumer(id);
    }
}