package com.github.czyzby.lml.parser.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.kiwi.util.gdx.scene2d.Actors;
import com.github.czyzby.lml.parser.impl.DefaultLmlData;
import com.github.czyzby.lml.util.LmlUtilities;
import com.github.czyzby.lml.util.LmlUserObject.TableTarget;

/** Contains data necessary to construct widgets.
 *
 * @author MJ */
public class LmlActorBuilder {
    private String styleName = getInitialStyleName();
    private String skinName = DefaultLmlData.DEFAULT_KEY;
    private TableTarget tableTarget;

    /** @return defaults to "default". */
    protected String getInitialStyleName() {
        return Actors.DEFAULT_STYLE;
    }

    /** @return name of the style as it appears in the skin. */
    public String getStyleName() {
        return styleName;
    }

    /** @param styleName name of the style as it appears in the skin. */
    public void setStyleName(final String styleName) {
        this.styleName = styleName;
    }

    /** @return name of the skin containing widget's style. */
    public String getSkinName() {
        return skinName;
    }

    /** @param skinName name of the skin containing widget's style. */
    public void setSkinName(final String skinName) {
        this.skinName = skinName;
    }

    /** @param tableTarget determines how the actor is added to a table. Can be null. */
    public void setTableTarget(final TableTarget tableTarget) {
        this.tableTarget = tableTarget;
    }

    /** @return object that determines how the actor is added to a table. Can be null. */
    public TableTarget getTableTarget() {
        return tableTarget;
    }

    /** @param actor is now created and the builder might need to finalize building. Should be invoked after a new
     *            instance of actor is constructed and before attributes are parsed. */
    public void finishBuilding(final Actor actor) {
        if (tableTarget != null) {
            LmlUtilities.getLmlUserObject(actor).setTableTarget(tableTarget);
        }
    }
}
