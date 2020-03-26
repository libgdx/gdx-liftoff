package com.github.czyzby.lml.vis.ui.reflected.action;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.github.czyzby.lml.vis.parser.impl.tag.TabbedPaneLmlTag.LmlTabbedPaneListener;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

/** Changes current tab to a new one.
 *
 * @author MJ
 * @see com.github.czyzby.lml.vis.parser.impl.tag.TabbedPaneLmlTag */
public class TabShowingAction extends Action {
    private boolean shown;
    private Tab tabToShow;
    private LmlTabbedPaneListener listener;

    /** Chaining action for pooling utility.
     *
     * @param tabToShow will be shown.
     * @param listener manages tabs.
     * @return this for chaining. */
    public TabShowingAction show(final Tab tabToShow, final LmlTabbedPaneListener listener) {
        this.tabToShow = tabToShow;
        this.listener = listener;
        shown = false;
        return this;
    }

    @Override
    public boolean act(final float delta) {
        if (!shown) {
            shown = true;
            listener.setNewTab(tabToShow);
        }
        return true;
    }

    @Override
    public void restart() {
        shown = false;
    }
}