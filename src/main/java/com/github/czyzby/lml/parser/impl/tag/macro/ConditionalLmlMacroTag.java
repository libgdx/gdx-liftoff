package com.github.czyzby.lml.parser.impl.tag.macro;

import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.macro.util.Equation;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Constructs an equation from passed attribute, evaluates it and converts its result to a boolean. Spaces in
 * attributes are supported and stripped. Equations can work on booleans, int, floats and Strings (types determined at
 * runtime). Numbers are converted to true if greater than 0; Strings return true if they are not blank, "false" or
 * "null". If no macro attributes are passed, condition returns false. Supported operators:
 *
 * <ul>
 * <li>+: adds ints and floats; concats Strings.
 * <li>-: subtracts ints and floats; uses {@link String#replace(CharSequence, CharSequence)} (replacing with empty
 * string) on first string, removing occurrences of the second.
 * <li>=: compares booleans, ints and floats; uses {@link String#equalsIgnoreCase(String)} on strings.
 * <li>==: works like =, except uses regular equals method on strings.
 * <li>!=, !==: negated equality operators.
 * <li>&lt;, &lt;=, &gt;, &gt;=: compares ints and floats; compares strings's lengths. If one of the strings is an int,
 * compares numeric int value to the other's length. For example, "string&gt;t" returns true, as "string" is longer than
 * "t", but "string&gt;9" returns false, because "string" has less characters than 9. Since &gt; is also the char that
 * closes the tag it cannot be used directly - alias for &gt; is {@literal &gt;}. &lt; does not have to be escaped, so
 * there's no alias.
 * <li>*, /: multiples and divides ints and floats.
 * <li>^: {@link Math#pow(double, double)} on numbers; XOR on booleans.
 * <li>&amp;, &amp;&amp;, |, ||: logical "and" and "or" on booleans. Bit "and" and "or" on ints.
 * <li>%: modulo on ints and floats.
 * <li>!: negates booleans, ints and floats. Clears strings to empty string. Negates operators (!= becomes "not equals",
 * !&lt; becomes &gt;, etc).
 * <li>~: negates booleans. Bit negation on ints.
 * <li>--, ++: increments or decrements numbers. Has to appear BEFORE the value to modify: will have no effect or throw
 * exception otherwise. Has similar effect to +1 or -1, but is evaluated before most other operators; for example, --3^2
 * returns 4, while 3-1^2 returns 2. If you want to simulate -- or ++, use parenthesis: (3-1)^2 returns 4.
 * </ul>
 *
 * Example usage:
 *
 * <blockquote>
 *
 * <pre>
 * &lt;:if {loop:index}%4=0&gt;
 *      &lt;label row=true/&gt;
 * &lt;/:if&gt;
 * </pre>
 *
 * </blockquote> This macro appends label tag only if "loop:index" argument modulo 4 equals 0.
 *
 * <blockquote>
 *
 * <pre>
 * &lt;:if 100&lt;{@literal @}bundleLine&gt;
 *      &lt;textButton text=@bundleLine width=256 expandX=true/&gt;
 * &lt;:if:else/&gt;
 *      &lt;textButton text=@bundleLine width=128/&gt;
 * &lt;/:if&gt;
 * </pre>
 *
 * </blockquote>This macro finds i18n bundle line mapped to "bundleLine" and checks if its longer than 100 chars. If it
 * is, it creates a bigger, expanding button. Otherwise, creates a smaller button.
 *
 * <blockquote>
 *
 * <pre>
 * &lt;:if ($action = (--{for:index})) || ({marker} = continue)&gt;
 * </pre>
 *
 * </blockquote>This condition evaluates to true if result of method mapped to "action" equals decremented "for:index"
 * argument value or if the "marker" argument equals ignore case "continue" string. As you can see, parenthesis support
 * is available.
 *
 * <p>
 * This macro can also be used with named attributes: <blockquote>
 *
 * <pre>
 * &lt;:if test="{loop:index}%4=0"&gt;
 *      &lt;label row="true"/&gt;
 * &lt;/:if&gt;
 * </pre>
 *
 * </blockquote>
 *
 * @author MJ */
public class ConditionalLmlMacroTag extends AbstractConditionalLmlMacroTag {
    public ConditionalLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected boolean checkCondition() {
        if (GdxArrays.isEmpty(getAttributes())) {
            return false;
        }
        final String conditionContent = hasAttribute(TEST_ATTRIBUTE) ? getAttribute(TEST_ATTRIBUTE)
                : convertAttributesToEquation();
        return new Equation(getParser(), getActor()).getBooleanResult(conditionContent);
    }
}
