package com.github.czyzby.lml.parser.impl.tag.macro;

import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.macro.util.Equation;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Repeats its data between tags until its condition evaluates to true. Uses the same equation system as
 * {@link CalculationLmlMacroTag} and {@link ConditionalLmlMacroTag}. HAS to have attributes - its attributes are merged
 * into a single equation. For example:
 *
 * <blockquote>
 *
 * <pre>
 * &lt;:while 9 &lt; $someAction&gt;
 *     &lt;label&gt;Label: {while:index}&lt;/label&gt;
 * &lt;/:while&gt;
 * </pre>
 *
 * </blockquote>This macro would create labels until "someAction" result is greater than 9 (or longer than 9, in case
 * its a string).
 *
 * <p>
 * Be careful: arguments are parsed BEFORE the macro is evaluated, NOT each time. This would enter an endless loop:
 *
 * <blockquote>
 *
 * <pre>
 * &lt;:assign myArg 12 /&gt;
 * &lt;:while 9&lt;{myArg}&gt;
 *     &lt;label&gt;Label: {while:index}&lt;/label&gt;
 *     &lt;:calculate myArg --{myArg}/&gt;
 * &lt;/:while&gt;
 * </pre>
 *
 * </blockquote>Even though "myArg" attribute would be decremented on each run, loop will never end, as it will be
 * parsed to this before it is even running:
 *
 * <blockquote>
 *
 * <pre>
 * &lt;:while 9&lt;12&gt;
 *     &lt;label&gt;Label: {while:index}&lt;/label&gt;
 *     &lt;:calculate myArg --{myArg}/&gt;
 * &lt;/:while&gt;
 * </pre>
 *
 * </blockquote>
 *
 * <p>
 * Note that this macro can be also used with named attributes:<blockquote>
 *
 * <pre>
 * &lt;:while test="9 &lt; $someAction"&gt;
 *     &lt;label&gt;Label: {while:index}&lt;/label&gt;
 * &lt;/:while&gt;
 * </pre>
 *
 * </blockquote>
 *
 * @author MJ */
public class WhileLmlMacroTag extends AbstractLoopLmlMacroTag {
    private final Equation equationParser;
    private final String equation;
    private int currentIndex;

    public WhileLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
        equation = getEquation();
        equationParser = getEquationParser();
    }

    private String getEquation() {
        if (GdxArrays.isEmpty(getAttributes())) {
            getParser().throwErrorIfStrict(
                    "While macro needs tag attributes to determine condition. Found no attributes on tag: "
                            + getTagName());
            return null;
        } else if (hasAttribute(AbstractConditionalLmlMacroTag.TEST_ATTRIBUTE)) {
            return getAttribute(AbstractConditionalLmlMacroTag.TEST_ATTRIBUTE);
        }
        return convertAttributesToEquation();
    }

    private Equation getEquationParser() {
        if (Strings.isNotBlank(equation)) {
            return new Equation(getParser(), getActor());
        }
        return null;
    }

    @Override
    protected boolean hasNext() {
        return equationParser == null ? false : equationParser.getBooleanResult(equation);
    }

    @Override
    protected int getIndex() {
        return currentIndex;
    }

    @Override
    protected void next(final ObjectMap<String, String> arguments) {
        // While macro does not replace any local arguments.
        currentIndex++;
    }

    @Override
    public String[] getExpectedAttributes() {
        return new String[] { AbstractConditionalLmlMacroTag.TEST_ATTRIBUTE };
    }
}
