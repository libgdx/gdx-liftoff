package com.github.czyzby.autumn.mvc.component.ui.controller;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ObjectMap;

/** Manages view creation.
 *
 * @author MJ */
public interface ViewInitializer {
    /** Called each time the view is created. Processed after LML template parsing.
     *
     * @param stage managed by the view.
     * @param actorMappedByIds all actions with IDs that were referenced in the LML template. Should not be modified or
     *            assigned, copy map's values if you want to keep the references. */
    void initialize(Stage stage, ObjectMap<String, Actor> actorMappedByIds);

    /** Called each time the view is destroyed.
     *
     * @param viewController a reference to view controller for extra utility. */
    void destroy(ViewController viewController);
}
