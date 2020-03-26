package com.github.czyzby.lml.parser.impl.tag.macro;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.kiwi.util.common.Nullables;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.LmlSyntax;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Allows to assign LML parser arguments from within LML templates. Arguments will be available after macro evaluation
 * with the given name, by default: {accessedLikeThis}. First macro attribute is the name of the argument to assign.
 * Other arguments will be joined to an array and a random value will be extracted. Alternatively, argument value can be
 * the data between macro tags. For example: <blockquote>
 *
 * <pre>
 * &lt;:random key Random;Value/&gt;
 * &lt;:random key Random  Value./&gt;
 * &lt;:random key&gt;Random;Value&lt;/:assign&gt;
 * </pre>
 *
 * </blockquote>All of these macro invocations would use assign either "Random" or "Value" to "key" argument.
 * <p>
 * Assignment macro supports optional named attributes:<blockquote>
 *
 * <pre>
 * &lt;:assign key="name" value="Random;Value"/&gt;
 * &lt;:assign key="name"&gt;Random;Value&lt;/:assign&gt;
 * </pre>
 *
 * </blockquote>
 *
 * @author MJ */
public class RandomLmlMacroTag extends AssignLmlMacroTag {
    public RandomLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected String processArgumentValue(final CharSequence argumentValue) {
        final String[] arguments = Strings.split(argumentValue, getParser().getSyntax().getArrayElementSeparator());
        if (arguments.length == 0) {
            return Strings.EMPTY_STRING;
        }
        String argument;
        if (arguments.length == 1) {
            argument = arguments[0];
        } else {
            argument = arguments[MathUtils.random(0, arguments.length - 1)];
        }
        return extractRandomValue(argument);
    }

    /** @param argument random array element.
     * @return random range value (if range) or random result value (if method) or the passed element (if
     *         unparseable). */
    protected String extractRandomValue(final String argument) {
        final LmlSyntax syntax = getParser().getSyntax();
        if (Strings.startsWith(argument, syntax.getMethodInvocationMarker())) {
            // Action:
            final ActorConsumer<?, Actor> action = getParser().parseAction(argument, getActor());
            if (action == null) {
                getParser().throwError("Unable to find action for ID: " + argument);
            }
            return extractRandomObject(action.consume(getActor()));
        } // Range or plain:
        final int openingIndex = argument.indexOf(syntax.getRangeArrayOpening());
        if (Strings.isCharacterAbsent(openingIndex)) {
            return argument; // Plain.
        }
        final int separatorIndex = argument.indexOf(syntax.getRangeArraySeparator());
        if (Strings.isCharacterAbsent(separatorIndex) || separatorIndex < openingIndex) {
            return argument; // Plain.
        }
        final int closingIndex = argument.indexOf(syntax.getRangeArrayClosing());
        if (Strings.isCharacterAbsent(closingIndex) || closingIndex < separatorIndex || closingIndex < openingIndex) {
            return argument; // Plain.
        }
        // Range:
        final String rangeBase = argument.substring(0, openingIndex);
        int rangeStart = getRangeValue(argument.substring(openingIndex + 1, separatorIndex));
        int rangeEnd = getRangeValue(argument.substring(separatorIndex + 1, closingIndex));
        if (rangeStart > rangeEnd) {
            final int temp = rangeStart;
            rangeStart = rangeEnd;
            rangeEnd = temp;
        }
        return rangeBase + MathUtils.random(rangeStart, rangeEnd);
    }

    /** @param rawData raw string data of range start or end. Expected to be a number (or a value that can be parsed
     *            into an int).
     * @return parsed value of the range. */
    protected int getRangeValue(final String rawData) {
        if (Strings.isInt(rawData)) {
            return Integer.parseInt(rawData);
        }
        return getParser().parseInt(rawData, getActor());
    }

    /** @param result method result.
     * @return random element (if a collection) or result to string. */
    protected String extractRandomObject(final Object result) {
        if (result instanceof Object[]) {
            final Object[] array = (Object[]) result;
            return Nullables.toString(array[MathUtils.random(0, array.length - 1)]);
        } else if (result instanceof Array<?>) {
            return Nullables.toString(((Array<?>) result).random());
        }
        return Nullables.toString(result);
    }

    @Override
    protected char getAttributeSeparator() {
        return getParser().getSyntax().getArrayElementSeparator();
    }
}
