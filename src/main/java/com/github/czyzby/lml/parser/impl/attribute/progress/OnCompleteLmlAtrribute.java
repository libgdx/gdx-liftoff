package com.github.czyzby.lml.parser.impl.attribute.progress;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Adds a change listener that invokes an action when progress bar value changes and reaches its maximum value. Expects
 * an action ID. Mapped to "onComplete", "complete". Note that it might be invoked multiple times if change events of
 * reaching max value are posted more than once. If the referenced action returns true (boolean), listener will be
 * removed. See {@link #REMOVE_LISTENER}.
 *
 * @author MJ */
public class OnCompleteLmlAtrribute implements LmlAttribute<ProgressBar> {
    /** If returned by the action referenced in the attribute, attached listener will be removed. Utility reference for
     * code clarity. This matches boolean true value; if false or null is returned, listener is kept. */
    public static final Boolean REMOVE_LISTENER = Boolean.TRUE;

    @Override
    public Class<ProgressBar> getHandledType() {
        return ProgressBar.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final ProgressBar actor,
            final String rawAttributeData) {
        final ActorConsumer<?, ProgressBar> action = parser.parseAction(rawAttributeData, actor);
        if (action == null) {
            parser.throwErrorIfStrict(
                    "Unable to attach listener for " + actor + " with invalid action ID: " + rawAttributeData);
            return;
        }
        actor.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor widget) {
                if (actor.getValue() >= actor.getMaxValue()) {
                    final Object result = action.consume(actor);
                    if (result instanceof Boolean && ((Boolean) result).booleanValue()) {
                        actor.removeListener(this);
                    }
                }
            }
        });
    }
}
