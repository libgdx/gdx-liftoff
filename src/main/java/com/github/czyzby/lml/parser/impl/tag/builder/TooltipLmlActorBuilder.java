package com.github.czyzby.lml.parser.impl.tag.builder;

import com.github.czyzby.lml.parser.impl.DefaultLmlData;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;

/** Contains tooltip manager ID, allowing to choose a specific manager for each tooltip.
 *
 * @author MJ */
public class TooltipLmlActorBuilder extends LmlActorBuilder {
    private String tooltipManager = DefaultLmlData.DEFAULT_KEY;

    /** @return ID of the tooltip manager that should be used to create the tooltip. */
    public String getTooltipManager() {
        return tooltipManager;
    }

    /** @param tooltipManager ID of the tooltip manager used to create the tooltip. */
    public void setTooltipManager(final String tooltipManager) {
        this.tooltipManager = tooltipManager;
    }
}
