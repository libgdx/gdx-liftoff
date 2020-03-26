package com.github.czyzby.lml.parser.impl.tag.macro;

import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractMacroLmlTag;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.collection.IgnoreCaseStringMap;

/** Abstract base for loop and iteration-based macros.
 *
 * @author MJ */
public abstract class AbstractLoopLmlMacroTag extends AbstractMacroLmlTag {
    /** Optional name of iteration argument. Note that when always using this name, nested loops (or multi-argument
     * loops) are impossible to create. Used for iteration macros. */
    public static final String ELEMENT_ATTRIBUTE = "element";
    /** Optional name of the loop times argument. Used for loop macros. */
    public static final String TIMES_ATTRIBUTE = "times";

    private final String indexArgument;

    public AbstractLoopLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
        indexArgument = getTagName() + ":index";
    }

    @Override
    public void handleDataBetweenTags(final CharSequence rawMacroContent) {
        final StringBuilder contentBuilder = new StringBuilder(rawMacroContent.length());
        final ObjectMap<String, String> arguments = new IgnoreCaseStringMap<String>();
        while (hasNext()) {
            arguments.put(indexArgument, String.valueOf(getIndex()));
            next(arguments);
            contentBuilder.append(replaceArguments(rawMacroContent, arguments));
        }
        if (Strings.isNotEmpty(contentBuilder)) {
            appendTextToParse(contentBuilder);
        }
    }

    /** @return true if macro should continue to be evaluated. */
    protected abstract boolean hasNext();

    /** @return current iteration index. Should generally return different value after each time
     *         {@link #next(ObjectMap)} is called. The method is called BEFORE {@link #next(ObjectMap)} during iteration
     *         to set current index argument in the loop, so next method can safely increment this value. */
    protected abstract int getIndex();

    /** Advances loop iteration.
     *
     * @param arguments might have to be adjusted with each iteration step. */
    protected abstract void next(ObjectMap<String, String> arguments);
}
