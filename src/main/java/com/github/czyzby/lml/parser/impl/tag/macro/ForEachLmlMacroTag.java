package com.github.czyzby.lml.parser.impl.tag.macro;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.github.czyzby.kiwi.util.common.Nullables;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Allows to iterate over multiple arrays at once. Keeps track of its iteration index. Arrays have to follow LML array
 * syntax. If one of the arrays is longer than the others, null values will be eventually returned when its out of
 * indexes. For example: <blockquote>
 *
 * <pre>
 * &lt;:forEach element=elem0;elem1 range=rang[0,2]&gt;
 *      {forEach:index}: {element} {range}
 * &lt;/:forEach&gt;
 * </pre>
 *
 * </blockquote>The first argument is a standard array with values separated with ';'. The second is a range. This will
 * evaluate to:
 * <ul>
 * <li>0: elem0 rang0
 * <li>1: elem1 rang1
 * <li>2: null rang2
 * </ul>
 * Be careful when using nested loop tags: its arguments should not overlap. If you need to access indexes of both
 * loops, use different tag aliases.
 *
 * <p>
 * When using default DTD settings, "element" is the only recognized macro attribute. As this does not allow you to
 * create nested loops or iterate over multiple arrays at once, you might want to modify DTD files manually.
 *
 * @author MJ */
public class ForEachLmlMacroTag extends AbstractLoopLmlMacroTag {
    private final Array<String> argumentNames;
    private final Array<String[]> arrays;
    private final int size;
    private int currentIndex;

    public ForEachLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
        if (GdxArrays.isEmpty(getAttributes())) {
            parser.throwErrorIfStrict("For each macro needs array attributes to iterate over.");
            argumentNames = null;
            arrays = null;
            size = 0;
        } else {
            argumentNames = GdxArrays.newArray();
            arrays = GdxArrays.newArray();
            size = fillArrays();
        }
    }

    /** @return size of the biggest array that we iterate over. */
    private int fillArrays() {
        int biggestSize = 0;
        for (final Entry<String, String> attribute : getNamedAttributes()) {
            argumentNames.add(attribute.key);
            final String[] array = getParser().parseArray(attribute.value, getActor());
            arrays.add(array);
            if (array.length > biggestSize) {
                biggestSize = array.length;
            }
        }
        return biggestSize;
    }

    @Override
    protected boolean supportsNamedAttributes() {
        return true;
    }

    @Override
    protected boolean hasNext() {
        return currentIndex < size;
    }

    @Override
    protected int getIndex() {
        return currentIndex;
    }

    @Override
    protected void next(final ObjectMap<String, String> arguments) {
        for (int argumentId = 0, length = argumentNames.size; argumentId < length; argumentId++) {
            arguments.put(argumentNames.get(argumentId), getArgumentValue(argumentId));
        }
        currentIndex++;
    }

    /** @param argumentId ID of the argument array.
     * @return value stored in the selected array. */
    protected String getArgumentValue(final int argumentId) {
        final String[] array = arrays.get(argumentId);
        if (array.length > currentIndex) {
            return array[currentIndex];
        }
        return Nullables.DEFAULT_NULL_STRING;
    }

    @Override
    public String[] getExpectedAttributes() {
        return new String[] { ELEMENT_ATTRIBUTE };
    }
}
