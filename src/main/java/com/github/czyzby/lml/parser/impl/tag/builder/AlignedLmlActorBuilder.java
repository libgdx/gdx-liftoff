package com.github.czyzby.lml.parser.impl.tag.builder;

import com.github.czyzby.kiwi.util.gdx.scene2d.Actors;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;

/** Used to build widgets that are either horizontal or vertical and need this setting to both set this alignment in the
 * constructor and get proper initial style name.
 *
 * @author MJ
 * @see Actors#DEFAULT_HORIZONTAL_STYLE
 * @see Actors#DEFAULT_VERTICAL_STYLE */
public class AlignedLmlActorBuilder extends LmlActorBuilder {
    private boolean vertical; // Vertical setting is required by most constructors.

    @Override
    protected String getInitialStyleName() {
        return null; // Nulling out initial style. No style means default, as default style varies with setting.
    }

    @Override
    public String getStyleName() {
        final String style = super.getStyleName();
        if (style == null) {
            return vertical ? Actors.DEFAULT_VERTICAL_STYLE : Actors.DEFAULT_HORIZONTAL_STYLE;
        }
        return style;
    }

    /** @return true if the actor should be initiated as horizontal. */
    public boolean isHorizontal() {
        return !vertical;
    }

    /** @param horizontal if true, widget will be initiated as horizontal. */
    public void setHorizontal(final boolean horizontal) {
        vertical = !horizontal;
    }

    /** @return true if the actor should be initiated as vertical. */
    public boolean isVertical() {
        return vertical;
    }

    /** @param vertical if true, widget will be initiated as vertical. */
    public void setVertical(final boolean vertical) {
        this.vertical = vertical;
    }
}
