package com.github.czyzby.lml.parser.impl.tag.macro;

import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractMacroLmlTag;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** This macro allows to evaluate arguments with custom values. For example, if you map a LML parser argument with
 * assign macro, if will be available in all templates, but thanks to the fact that macros parse their own arguments
 * (think in terms of variable overshadowing), they can be locally replaced with this macro.
 *
 * <blockquote>
 *
 * <pre>
 * &lt;:assign argumentName argumentValue&gt;
 * &lt;:replace argumentName=localValue&gt;
 *      {argumentName} &lt;!-- == localValue --&gt;
 * &lt;/:replace&gt;
 * {argumentName} &lt;!-- == argumentValue --&gt;
 * </pre>
 *
 * </blockquote>The first macro assigns "argumentValue" to "argumentName" key. Normally, outside "replace" macro, this
 * argument would evaluate to "argumentValue" (as expected), but since we set it as attribute with
 * "argumentName=localValue", it locally changes to "localValue".
 *
 * <p>
 * Tip: using replacement macro with no attributes is effectively a no-op. It can be used as a root if you want to
 * preserve XML structure, but parse multiple root actor tags in the template:
 *
 * <blockquote>
 *
 * <pre>
 * &lt;!-- Invalid XML: --&gt;
 * &lt;actor&gt;
 *      &lt;actor/&gt;
 * &lt;/actor&gt;
 * &lt;actor/&gt;
 *
 * &lt;!-- Valid XML with essentially the same functionality: --&gt;
 * &lt;:root&gt;
 *      &lt;actor&gt;
 *          &lt;actor/&gt;
 *      &lt;/actor&gt;
 *      &lt;actor/&gt;
 * &lt;/:root&gt;
 * </pre>
 *
 * </blockquote>
 *
 * @author MJ */
public class ArgumentReplacementLmlMacroTag extends AbstractMacroLmlTag {
    public ArgumentReplacementLmlMacroTag(final LmlParser parser, final LmlTag parentTag,
            final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    public void handleDataBetweenTags(final CharSequence rawData) {
        if (GdxMaps.isNotEmpty(getNamedAttributes())) {
            appendTextToParse(replaceArguments(rawData, getNamedAttributes()));
        } else {
            appendTextToParse(rawData.toString());
        }
    }

    @Override
    protected boolean supportsNamedAttributes() {
        return true;
    }
}
