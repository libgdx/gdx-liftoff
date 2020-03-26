package com.github.czyzby.lml.vis.parser.impl.tag.builder;

import com.github.czyzby.lml.parser.tag.LmlActorBuilder;

/** Used to construct a range of (usually numeric) values represented by strings. This is especially useful for ranges
 * of large numbers, like big integers or big decimals. By default, constructs range of [0, 1] with step size of 0.01.
 *
 * @author MJ */
public class StringRangeLmlActorBuilder extends LmlActorBuilder {
    private String min = "0";
    private String max = "1";
    private String step = "0.01";
    private String value = "0";

    /** @return lowest possible value. */
    public String getMin() {
        return min;
    }

    /** @param min lowest possible value. */
    public void setMin(final String min) {
        this.min = min;
    }

    /** @return highest possible value. */
    public String getMax() {
        return max;
    }

    /** @param max highest possible value. */
    public void setMax(final String max) {
        this.max = max;
    }

    /** @return incrementation value. */
    public String getStep() {
        return step;
    }

    /** @param step incrementation value. */
    public void setStep(final String step) {
        this.step = step;
    }

    /** @return initial value. */
    public String getValue() {
        return value;
    }

    /** @param value initial value. */
    public void setValue(final String value) {
        this.value = value;
    }
}
