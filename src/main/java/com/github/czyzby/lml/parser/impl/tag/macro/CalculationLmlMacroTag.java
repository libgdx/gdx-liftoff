package com.github.czyzby.lml.parser.impl.tag.macro;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.macro.util.Equation;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Calculates passed equation and assigns its result to an LML argument. The first macro attribute has to be tag's
 * name. The rest of attributes is merged into an equation - so spaces are allowed. Alternatively, equation can be
 * passed with data between macro tags. Supported operators:
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
 * For example:
 *
 * <blockquote>
 *
 * <pre>
 * &lt;:calculate meaningOfLife 40+2/&gt;
 * </pre>
 *
 * </blockquote>This will assign "42" to {meaningOfLife} attribute.
 *
 * <blockquote>
 *
 * <pre>
 * &lt;:calculate shouldContinue&gt;{loop:index}^2==4&lt;/:calculate&gt;
 * </pre>
 *
 * </blockquote>This will assign "true" boolean value to {shouldContinue} if squared "loop:index" argument value equals
 * 4.
 *
 * <blockquote>
 *
 * <pre>
 * &lt;:calculate concat should+be+joined+{index}/&gt;
 * </pre>
 *
 * </blockquote>Will merge the arguments, assigning "shouldbejoinedN" (where N is the current value of "index") to
 * {concat} argument.
 *
 * <blockquote>
 *
 * <pre>
 *&lt;table&gt;
 *  &lt;:loop 4&gt;
 *    &lt;:calculate makeRow {loop:index}%2==1/&gt;
 *    &lt;label row={makeRow}&gt;@bundleLine{loop:index}&lt;/label&gt;
 *  &lt;/:loop&gt;
 *&lt;/table&gt;
 * </pre>
 *
 * </blockquote>
 *
 * This makes 4 labels (with text extracted from i18n bundle lines: bundleLine0-bundleLine3). Since the calculation
 * result is a boolean, it can be easily used as an actor attribute. In this example, when loop index is not even (1,
 * 3), it adds a table row thanks to "row=true" attribute. {makeRow} will take these values: false (0 iteration), true
 * (1), false (2), true (3).
 *
 * <p>
 * {@link com.github.czyzby.lml.parser.LmlSyntax#getEquationMarker() Equation marker} is a simplified alternative to
 * this macro.
 *
 * <p>
 * This macro can also be used with named attributes: <blockquote>
 *
 * <pre>
 * &lt;:calculate key="meaningOfLife" value="40+2"/&gt;
 * </pre>
 *
 * </blockquote>
 *
 * @author MJ */
public class CalculationLmlMacroTag extends AssignLmlMacroTag {
    public CalculationLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected String processArgumentValue(final CharSequence argumentValue) {
        return new Equation(getParser(), getActor())
                .getResult(replaceArguments(argumentValue, getParser().getData().getArguments()));
    }
}
