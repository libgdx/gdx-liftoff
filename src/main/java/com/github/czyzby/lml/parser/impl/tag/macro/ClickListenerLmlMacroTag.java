package com.github.czyzby.lml.parser.impl.tag.macro;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Attaches {@link ClickListener} to its actor parent. When the actor is clicked, content between macro tags will be
 * parsed using {@link LmlParser#parseTemplate(String)} and the returned actors will be added to the stage. This is very
 * useful for adding "delayed" actors creation that should occur only if certain event is detected. For example, this
 * shows a dialog when the button is clicked:
 *
 * <blockquote>
 *
 * <pre>
 * &lt;button&gt;
 *   &lt;:onClick&gt;
 *     &lt;dialog&gt;Message!&lt;/dialog&gt;
 *   &lt;/:onClick&gt;
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
 *   &lt;:onClick if="$myMethod == 13" cache="true" button="1" &gt;
 *     &lt;dialog&gt;Message!&lt;/dialog&gt;
 *   &lt;/:onClick&gt;
 * &lt;/button&gt;
 * </pre>
 *
 * </blockquote>In the example above, the actors will be displayed only if the result of myMethod is 13. Also, actors
 * will be cached after first parsing ("cache" attribute). The listener will be triggered only by the mouse button with
 * ID of 1 ("button" attribute).
 *
 * @author MJ */
public class ClickListenerLmlMacroTag extends AbstractListenerLmlMacroTag {
    public static final String BUTTON_ATTRIBUTE = "button";
    private final ClickListener listener = new ClickListener() {
        @Override
        public void clicked(final InputEvent event, final float x, final float y) {
            doOnEvent(event.getListenerActor());
        }
    };

    public ClickListenerLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    public void closeTag() {
        super.closeTag();
        if (hasAttribute(BUTTON_ATTRIBUTE)) {
            final int button = getParser().parseInt(getAttribute(BUTTON_ATTRIBUTE), getActor());
            listener.setButton(button);
        }
    }

    @Override
    protected ClickListener getEventListener() {
        return listener;
    }

    @Override
    public String[] getExpectedAttributes() {
        return new String[] { IF_ATTRIBUTE, CACHE_ATTRIBUTE, KEEP_ATTRIBUTE, IDS_ATTRIBUTE, BUTTON_ATTRIBUTE };
    }
}
