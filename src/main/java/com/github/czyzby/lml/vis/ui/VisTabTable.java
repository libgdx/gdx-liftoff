package com.github.czyzby.lml.vis.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

/** {@link VisTable} extension that manages an internal {@link Tab} implementation. Meant to be used as
 * {@link com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane} tab child.
 *
 * @author MJ */
public class VisTabTable extends VisTable {
    private final TableTab tab = new TableTab();
    private String title;
    private boolean disabled;
    private ActorConsumer<?, VisTabTable> onDispose;
    private ActorConsumer<?, VisTabTable> onShow;
    private ActorConsumer<?, VisTabTable> onHide;
    private ActorConsumer<Boolean, VisTabTable> onSave;

    /** @param title tab's title. */
    public VisTabTable(final String title) {
        this.title = title;
    }

    /** @return implementation of {@link Tab} managed by this table. */
    public TableTab getTab() {
        return tab;
    }

    /** @return title used by the tab. */
    public String getTitle() {
        return title;
    }

    /** @param onDispose will be invoked (consuming this table) when the tab is being removed from the pane. */
    public void setOnDispose(final ActorConsumer<?, VisTabTable> onDispose) {
        this.onDispose = onDispose;
    }

    /** @param onSave will be invoked each time the tab is saved. If returns true, tab will be set as saved and will not
     *            be dirty anymore. */
    public void setOnSave(final ActorConsumer<Boolean, VisTabTable> onSave) {
        this.onSave = onSave;
    }

    /** @param onShow will be invoked with this table each time the tab is shown. */
    public void setOnShow(final ActorConsumer<?, VisTabTable> onShow) {
        this.onShow = onShow;
    }

    /** @param onHide will be invoked with this table each time the tab is hidden. */
    public void setOnHide(final ActorConsumer<?, VisTabTable> onHide) {
        this.onHide = onHide;
    }

    /** @param title will become title of the tab. Note that this value should be changed only if the tab is currently
     *            not shown. */
    public void setTitle(final String title) {
        this.title = title;
    }

    /** @param disabled will change the disabled status of the tab if it is currently in a table. If the tab is not in a
     *            pane, this setting will be accessible through {@link #isDisabled()} method and should be set
     *            manually. */
    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
        if (tab.getPane() != null) {
            tab.getPane().disableTab(tab, disabled);
        }
    }

    /** @return true if the tab was set as disabled. */
    public boolean isDisabled() {
        return disabled;
    }

    /** Internal {@link Tab} implementation used by {@link VisTabTable}.
     *
     * @author MJ */
    public class TableTab extends Tab {
        private boolean closeableByUser = true;
        private boolean savable;

        @Override
        public boolean isCloseableByUser() {
            return closeableByUser;
        }

        /** @param closeableByUser will be returned by {@link #isCloseableByUser()} when adding the tab. Note that this
         *            method should be used only before the tab is added. */
        public void setCloseableByUser(final boolean closeableByUser) {
            this.closeableByUser = closeableByUser;
        }

        @Override
        public boolean isSavable() {
            return savable;
        }

        /** @param savable will be returned by {@link #isSavable()} when adding the tab to the pane. Note that this
         *            method should be used only before the tab is added. */
        public void setSavable(final boolean savable) {
            this.savable = savable;
        }

        @Override
        public String getTabTitle() {
            return title;
        }

        @Override
        public Table getContentTable() {
            return VisTabTable.this;
        }

        @Override
        public boolean save() {
            super.save();
            if (onSave == null || onSave.consume(VisTabTable.this)) {
                setDirty(false);
                return true;
            }
            return false;
        }

        @Override
        public void onShow() {
            super.onShow();
            if (onShow != null) {
                onShow.consume(VisTabTable.this);
            }
        }

        @Override
        public void onHide() {
            super.onHide();
            if (onHide != null) {
                onHide.consume(VisTabTable.this);
            }
        }

        @Override
        public void dispose() {
            if (onDispose != null) {
                onDispose.consume(VisTabTable.this);
            }
        }
    }
}
