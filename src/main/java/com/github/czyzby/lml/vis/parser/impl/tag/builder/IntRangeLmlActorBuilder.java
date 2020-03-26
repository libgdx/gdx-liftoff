package com.github.czyzby.lml.vis.parser.impl.tag.builder;

import com.github.czyzby.lml.parser.tag.LmlActorBuilder;

/** Represents a range of int values. By default, creates [0, 100] range with step size of 1.
 *
 * @author MJ */
public class IntRangeLmlActorBuilder extends LmlActorBuilder {
    private int min;
    private int max = 100;
    private int step = 1;
    private int value;

    /** @return range start. */
    public int getMin() {
        return min;
    }

    /** @param min range start. */
    public void setMin(final int min) {
        this.min = min;
    }

    /** @return range end. */
    public int getMax() {
        return max;
    }

    /** @param max range end. */
    public void setMax(final int max) {
        this.max = max;
    }

    /** @return lowest possible incrementation value in the range. */
    public int getStep() {
        return step;
    }

    /** @param step lowest possible incrementation value in the range. */
    public void setStep(final int step) {
        this.step = step;
    }

    /** @return initial range value. */
    public int getValue() {
        return value;
    }

    /** @param value initial range value. */
    public void setValue(final int value) {
        this.value = value;
    }
}
