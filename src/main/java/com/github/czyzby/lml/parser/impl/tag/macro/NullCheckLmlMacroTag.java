package com.github.czyzby.lml.parser.impl.tag.macro;

import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Checks if passed attributes are not null or boolean false. If any of the arguments is considered null, condition
 * evaluates to false. Be careful when checking multiple LML arguments, for example:
 *
 * <blockquote>
 *
 * <pre>
 * &lt;:notNull {arg0} {arg1}&gt;
 * </pre>
 *
 * </blockquote>If one of the arguments returns a non-null value and the other is mapped to an empty string, macro will
 * receive only 1 attribute and will evaluate to true (effectively becoming if-any-not-null rather than
 * if-all-not-null). If you have arguments that might be empty (but never null!) and want if-all-not-null behavior, use
 * nested null check. If you really want to do this in a single tag, this might be achieved through a simple "hack":
 *
 *
 * <blockquote>
 *
 * <pre>
 * &lt;:notNull {arg0}null {arg1}null&gt;
 * </pre>
 *
 * </blockquote>If any of the arguments is an empty string in this example, the macro will still receive fixed amount of
 * attributes to check: 2. If the argument is empty, "null" is processed and macro evaluates to false. This, on the
 * other hand, fails if the argument is null, as macro will receive "nullnull" and evaluate to true. Yeah, stick to
 * nested tags.
 *
 * <p>
 * Note that his macro can be also used with named parameters:<blockquote>
 *
 * <pre>
 * &lt;:notNull test="{arg0} {arg1}"&gt;
 * </pre>
 *
 * </blockquote>
 *
 * @author MJ */
public class NullCheckLmlMacroTag extends AbstractConditionalLmlMacroTag {
    public NullCheckLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected boolean checkCondition() {
        if (GdxArrays.isEmpty(getAttributes())) {
            // No arguments - this might happen even if the macro was properly invoked - with an argument that returns
            // empty string, for example. Assuming that no params = received null.
            return false;
        }
        if (hasAttribute(TEST_ATTRIBUTE)) {
            for (final String attribute : Strings.split(getAttribute(TEST_ATTRIBUTE), ' ')) {
                if (!testAttribute(attribute)) {
                    return false;
                }
            }
        } else {
            for (final String attribute : getAttributes()) {
                if (!testAttribute(attribute)) {
                    return false;
                }
            }
        }
        return true;
    }

    /** @param attribute will be tested.
     * @return true if is null. */
    private boolean testAttribute(final String attribute) {
        if (isAction(attribute)) {
            final Object result = invokeAction(attribute);
            if (isNullOrFalse(result)) {
                // Method result is empty or false.
                return false;
            }
        } else if (isNullOrFalse(attribute)) {
            // Attribute is blank, equals "null" or "false" - assuming the attribute is null.
            return false;
        }
        return true;
    }
}
