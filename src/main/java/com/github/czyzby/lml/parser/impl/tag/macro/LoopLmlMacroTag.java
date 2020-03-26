package com.github.czyzby.lml.parser.impl.tag.macro;

import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Allows to repeat chosen template part multiple times. Expects an int attribute: runs amount. Allows to access its
 * iteration index. For example: <blockquote>
 *
 * <pre>
 * &lt;:loop 3&gt;txt{loop:index} &lt;/:loop&gt;
 * </pre>
 *
 * </blockquote>This loop will print: "txt0 txt1 txt2 ".
 *
 * <p>
 * Tip: if you need to iterate over non-standard number range, use {@link ForEachLmlMacroTag} with a range argument
 * instead: <blockquote>
 *
 * <pre>
 * &lt;:each index=[4,-2]&gt;{index} &lt;/:each&gt;
 * </pre>
 *
 * </blockquote>This will prints: "4 3 2 1 0 -1 -2 ". (If the start of the range is bigger than the end, value will be
 * decremented on each iteration turn. For example, [-2,4] range would print "-2 -1 0 1 2 3 4 ").
 *
 * @author MJ */
public class LoopLmlMacroTag extends AbstractLoopLmlMacroTag {
    private final int stepsAmount;
    private int currentIndex;

    public LoopLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
        stepsAmount = getStepsAmount();
    }

    private int getStepsAmount() {
        if (GdxArrays.isEmpty(getAttributes())) {
            getParser().throwErrorIfStrict("Loop macro needs at least one attribute: runs amount.");
            return 0;
        }
        final int amount;
        if (hasAttribute(TIMES_ATTRIBUTE)) {
            amount = getParser().parseInt(getAttribute(TIMES_ATTRIBUTE), getActor());
        } else {
            amount = getParser().parseInt(getAttributes().first(), getActor());
        }
        if (amount < 0) {
            getParser().throwErrorIfStrict("Loop macro runs amount cannot be negative.");
            return 0;
        }
        return amount;
    }

    @Override
    protected boolean hasNext() {
        return currentIndex < stepsAmount;
    }

    @Override
    protected int getIndex() {
        return currentIndex;
    }

    @Override
    protected void next(final ObjectMap<String, String> arguments) {
        currentIndex++;
    }

    @Override
    public String[] getExpectedAttributes() {
        return new String[] { TIMES_ATTRIBUTE };
    }
}
