package com.github.czyzby.lml.parser.impl.tag.macro;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** As opposed to a regular for-each macro, this iterates over passed arrays as a nested for loop rather than all at
 * once. This: <blockquote>
 *
 * <pre>
 * &lt;:nested value0=array0 value1=array1&gt;
 *      &lt;!-- do something, index: {nested:index}, values: {value0}, {value1} --&gt;
 * &lt;/:nested&gt;
 * </pre>
 *
 * </blockquote>...is "equivalent" to this Java syntax: <blockquote>
 *
 * <pre>
 * int index = 0;
 * for (Type0 value0 : array0) {
 *     for (Type1 value1 : array1) {
 *         // do something
 *         index++;
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * This macro should be preferred over two actually nested for-each macros when you need access to a global iteration
 * index. This is also arguably faster to parse. For example: <blockquote>
 *
 * <pre>
 * &lt;:nested who=he;she;it what=is;was;does&gt;
 *      Rule {nested:index}: {who} {what}.
 * &lt;/:nested&gt;
 * </pre>
 *
 * </blockquote>...prints:
 * <ul>
 * <li>Rule 0: he is.
 * <li>Rule 1: he was.
 * <li>Rule 2: he does.
 * <li>Rule 3: she is.
 * <li>Rule 4: she was.
 * <li>Rule 5: she does.
 * <li>Rule 6: it is.
 * <li>Rule 7: it was.
 * <li>Rule 8: it does.
 * </ul>
 * Total runs amount is equal to multiplied sizes of passed arrays.
 * <p>
 * When using default DTD settings, "element" is the only recognized macro attribute. As this does not allow you to
 * create nested loops or iterate over multiple arrays at once, you might want to modify DTD files manually.
 *
 * @author MJ */
public class NestedForEachLmlMacroTag extends AbstractLoopLmlMacroTag {
    private final IntArray indexes;
    private final Array<String> argumentNames;
    private final Array<String[]> values;
    private int currentIndex;

    public NestedForEachLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
        final int argumentsAmount = GdxArrays.sizeOf(getAttributes());
        if (argumentsAmount <= 0) {
            parser.throwErrorIfStrict("Nested for each macro needs array attributes to iterate over.");
            indexes = null;
            argumentNames = null;
            values = null;
        } else {
            indexes = new IntArray(argumentsAmount);
            argumentNames = GdxArrays.newArray(argumentsAmount);
            values = GdxArrays.newArray(argumentsAmount);
            fillArrays();
        }
    }

    private void fillArrays() {
        for (final Entry<String, String> attribute : getNamedAttributes()) {
            indexes.add(0);
            argumentNames.add(attribute.key);
            final String[] array = getParser().parseArray(attribute.value, getActor());
            values.add(array);
        }
    }

    @Override
    protected boolean supportsNamedAttributes() {
        return true;
    }

    @Override
    protected boolean hasNext() {
        if (GdxArrays.isEmpty(indexes)) {
            return false;
        }
        return indexes.first() < values.first().length;
    }

    @Override
    protected int getIndex() {
        return currentIndex;
    }

    @Override
    protected void next(final ObjectMap<String, String> arguments) {
        for (int arrayId = 0, length = indexes.size; arrayId < length; arrayId++) {
            arguments.put(argumentNames.get(arrayId), values.get(arrayId)[indexes.get(arrayId)]);
        }
        incrementIndex(indexes.size - 1);
        currentIndex++;
    }

    /** @param arrayId index of the array argument to increment. */
    private void incrementIndex(final int arrayId) {
        indexes.set(arrayId, indexes.get(arrayId) + 1);
        if (arrayId > 0 && indexes.get(arrayId) == values.get(arrayId).length) {
            indexes.set(arrayId, 0);
            incrementIndex(arrayId - 1);
        }
    }

    @Override
    public String[] getExpectedAttributes() {
        return new String[] { ELEMENT_ATTRIBUTE };
    }
}
