package com.github.czyzby.lml.scene2d.ui.reflected;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import com.github.czyzby.lml.parser.LmlParserListener;

/** Utility Table extension made specifically to work as content of a tooltip. Holds a reference to the {@link Tooltip}
 * that is represented by this table. Otherwise, it works like any other {@link Table} widget. For simplified
 * construction, use {@link #create(Skin, TooltipManager)} factory method.
 *
 * @author MJ */
public class TooltipTable extends Table {
    private final Tooltip<TooltipTable> tooltip;
    private String[] ids;
    private boolean keep = LmlParserListener.REMOVE;

    /** @param skin used to construct table children (for example: labels out of plain text).
     * @param tooltip will be represented by this table. */
    public TooltipTable(final Skin skin, final Tooltip<TooltipTable> tooltip) {
        super(skin);
        this.tooltip = tooltip;
    }

    /** @return tooltip represented by this table. */
    public Tooltip<TooltipTable> getTooltip() {
        return tooltip;
    }

    /** Utility for LML parsing.
     *
     * @param ids optional set of actor IDs that the tooltip should be attached to. */
    public void setIds(final String[] ids) {
        this.ids = ids;
    }

    /** @return optional set of actor IDs that the tooltip should be attached to. Might be null.
     * @see #setIds(String[]) */
    public String[] getIds() {
        return ids;
    }

    /** Utility for LML parsing.
     *
     * @param keep if set to true, this tooltip will be attached to actors with the selected IDs even in the next parsed
     *            template. This allows you to share tooltip instances across multiple views. */
    public void setKeep(final boolean keep) {
        this.keep = keep;
    }

    /** @return if true, this tooltip should be attached to actors with the selected IDs even in all following
     *         templates. This allows to share tooltip instances across multiple views.
     * @see #setKeep(boolean) */
    public boolean isKept() {
        return keep;
    }

    /** @param skin used to construct table.
     * @param tooltipManager used to construct tooltip.
     * @return a new TooltipTable instance with a fully initiated tooltip accessible with {@link #getTooltip()}
     *         method. */
    public static TooltipTable create(final Skin skin, final TooltipManager tooltipManager) {
        final Tooltip<TooltipTable> tooltip = new Tooltip<TooltipTable>(null, tooltipManager);
        final TooltipTable content = new TooltipTable(skin, tooltip);
        tooltip.setActor(content);
        return content;
    }
}
