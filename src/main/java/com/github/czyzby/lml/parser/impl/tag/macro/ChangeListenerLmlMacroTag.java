package com.github.czyzby.lml.parser.impl.tag.macro;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Attaches {@link ChangeListener} to its actor parent. When the actor's state is change, content between macro tags
 * will be parsed using {@link LmlParser#parseTemplate(String)} and the returned actors will be added to the stage. This
 * is very useful for adding "delayed" actors creation that should occur only if certain event is detected. For example,
 * this shows a dialog when the button is not disabled and is clicked:
 *
 * <blockquote>
 *
 * <pre>
 * &lt;button&gt;
 *   &lt;:onChange&gt;
 *     &lt;dialog&gt;Message!&lt;/dialog&gt;
 *   &lt;/:onChange&gt;
 * &lt;/button&gt;
 * </pre>
 *
 * </blockquote>However, you might want to cache the actors to prevent from parsing it each time the event occurs. Also,
 * you might need to show the actors only if certain condition is met. Both of these functionalities are supported:
 *
 * <blockquote>
 *
 * <pre>
 * &lt;button&gt;
 *   &lt;:onChange if="$myMethod == 13" cache="true" &gt;
 *     &lt;dialog&gt;Message!&lt;/dialog&gt;
 *   &lt;/:onChange&gt;
 * &lt;/button&gt;
 * </pre>
 *
 * </blockquote>In the example above, the actors will be displayed only if the result of myMethod is 13. Also, actors
 * will be cached after first parsing ("cache" attribute).
 *
 * @author MJ */
public class ChangeListenerLmlMacroTag extends AbstractListenerLmlMacroTag {
    private final ChangeListener listener = new ChangeListener() {
        @Override
        public void changed(final ChangeEvent event, final Actor actor) {
            doOnEvent(actor);
        }
    };

    public ChangeListenerLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected ChangeListener getEventListener() {
        return listener;
    }
}
