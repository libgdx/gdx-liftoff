package com.github.czyzby.lml.vis.parser.impl.tag.builder;

import com.github.czyzby.kiwi.util.gdx.scene2d.Actors;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;

/** Allows to build Vis windows.
 *
 * @author MJ */
public class VisWindowLmlActorBuilder extends TextLmlActorBuilder {
    private boolean showWindowBorder = true;

    @Override
    protected String getInitialStyleName() {
        // Default style varies, using null as initial. If is not changed, using default style according to
        // showWindowBorder.
        return null;
    }

    @Override
    public String getStyleName() {
        final String style = super.getStyleName();
        if (style == null) {
            return showWindowBorder ? Actors.DEFAULT_STYLE : "noborder";
        }
        return style;
    }

    /** @param showWindowBorder true if should show window border. */
    public void setShowWindowBorder(final boolean showWindowBorder) {
        this.showWindowBorder = showWindowBorder;
    }

    /** @return true if should show window border. */
    public boolean isShowingWindowBorder() {
        return showWindowBorder;
    }
}
