package com.github.czyzby.autumn.mvc.component.ui.action;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewController;

/** A simple supplier interface that creates an action designed for specific view.
 *
 * @author MJ */
public interface ActionProvider {
    /** @param forController request an action instance.
     * @param connectedView next, previous or otherwise connected screen. Allows to determine which screen will be next,
     *            for example.
     * @return a new action for the selected controller. */
    Action provideAction(ViewController forController, ViewController connectedView);
}