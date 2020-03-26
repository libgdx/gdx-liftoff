package com.github.czyzby.lml.parser;

import com.badlogic.gdx.scenes.scene2d.Stage;

/** Optional interface for view classes filled with {@link LmlParser}. If {@link LmlParser#createView(Class, String)},
 * {@link LmlParser#createView(Class, com.badlogic.gdx.files.FileHandle)}, {@link LmlParser#createView(Object, String)}
 * or {@link LmlParser#createView(Object, com.badlogic.gdx.files.FileHandle)} method is used and the passed view type
 * implements this interface, its stage will be filled with the actors.
 *
 * @author MJ */
public interface LmlView {
    /** @return stage used by the view. Will be filled with parsed actors after successful parsing of a selected LML
     *         template. */
    Stage getStage();

    /** @return ID of the view. Does not have to be unique - it just cannot collide with other action containers that
     *         you use. If the {@link LmlView} implementation also implements
     *         {@link com.github.czyzby.lml.parser.action.ActionContainer ActionContainer interface}, it will be added
     *         as an action container with this ID when its template is parsed. It will be removed after parsing to make
     *         sure that the actions are not available in other views. */
    String getViewId();
}
