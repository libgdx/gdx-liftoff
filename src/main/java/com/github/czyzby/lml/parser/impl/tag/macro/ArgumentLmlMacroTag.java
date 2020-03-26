package com.github.czyzby.lml.parser.impl.tag.macro;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Allows to assign LML parser arguments from within LML templates. Contrary to {@link AssignLmlMacroTag}, this macro
 * evaluates the passed text. This can be used to evaluate and assign preferences or bundle lines. This macro expects at
 * least one attribute: argument name. Other arguments are joined and evaluated by
 * {@link LmlParser#parseString(String, Object)}. For example (macro aliases used):<blockquote>
 *
 * <pre>
 * &lt;:argument arg0 Value/&gt;
 * &lt;:nls arg1 {@literal @}bundle/&gt;
 * &lt;:preference arg2&gt;#preference&lt;/:preference&gt;
 * </pre>
 *
 * </blockquote>Assigned values:
 * <ul>
 * <li>{arg0}: {@code "Value"}
 * <li>{arg1}: {@code "Value extracted from .properties file mapped to 'bundle'."}
 * <li>{arg2}: {@code "Value extracted from Preferences object mapped to 'preference'."}
 * </ul>
 * <p>
 * This macro can be also used with named parameters:<blockquote>
 *
 * <pre>
 * &lt;:argument key="arg0" value="Value"/&gt;
 * </pre>
 *
 * </blockquote>
 *
 *
 * @author MJ */
public class ArgumentLmlMacroTag extends AssignLmlMacroTag {
    public ArgumentLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected String processArgumentValue(final CharSequence argumentValue) {
        return getParser().parseString(argumentValue.toString(), getActor());
    }
}
