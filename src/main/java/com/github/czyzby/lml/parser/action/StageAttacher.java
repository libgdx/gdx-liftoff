package com.github.czyzby.lml.parser.action;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

/** Allows to pass actions executed when widgets are attached to a stage. Note that it does not have to actually add the
 * widget to a stage, as LML parsers should already do that - attacher's purpose is to add additional actions.
 *
 * @author MJ */
public interface StageAttacher {
    /** Allows to execute extra actions upon adding to a stage.
     *
     * @param actor is added to the stage.
     * @param stage actor should be already added to this stage. */
    void attachToStage(Actor actor, Stage stage);
}
