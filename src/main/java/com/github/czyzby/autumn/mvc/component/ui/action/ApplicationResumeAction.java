package com.github.czyzby.autumn.mvc.component.ui.action;

import com.badlogic.gdx.Gdx;
import com.github.czyzby.lml.parser.action.ActorConsumer;

/** LML view action. Forces application's resuming on invocation.
 *
 * @author MJ */
public class ApplicationResumeAction implements ActorConsumer<Void, Object> {
    /** Name of the action as it appears in the templates. Can be changed globally before the context loading. */
    public static String ID = "app:resume";

    @Override
    public Void consume(final Object actor) {
        Gdx.app.getApplicationListener().resume();
        return null;
    }
}
