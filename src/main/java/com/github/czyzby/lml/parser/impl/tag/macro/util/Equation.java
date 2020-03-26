package com.github.czyzby.lml.parser.impl.tag.macro.util;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.IntMap;
import com.github.czyzby.kiwi.util.common.Nullables;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.util.LmlUtilities;

/** Allows to evaluate string equations at runtime. Supports String, float, int and boolean types, determined upon
 * parsing. See {@link DefaultOperator} for supported operations.
 *
 * @author MJ */
// I think that this is currently by far the most dragon-ish class in LML, as the logic is pretty complex and I decided
// to keep most variables method-scoped for easy nested equations. Also, I didn't really want the internal classes to
// flood even further than it already is with LML attributes, tags and macros, since this functionality will probably be
// rarely extended. So, basically: here be dragons. Kind of.
public class Equation {
    private static final IntMap<Operator> OPERATORS = new IntMap<Operator>();
    private final LmlParser parser;
    private final Actor actor;

    static {
        for (final Operator operator : DefaultOperator.values()) {
            registerOperator(operator);
        }
    }

    /** Constructs a new equation without LML syntax support. */
    public Equation() {
        this(null, null);
    }

    /** @param operator will be registered and handled during equation parsing. Be careful not to override default
     *            operators. */
    public static void registerOperator(final Operator operator) {
        OPERATORS.put(operator.getSign(), operator);
    }

    /** @param parser will be used to parse values.
     * @param actor will be used to invoke parsed actions, if any found. */
    public Equation(final LmlParser parser, final Actor actor) {
        this.parser = parser;
        this.actor = actor;
    }

    /** @param value plain text value.
     * @return if parser exists, it will return pre-processed value. */
    protected String parseValue(final String value) {
        if (parser != null) {
            return parser.parseString(value, actor);
        }
        return value;
    }

    /** @param exception was thrown during evaluation. If parser exists, it will be used to process this exception. */
    protected void throwException(final RuntimeException exception) {
        if (parser != null) {
            parser.throwError("Unable to evaluate equation.", exception);
        }
        throw exception;
    }

    /** @param equation will be evaluated.
     * @return result of the equation as string. */
    public String getResult(final CharSequence equation) {
        try {
            return Nullables.toString(parseEquation(equation));
        } catch (final RuntimeException exception) {
            throwException(exception);
        }
        return null;
    }

    private Object parseEquation(final CharSequence equation) {
        Element firstNode = new OperatorElement(DefaultOperator.NO_OP, null, null);
        // Separating equation into values (boolean, int, float, String) and operators:
        findEquationElements(equation, firstNode);
        // Merging neighbor operators (for example, != will be parsed as one operator: NOT_EQUALS):
        firstNode = mergeOperatorElements(firstNode);
        // Executing operators:
        firstNode = evaluateOperators(firstNode, findMaxPriority(firstNode));
        return convertToResult(firstNode);
    }

    protected void findEquationElements(final CharSequence equation, final Element firstNode) {
        final StringBuilder valueBuilder = new StringBuilder();
        Element lastNode = firstNode;
        for (int index = 0, length = equation.length(); index < length; index++) {
            char character = equation.charAt(index);
            Element node;
            if (character == ')') {
                // Assuming this was properly parsed by a nested equation.
                continue;
            } else if (character == '(') {
                Strings.clearBuilder(valueBuilder);
                int nested = 1;
                // This is not extracted to another method, as A) we want to keep variables method-scoped, B) it needs
                // both index and parsed value after the loop - wrapping it in an array/collection/pair is an overkill.
                for (++index; index < length; index++) {
                    character = equation.charAt(index);
                    if (character == '(') {
                        nested++;
                    } else if (character == ')' && --nested == 0) {
                        break;
                    }
                    valueBuilder.append(character);
                }
                if (nested != 0) {
                    throw new IllegalStateException("Invalid amount of parenthesis in equation: " + equation);
                }
                node = new ValueElement(parseEquation(valueBuilder.toString()), lastNode, null);
            } else if (isOperator(character)) {
                node = new OperatorElement(getOperator(character), lastNode, null);
            } else {
                Strings.clearBuilder(valueBuilder);
                // This is not extracted to another method, as A) we want to keep variables method-scoped, B) it needs
                // both index and parsed value after the loop - wrapping it in an array/collection/pair is an overkill.
                for (; index < length; index++) {
                    character = equation.charAt(index);
                    if (isOperator(character) || character == '(') {
                        index--; // This is not ours to parse.
                        break;
                    } else if (character == ')') {
                        // Assuming parenthesis was properly parsed by nested equation.
                        break;
                    }
                    valueBuilder.append(character);
                }
                if (Strings.isBlank(valueBuilder)) {
                    continue;
                }
                node = new ValueElement(buildValue(valueBuilder), lastNode, null);
            }
            lastNode.setNext(node);
            lastNode = node;
        }
    }

    private String buildValue(final StringBuilder valueBuilder) {
        String value = valueBuilder.toString().trim().replace("\\n", "\n");
        if (isInQuotation(value)) {
            value = LmlUtilities.stripQuotation(value);
        }
        if (parser != null) {
            return parser.parseString(value, actor);
        }
        return value;
    }

    protected boolean isInQuotation(final String value) {
        return Strings.startsWith(value, '"') && Strings.endsWith(value, '"')
                || Strings.startsWith(value, '\'') && Strings.endsWith(value, '\'');
    }

    protected String printNodes(Element firstNode) {
        final StringBuilder debug = new StringBuilder();
        debug.append(firstNode);
        while (firstNode.hasNext()) {
            debug.append(", ");
            firstNode = firstNode.next();
            debug.append(firstNode);
        }
        return debug.toString();
    }

    protected Element mergeOperatorElements(Element firstNode) {
        for (Element element = firstNode; element != null;) {
            if (element.isOperator() && element.hasNext()) {
                final Element next = element.next();
                if (next != null && next.isOperator()) {
                    final Operator mergedOperator = element.getOperator().merge(next.getOperator());
                    if (mergedOperator != null) {
                        final Element mergedElement = new OperatorElement(mergedOperator, element.previous(),
                                next.next());
                        if (element == firstNode) {
                            firstNode = mergedElement;
                        }
                        element = mergedElement;
                        // Correct next/previous for old elements are set in abstract elements.
                        continue;
                    }
                }
            }
            element = element.next();
        }
        return firstNode;
    }

    protected int findMaxPriority(final Element firstNode) {
        int priority = -1;
        for (Element element = firstNode; element != null;) {
            if (element.isOperator()) {
                priority = Math.max(priority, Math.max(element.getOperator().getDoubleArgumentPriority(),
                        element.getOperator().getSingleArgumentPriority()));
            }
            element = element.next();
        }
        return priority;
    }

    private Element evaluateOperators(Element firstNode, final int maxPriority) {
        for (int currentPriority = maxPriority; currentPriority <= maxPriority
                && currentPriority >= 0; currentPriority--) {
            for (Element element = firstNode; element != null;) {
                if (!element.isOperator()) {
                    element = element.next();
                    continue;
                }
                Element resultElement = null;
                if (element.getOperator().getDoubleArgumentPriority() == currentPriority) {
                    // Evaluating double argument expression.
                    validateEvaluatedElement(element);
                    if (isSingleArgument(element)) {
                        // Has only two arguments...
                        if (element.getOperator().getSingleArgumentPriority() < 0) {
                            // ...if it supports single arguments, its OK, but if it doesn't, we have to exit this.
                            throw new IllegalStateException(
                                    element + " is expected to receive two logical arguments, but it is proceeded by: "
                                            + element.previous() + " and followed by " + element.next());
                        }
                    } else {
                        final Object result = evaluateDoubleArgumentOperator(element);
                        resultElement = new ValueElement(result, element.previous().previous(), element.next().next());
                    }
                }
                if (resultElement == null && element.getOperator().getSingleArgumentPriority() == currentPriority) {
                    // Evaluating a single argument expression.
                    validateEvaluatedElement(element);
                    if (element.getOperator().getDoubleArgumentPriority() >= 0 && !isSingleArgument(element)) {
                        // It is a single argument-handling operator, but it also parses double arguments and now its
                        // got two of them. For example:
                        // - operator would normally negate number, but it it has two arguments, it subtracts them
                        // instead.
                        // ! operator negates value and never enters this if.
                        element = element.next();
                        continue;
                    }
                    final Object result = evaluateSingleArgumentOperator(element);
                    resultElement = new ValueElement(result, element.previous(), element.next().next());
                } else {
                    resultElement = resultElement == null ? element.next() : resultElement;
                }
                if (resultElement != null && !resultElement.hasPrevious()) {
                    firstNode = resultElement;
                }
                element = resultElement;
            }
        }
        return firstNode;
    }

    protected boolean isSingleArgument(final Element element) {
        return !element.hasPrevious() || element.hasPrevious() && element.previous().isOperator();
    }

    protected void validateEvaluatedElement(final Element element) {
        if (!element.hasNext()) {
            throw new IllegalStateException(
                    "Operator cannot end an expression - it needs a logical value to work on. Found " + element
                            + " without an argument.");
        }
        if (element.next().isOperator()) {
            throw new IllegalStateException("Invalid operator usage. " + element + " cannot be before " + element.next()
                    + ". Could not merge operators.");
        }
    }

    protected Object evaluateDoubleArgumentOperator(final Element element) {
        final Operator operator = element.getOperator();
        final Element leftArgument = element.previous();
        final Element rightArgument = element.next();
        if (leftArgument.isBoolean() && rightArgument.isBoolean()) {
            return operator.process(leftArgument.getBoolean(), rightArgument.getBoolean());
        } else if (leftArgument.isInt() && rightArgument.isInt()) {
            return operator.process(leftArgument.getInt(), rightArgument.getInt());
        } else if (leftArgument.isFloat() && rightArgument.isFloat()) {
            return operator.process(leftArgument.getFloat(), rightArgument.getFloat());
        }
        return operator.process(leftArgument.getString(), rightArgument.getString());
    }

    protected Object evaluateSingleArgumentOperator(final Element element) {
        final Operator operator = element.getOperator();
        final Element argument = element.next();
        if (argument.isBoolean()) {
            return operator.process(argument.getBoolean());
        } else if (argument.isInt()) {
            return operator.process(argument.getInt());
        } else if (argument.isFloat()) {
            return operator.process(argument.getFloat());
        }
        return operator.process(argument.getString());
    }

    protected Object convertToResult(final Element firstNode) {
        if (firstNode.isOperator()) {
            throw new IllegalStateException(
                    "No logical values in the equation. Equation cannot be empty or contain only operators.");
        }
        if (firstNode.hasNext()) {
            throw new IllegalStateException(
                    "Equation could not have been evaluated to a single value. Most likely not enough operators were given. Leftover nodes: "
                            + printNodes(firstNode));
        }
        return firstNode.getString();
    }

    protected Operator getOperator(final char character) {
        return OPERATORS.get(character);
    }

    protected boolean isOperator(final char character) {
        return OPERATORS.containsKey(character);
    }

    /** Utility method that calls {@link #getResult(CharSequence)} and converts returned value to a boolean.
     *
     * @param equation will be evaluated.
     * @return true if: returned value is boolean true, a positive number or non-null string. */
    public boolean getBooleanResult(final CharSequence equation) {
        final Element result = new ValueElement(getResult(equation), null, null);
        if (result.isBoolean()) {
            return result.getBoolean();
        } else if (result.isInt()) {
            return result.getInt() > 0;
        } else if (result.isFloat()) {
            return result.getFloat() > 0f;
        }
        return !isNullOrFalse(result.getString());
    }

    /** @param value LML value.
     * @return true if value is mapped to null or boolean false. */
    protected boolean isNullOrFalse(final String value) {
        return value == null || Strings.isWhitespace(value) || Nullables.DEFAULT_NULL_STRING.equalsIgnoreCase(value)
                || Boolean.FALSE.toString().equalsIgnoreCase(value);
    }

    /** Common interface for parsed operators.
     *
     * @author MJ */
    public static interface Operator {
        /** @return sign of the operator as it should appear in the equation. */
        char getSign();

        /** @return non-negative operator priority value. By default, in range of [0,5]. -1 if not supported. 0 priority
         *         is reserved for finalizing operators (and, or), 1 is for comparing operators (lower than, greater
         *         than, equal, etc), 2 for low priority operations (modulo), 3 for moderate priority (add, subtract), 4
         *         for high priority (multiply, divide, pow), 5 is usually only for single arguments (negate, increment,
         *         decrement). */
        int getDoubleArgumentPriority();

        /** @return -1 if not supports single argument. Usually 5 otherwise. */
        int getSingleArgumentPriority();

        /** @param operator is right after this operator.
         * @return merged operator or null if cannot merge. */
        Operator merge(Operator operator);

        /** @return negated operator. */
        Operator negate();

        /** @param leftValue value before the operator.
         * @param rightValue value after the operator.
         * @return result of the operation. */
        Object process(String leftValue, String rightValue);

        /** @param value proceeded by the operator.
         * @return result of the single-argument operation. */
        Object process(String value);

        /** @param leftValue value before the operator.
         * @param rightValue value after the operator.
         * @return result of the operation. */
        Object process(float leftValue, float rightValue);

        /** @param value proceeded by the operator.
         * @return result of the single-argument operation. */
        Object process(float value);

        /** @param leftValue value before the operator.
         * @param rightValue value after the operator.
         * @return result of the operation. */
        Object process(int leftValue, int rightValue);

        /** @param value proceeded by the operator.
         * @return result of the single-argument operation. */
        Object process(int value);

        /** @param leftValue value before the operator.
         * @param rightValue value after the operator.
         * @return result of the operation. */
        Object process(boolean leftValue, boolean rightValue);

        /** @param value proceeded by the operator.
         * @return result of the single-argument operation. */
        Object process(boolean value);
    }

    /** Common base interface for elements of equation.
     *
     * @author MJ */
    protected static interface Element {
        /** @return previous element in the equation. Might be null. */
        Element previous();

        /** @return true if previous element is not null. */
        boolean hasPrevious();

        /** @param previous becomes previous element in the equation. */
        void setPrevious(Element previous);

        /** @return next element in the equation. Might be null. */
        Element next();

        /** @return true if next element is not null. */
        boolean hasNext();

        /** @param next becomes next element in the equation. */
        void setNext(Element next);

        /** @return true if element holds an operator. */
        boolean isOperator();

        /** @return wrapped operator, if the element is an operator. */
        Operator getOperator();

        /** @return true if element holds a boolean. */
        boolean isBoolean();

        /** @return true if element holds an int. */
        boolean isInt();

        /** @return true if element holds a float. */
        boolean isFloat();

        /** @return stored value as boolean. */
        boolean getBoolean();

        /** @return stored value as int. */
        int getInt();

        /** @return stored value as float. */
        float getFloat();

        /** @return stored value as String. */
        String getString();
    }

    /** Abstract base for elements. Throws exceptions for any getters except next/previous element.
     *
     * @author MJ */
    protected static abstract class AbstractElement implements Element {
        private Element previous, next;

        public AbstractElement(final Element previous, final Element next) {
            this.previous = previous;
            if (previous != null) {
                previous.setNext(this);
            }
            this.next = next;
            if (next != null) {
                next.setPrevious(this);
            }
        }

        @Override
        public Element previous() {
            return previous;
        }

        @Override
        public void setPrevious(final Element previous) {
            this.previous = previous;
        }

        @Override
        public boolean hasPrevious() {
            return previous != null;
        }

        @Override
        public Element next() {
            return next;
        }

        @Override
        public void setNext(final Element next) {
            this.next = next;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public boolean isOperator() {
            return false;
        }

        @Override
        public Operator getOperator() {
            throw new IllegalStateException("Not an operator.");
        }

        @Override
        public boolean isBoolean() {
            return false;
        }

        @Override
        public boolean isInt() {
            return false;
        }

        @Override
        public boolean isFloat() {
            return false;
        }

        @Override
        public boolean getBoolean() {
            return false;
        }

        @Override
        public int getInt() {
            throw new IllegalStateException("Not a value.");
        }

        @Override
        public float getFloat() {
            throw new IllegalStateException("Not a value.");
        }

        @Override
        public String getString() {
            throw new IllegalStateException("Not a value.");
        }

        @Override
        public String toString() {
            return isOperator() ? getOperator().toString() : getString();
        }
    }

    /** Holds operator elements.
     *
     * @author MJ */
    protected static class OperatorElement extends AbstractElement {
        private final Operator operator;

        public OperatorElement(final Operator operator, final Element previous, final Element next) {
            super(previous, next);
            this.operator = operator;
        }

        @Override
        public boolean isOperator() {
            return true;
        }

        @Override
        public Operator getOperator() {
            return operator;
        }
    }

    /** Holds actual equation values.
     *
     * @author MJ */
    protected static class ValueElement extends AbstractElement {
        private final String value;

        public ValueElement(final Object value, final Element previous, final Element next) {
            this(Nullables.toString(value, Strings.EMPTY_STRING), previous, next);
        }

        public ValueElement(final String value, final Element previous, final Element next) {
            super(previous, next);
            if (value == null || value.equalsIgnoreCase(Nullables.DEFAULT_NULL_STRING)) {
                this.value = Strings.EMPTY_STRING;
            } else {
                this.value = value;
            }
        }

        @Override
        public boolean isBoolean() {
            return Strings.isBoolean(value);
        }

        @Override
        public boolean isInt() {
            return Strings.isInt(value);
        }

        @Override
        public boolean isFloat() {
            return Strings.isFloat(value);
        }

        @Override
        public boolean getBoolean() {
            return Boolean.valueOf(value);
        }

        @Override
        public float getFloat() {
            return Float.valueOf(value);
        }

        @Override
        public int getInt() {
            return Integer.valueOf(value);
        }

        @Override
        public String getString() {
            return value;
        }
    }

    /** Contains operators supported by default.
     *
     * @author MJ */
    protected enum DefaultOperator implements Operator {
        /** <table summary="">
         * <tr>
         * <th>Type</th>
         * <th>Effect</th>
         * </tr>
         * <tr>
         * <th>boolean</th>
         * <td>!value</td>
         * </tr>
         * <tr>
         * <th>int</th>
         * <td>-value</td>
         * </tr>
         * <tr>
         * <th>float</th>
         * <td>-value</td>
         * </tr>
         * <tr>
         * <th>String</th>
         * <td>Return empty string.</td>
         * </tr>
         * <tr>
         * <th>Operator</th>
         * <td>Returns negated operator.</td>
         * </tr>
         * </table>
        */
        NEGATE('!') {
            @Override
            public int getSingleArgumentPriority() {
                return 5;
            }

            @Override
            public Object process(final boolean value) {
                return !value;
            }

            @Override
            public Object process(final int value) {
                return -value;
            }

            @Override
            public Object process(final float value) {
                return -value;
            }

            @Override
            public String process(final String value) {
                return Strings.EMPTY_STRING;
            }

            @Override
            public Operator merge(final Operator operator) {
                return operator.negate();
            }

            @Override
            public Operator negate() {
                return DefaultOperator.NO_OP;
            }
        },
        /** Utility operator that can process 1 value at a time (cannot process 2 arguments) or an operator. Returns
         * unchanged passed value while processing and passed operator while merging. This is basically negated
         * negation. */
        NO_OP('\uFFEE') {
            @Override
            public int getSingleArgumentPriority() {
                return 5;
            }

            @Override
            public Object process(final boolean value) {
                return value;
            }

            @Override
            public Object process(final int value) {
                return value;
            }

            @Override
            public Object process(final float value) {
                return value;
            }

            @Override
            public String process(final String value) {
                return value;
            }

            @Override
            public Operator merge(final Operator operator) {
                return operator;
            }

            @Override
            public Operator negate() {
                return DefaultOperator.NEGATE;
            }
        },
        /** <table summary="">
         * <tr>
         * <th>Type</th>
         * <th>Effect</th>
         * </tr>
         * <tr>
         * <th>boolean</th>
         * <td>!value</td>
         * </tr>
         * <tr>
         * <th>int</th>
         * <td>~value</td>
         * </tr>
         * <tr>
         * <th>float</th>
         * <td>Not supported.</td>
         * </tr>
         * <tr>
         * <th>String</th>
         * <td>Not supported.</td>
         * </tr>
         * <tr>
         * <th>Operator</th>
         * <td>Returns negated operator.</td>
         * </tr>
         * </table>
        */
        BIT_NEGATE('~') {
            @Override
            public int getSingleArgumentPriority() {
                return 5;
            }

            @Override
            public Object process(final boolean value) {
                return !value;
            }

            @Override
            public Object process(final int value) {
                return ~value;
            }

            @Override
            public Operator merge(final Operator operator) {
                return operator.negate();
            }

            @Override
            public Operator negate() {
                return DefaultOperator.NO_OP;
            }
        },
        /** <table summary="">
         * <tr>
         * <th>Type</th>
         * <th>Effect</th>
         * </tr>
         * <tr>
         * <th>boolean</th>
         * <td>first == second</td>
         * </tr>
         * <tr>
         * <th>int</th>
         * <td>first == second</td>
         * </tr>
         * <tr>
         * <th>float</th>
         * <td>{@link Float#compare(float, float)} == 0</td>
         * </tr>
         * <tr>
         * <th>String</th>
         * <td>{@link String#equalsIgnoreCase(String)}</td>
         * </tr>
         * </table>
        */
        EQUALS('=') {
            @Override
            public int getDoubleArgumentPriority() {
                return 1;
            }

            @Override
            public Object process(final boolean leftValue, final boolean rightValue) {
                return leftValue == rightValue;
            }

            @Override
            public Object process(final int leftValue, final int rightValue) {
                return leftValue == rightValue;
            }

            @Override
            public Object process(final float leftValue, final float rightValue) {
                return Float.compare(leftValue, rightValue) == 0;
            }

            @Override
            public Object process(final String leftValue, final String rightValue) {
                return leftValue.equalsIgnoreCase(rightValue);
            }

            @Override
            public Operator merge(final Operator operator) {
                if (operator == EQUALS) {
                    return EQUALS_STRICT;
                } else if (operator == LOWER_THAN) {
                    return LOWER_OR_EQUALS;
                } else if (operator == GREATER_THAN) {
                    return GREATER_OR_EQUALS;
                }
                return super.merge(operator);
            }

            @Override
            public Operator negate() {
                return NOT_EQUALS;
            }
        },
        /** <table summary="">
         * <tr>
         * <th>Type</th>
         * <th>Effect</th>
         * </tr>
         * <tr>
         * <th>boolean</th>
         * <td>first != second</td>
         * </tr>
         * <tr>
         * <th>int</th>
         * <td>first != second</td>
         * </tr>
         * <tr>
         * <th>float</th>
         * <td>{@link Float#compare(float, float)} != 0</td>
         * </tr>
         * <tr>
         * <th>String</th>
         * <td>!{@link String#equalsIgnoreCase(String)}</td>
         * </tr>
         * </table>
        */
        NOT_EQUALS('\uFFEF') { // != is converted to this.
            @Override
            public int getDoubleArgumentPriority() {
                return 1;
            }

            @Override
            public Object process(final boolean leftValue, final boolean rightValue) {
                return leftValue != rightValue;
            }

            @Override
            public Object process(final int leftValue, final int rightValue) {
                return leftValue != rightValue;
            }

            @Override
            public Object process(final float leftValue, final float rightValue) {
                return Float.compare(leftValue, rightValue) != 0;
            }

            @Override
            public Object process(final String leftValue, final String rightValue) {
                return !leftValue.equalsIgnoreCase(rightValue);
            }

            @Override
            public Operator merge(final Operator operator) {
                if (operator == EQUALS) {
                    return NOT_EQUALS_STRICT;
                } else if (operator == LOWER_THAN) {
                    return GREATER_OR_EQUALS;
                } else if (operator == GREATER_THAN) {
                    return LOWER_OR_EQUALS;
                }
                return super.merge(operator);
            }

            @Override
            protected String complexName() {
                return "!=";
            }

            @Override
            public Operator negate() {
                return EQUALS;
            }
        },
        /** <table summary="">
         * <tr>
         * <th>Type</th>
         * <th>Effect</th>
         * </tr>
         * <tr>
         * <th>boolean</th>
         * <td>first != second</td>
         * </tr>
         * <tr>
         * <th>int</th>
         * <td>first != second</td>
         * </tr>
         * <tr>
         * <th>float</th>
         * <td>{@link Float#compare(float, float)} != 0</td>
         * </tr>
         * <tr>
         * <th>String</th>
         * <td>!{@link Strings#equals(CharSequence, CharSequence)}</td>
         * </tr>
         * </table>
        */
        NOT_EQUALS_STRICT('\uFFFA') { // !== is converted to this.
            @Override
            public int getDoubleArgumentPriority() {
                return 1;
            }

            @Override
            public Object process(final boolean leftValue, final boolean rightValue) {
                return leftValue != rightValue;
            }

            @Override
            public Object process(final int leftValue, final int rightValue) {
                return leftValue != rightValue;
            }

            @Override
            public Object process(final float leftValue, final float rightValue) {
                return Float.compare(leftValue, rightValue) != 0;
            }

            @Override
            public Object process(final String leftValue, final String rightValue) {
                return !Strings.equals(leftValue, rightValue);
            }

            @Override
            protected String complexName() {
                return "!==";
            }

            @Override
            public Operator negate() {
                return EQUALS_STRICT;
            }
        },
        /** <table summary="">
         * <tr>
         * <th>Type</th>
         * <th>Effect</th>
         * </tr>
         * <tr>
         * <th>boolean</th>
         * <td>first == second</td>
         * </tr>
         * <tr>
         * <th>int</th>
         * <td>first == second</td>
         * </tr>
         * <tr>
         * <th>float</th>
         * <td>{@link Float#compare(float, float)} == 0</td>
         * </tr>
         * <tr>
         * <th>String</th>
         * <td>{@link Strings#equals(CharSequence, CharSequence)}</td>
         * </tr>
         * </table>
        */
        EQUALS_STRICT('\uFFFB') { // == is converted to this value.
            @Override
            public int getDoubleArgumentPriority() {
                return 1;
            }

            @Override
            public Object process(final boolean leftValue, final boolean rightValue) {
                return leftValue == rightValue;
            }

            @Override
            public Object process(final int leftValue, final int rightValue) {
                return leftValue == rightValue;
            }

            @Override
            public Object process(final float leftValue, final float rightValue) {
                return Float.compare(leftValue, rightValue) == 0;
            }

            @Override
            public Object process(final String leftValue, final String rightValue) {
                return Strings.equals(leftValue, rightValue);
            }

            @Override
            protected String complexName() {
                return "==";
            }

            @Override
            public Operator negate() {
                return NOT_EQUALS_STRICT;
            }
        },
        /** <table summary="">
         * <tr>
         * <th>Type</th>
         * <th>Effect</th>
         * </tr>
         * <tr>
         * <th>boolean</th>
         * <td>Not supported.</td>
         * </tr>
         * <tr>
         * <th>int</th>
         * <td>first &lt; second</td>
         * </tr>
         * <tr>
         * <th>float</th>
         * <td>first &lt; second</td>
         * </tr>
         * <tr>
         * <th>String</th>
         * <td>Compares lengths with &lt;.</td>
         * </tr>
         * </table>
        */
        LOWER_THAN('<') {
            @Override
            public int getDoubleArgumentPriority() {
                return 1;
            }

            @Override
            public Object process(final int leftValue, final int rightValue) {
                return leftValue < rightValue;
            }

            @Override
            public Object process(final float leftValue, final float rightValue) {
                return leftValue < rightValue;
            }

            @Override
            public Object process(final String leftValue, final String rightValue) {
                if (Strings.isInt(rightValue)) {
                    return leftValue.length() < Integer.parseInt(rightValue);
                } else if (Strings.isInt(leftValue)) {
                    return Integer.parseInt(leftValue) < rightValue.length();
                }
                return leftValue.length() < rightValue.length();
            }

            @Override
            public Operator merge(final Operator operator) {
                if (operator == EQUALS) {
                    return LOWER_OR_EQUALS;
                }
                return super.merge(operator);
            }

            @Override
            public Operator negate() {
                return GREATER_THAN;
            }
        },
        /** <table summary="">
         * <tr>
         * <th>Type</th>
         * <th>Effect</th>
         * </tr>
         * <tr>
         * <th>boolean</th>
         * <td>Not supported.</td>
         * </tr>
         * <tr>
         * <th>int</th>
         * <td>first &lt;= second</td>
         * </tr>
         * <tr>
         * <th>float</th>
         * <td>first &lt;= second</td>
         * </tr>
         * <tr>
         * <th>String</th>
         * <td>Compares lengths with &lt;=.</td>
         * </tr>
         * </table>
        */
        LOWER_OR_EQUALS('\uFFFC') { // <= and =< are converted to this operator.
            @Override
            public int getDoubleArgumentPriority() {
                return 1;
            }

            @Override
            public Object process(final int leftValue, final int rightValue) {
                return leftValue <= rightValue;
            }

            @Override
            public Object process(final float leftValue, final float rightValue) {
                return leftValue <= rightValue;
            }

            @Override
            public Object process(final String leftValue, final String rightValue) {
                if (Strings.isInt(rightValue)) {
                    return leftValue.length() <= Integer.parseInt(rightValue);
                } else if (Strings.isInt(leftValue)) {
                    return Integer.parseInt(leftValue) <= rightValue.length();
                }
                return leftValue.length() <= rightValue.length();
            }

            @Override
            protected String complexName() {
                return "<=";
            }

            @Override
            public Operator negate() {
                return GREATER_OR_EQUALS;
            }
        },
        /** <table summary="">
         * <tr>
         * <th>Type</th>
         * <th>Effect</th>
         * </tr>
         * <tr>
         * <th>boolean</th>
         * <td>Not supported.</td>
         * </tr>
         * <tr>
         * <th>int</th>
         * <td>first &gt; second</td>
         * </tr>
         * <tr>
         * <th>float</th>
         * <td>first &gt; second</td>
         * </tr>
         * <tr>
         * <th>String</th>
         * <td>Compares lengths with &gt;.</td>
         * </tr>
         * </table>
        */
        GREATER_THAN('>') {
            @Override
            public int getDoubleArgumentPriority() {
                return 1;
            }

            @Override
            public Object process(final int leftValue, final int rightValue) {
                return leftValue > rightValue;
            }

            @Override
            public Object process(final float leftValue, final float rightValue) {
                return leftValue > rightValue;
            }

            @Override
            public Object process(final String leftValue, final String rightValue) {
                if (Strings.isInt(rightValue)) {
                    return leftValue.length() > Integer.parseInt(rightValue);
                } else if (Strings.isInt(leftValue)) {
                    return Integer.parseInt(leftValue) > rightValue.length();
                }
                return leftValue.length() > rightValue.length();
            }

            @Override
            public Operator merge(final Operator operator) {
                if (operator == EQUALS) {
                    return GREATER_OR_EQUALS;
                }
                return super.merge(operator);
            }

            @Override
            public Operator negate() {
                return LOWER_THAN;
            }
        },
        /** <table summary="">
         * <tr>
         * <th>Type</th>
         * <th>Effect</th>
         * </tr>
         * <tr>
         * <th>boolean</th>
         * <td>Not supported.</td>
         * </tr>
         * <tr>
         * <th>int</th>
         * <td>first &gt;= second</td>
         * </tr>
         * <tr>
         * <th>float</th>
         * <td>first &gt;= second</td>
         * </tr>
         * <tr>
         * <th>String</th>
         * <td>Compares lengths with &gt;=.</td>
         * </tr>
         * </table>
        */
        GREATER_OR_EQUALS('\uFFFD') { // >= and => are converted to this operator
            @Override
            public int getDoubleArgumentPriority() {
                return 1;
            }

            @Override
            public Object process(final int leftValue, final int rightValue) {
                return leftValue >= rightValue;
            }

            @Override
            public Object process(final float leftValue, final float rightValue) {
                return leftValue >= rightValue;
            }

            @Override
            public Object process(final String leftValue, final String rightValue) {
                if (Strings.isInt(rightValue)) {
                    return leftValue.length() >= Integer.parseInt(rightValue);
                } else if (Strings.isInt(leftValue)) {
                    return Integer.parseInt(leftValue) >= rightValue.length();
                }
                return leftValue.length() >= rightValue.length();
            }

            @Override
            protected String complexName() {
                return ">=";
            }

            @Override
            public Operator negate() {
                return LOWER_OR_EQUALS;
            }
        },
        /** <table summary="">
         * <tr>
         * <th>Type</th>
         * <th>Effect</th>
         * </tr>
         * <tr>
         * <th>boolean</th>
         * <td>Not supported.</td>
         * </tr>
         * <tr>
         * <th>int</th>
         * <td>first * second</td>
         * </tr>
         * <tr>
         * <th>float</th>
         * <td>first * second</td>
         * </tr>
         * <tr>
         * <th>String</th>
         * <td>Not supported.</td>
         * </tr>
         * </table>
        */
        MULTIPLY('*') {
            @Override
            public int getDoubleArgumentPriority() {
                return 4;
            }

            @Override
            public Object process(final float leftValue, final float rightValue) {
                return leftValue * rightValue;
            }

            @Override
            public Object process(final int leftValue, final int rightValue) {
                return leftValue * rightValue;
            }

            @Override
            public Operator negate() {
                return DIVIDE;
            }
        },
        /** <table summary="">
         * <tr>
         * <th>Type</th>
         * <th>Effect</th>
         * </tr>
         * <tr>
         * <th>boolean</th>
         * <td>Not supported.</td>
         * </tr>
         * <tr>
         * <th>int</th>
         * <td>first / second</td>
         * </tr>
         * <tr>
         * <th>float</th>
         * <td>first / second</td>
         * </tr>
         * <tr>
         * <th>String</th>
         * <td>Not supported.</td>
         * </tr>
         * </table>
        */
        DIVIDE('/') {
            @Override
            public int getDoubleArgumentPriority() {
                return 4;
            }

            @Override
            public Object process(final int leftValue, final int rightValue) {
                return leftValue / rightValue;
            }

            @Override
            public Object process(final float leftValue, final float rightValue) {
                return leftValue / rightValue;
            }

            @Override
            public Operator negate() {
                return MULTIPLY;
            }
        },
        /** <table summary="">
         * <tr>
         * <th>Type</th>
         * <th>Effect</th>
         * </tr>
         * <tr>
         * <th>boolean</th>
         * <td>XOR (^).</td>
         * </tr>
         * <tr>
         * <th>int</th>
         * <td>{@link Math#pow(double, double)} with result converted to int.</td>
         * </tr>
         * <tr>
         * <th>float</th>
         * <td>{@link Math#pow(double, double)} with result converted to float.</td>
         * </tr>
         * <tr>
         * <th>String</th>
         * <td>Not supported.</td>
         * </tr>
         * </table>
        */
        POW('^') {
            @Override
            public int getDoubleArgumentPriority() {
                return 4;
            }

            @Override
            public Object process(final boolean leftValue, final boolean rightValue) {
                return leftValue ^ rightValue;
            }

            @Override
            public Object process(final int leftValue, final int rightValue) {
                return (int) Math.pow(leftValue, rightValue);
            }

            @Override
            public Object process(final float leftValue, final float rightValue) {
                return (float) Math.pow(leftValue, rightValue);
            }
        },
        /** <table summary="">
         * <tr>
         * <th>Type</th>
         * <th>Effect</th>
         * </tr>
         * <tr>
         * <th>boolean</th>
         * <td>Not supported.</td>
         * </tr>
         * <tr>
         * <th>int</th>
         * <td>first + second</td>
         * </tr>
         * <tr>
         * <th>float</th>
         * <td>first + second</td>
         * </tr>
         * <tr>
         * <th>String</th>
         * <td>first + second</td>
         * </tr>
         * </table>
        */
        ADD('+') {
            @Override
            public int getDoubleArgumentPriority() {
                return 3;
            }

            @Override
            public int getSingleArgumentPriority() {
                return 5;
            }

            @Override
            public Object process(final int value) {
                return value;
            }

            @Override
            public Object process(final float value) {
                return value;
            }

            @Override
            public Object process(final int leftValue, final int rightValue) {
                return leftValue + rightValue;
            }

            @Override
            public Object process(final float leftValue, final float rightValue) {
                return leftValue + rightValue;
            }

            @Override
            public Object process(final String leftValue, final String rightValue) {
                return leftValue + rightValue;
            }

            @Override
            public Operator merge(final Operator operator) {
                if (operator == ADD) {
                    return INCREMENT;
                } else if (operator == SUBTRACT) {
                    return SUBTRACT;
                }
                return super.merge(operator);
            }

            @Override
            public Operator negate() {
                return SUBTRACT;
            }
        },
        /** <table summary="">
         * <tr>
         * <th>Type</th>
         * <th>Effect</th>
         * </tr>
         * <tr>
         * <th>boolean</th>
         * <td>Not supported.</td>
         * </tr>
         * <tr>
         * <th>int</th>
         * <td>first - second</td>
         * </tr>
         * <tr>
         * <th>float</th>
         * <td>first - second</td>
         * </tr>
         * <tr>
         * <th>String</th>
         * <td>{@link String#replace(CharSequence, CharSequence)}. Occurrences of second value in first value are
         * replaced with empty strings.</td>
         * </tr>
         * </table>
        */
        SUBTRACT('-') {
            @Override
            public int getDoubleArgumentPriority() {
                return 3;
            }

            @Override
            public int getSingleArgumentPriority() {
                return 5;
            }

            @Override
            public Object process(final int value) {
                return -value;
            }

            @Override
            public Object process(final float value) {
                return -value;
            }

            @Override
            public Object process(final int leftValue, final int rightValue) {
                return leftValue - rightValue;
            }

            @Override
            public Object process(final float leftValue, final float rightValue) {
                return leftValue - rightValue;
            }

            @Override
            public Object process(final String leftValue, final String rightValue) {
                return leftValue.replace(rightValue, Strings.EMPTY_STRING);
            }

            @Override
            public Operator merge(final Operator operator) {
                if (operator == SUBTRACT) {
                    return DECREMENT;
                } else if (operator == ADD) {
                    return SUBTRACT;
                }
                return super.merge(operator);
            }

            @Override
            public Operator negate() {
                return ADD;
            }
        },
        /** <table summary="">
         * <tr>
         * <th>Type</th>
         * <th>Effect</th>
         * </tr>
         * <tr>
         * <th>boolean</th>
         * <td>Not supported.</td>
         * </tr>
         * <tr>
         * <th>int</th>
         * <td>Modulo (%).</td>
         * </tr>
         * <tr>
         * <th>float</th>
         * <td>Modulo (%)</td>
         * </tr>
         * <tr>
         * <th>String</th>
         * <td>Not supported.</td>
         * </tr>
         * </table>
        */
        MODULO('%') {
            @Override
            public int getDoubleArgumentPriority() {
                return 2;
            }

            @Override
            public Object process(final int leftValue, final int rightValue) {
                return leftValue % rightValue;
            }

            @Override
            public Object process(final float leftValue, final float rightValue) {
                return leftValue % rightValue;
            }
        },
        /** <table summary="">
         * <tr>
         * <th>Type</th>
         * <th>Effect</th>
         * </tr>
         * <tr>
         * <th>boolean</th>
         * <td>"Or" (||).</td>
         * </tr>
         * <tr>
         * <th>int</th>
         * <td>Bit "or" (|).</td>
         * </tr>
         * <tr>
         * <th>float</th>
         * <td>Not supported.</td>
         * </tr>
         * <tr>
         * <th>String</th>
         * <td>Not supported.</td>
         * </tr>
         * </table>
        */
        OR('|') {
            @Override
            public int getDoubleArgumentPriority() {
                return 0;
            }

            @Override
            public Object process(final boolean leftValue, final boolean rightValue) {
                return leftValue || rightValue;
            }

            @Override
            public Object process(final int leftValue, final int rightValue) {
                return leftValue | rightValue;
            }

            @Override
            public Operator merge(final Operator operator) {
                if (operator == OR) {
                    return OR;
                }
                return super.merge(operator);
            }
        },
        /** <table summary="">
         * <tr>
         * <th>Type</th>
         * <th>Effect</th>
         * </tr>
         * <tr>
         * <th>boolean</th>
         * <td>"And" (&amp;&amp;).</td>
         * </tr>
         * <tr>
         * <th>int</th>
         * <td>Bit "and" (&amp;).</td>
         * </tr>
         * <tr>
         * <th>float</th>
         * <td>Not supported.</td>
         * </tr>
         * <tr>
         * <th>String</th>
         * <td>Not supported.</td>
         * </tr>
         * </table>
        */
        AND('&') {
            @Override
            public int getDoubleArgumentPriority() {
                return 0;
            }

            @Override
            public Object process(final boolean leftValue, final boolean rightValue) {
                return leftValue && rightValue;
            }

            @Override
            public Object process(final int leftValue, final int rightValue) {
                return leftValue & rightValue;
            }

            @Override
            public Operator merge(final Operator operator) {
                if (operator == AND) {
                    return AND;
                }
                return super.merge(operator);
            }
        },
        /** <table summary="">
         * <tr>
         * <th>Type</th>
         * <th>Effect</th>
         * </tr>
         * <tr>
         * <th>boolean</th>
         * <td>Not supported.</td>
         * </tr>
         * <tr>
         * <th>int</th>
         * <td>++value</td>
         * </tr>
         * <tr>
         * <th>float</th>
         * <td>++value</td>
         * </tr>
         * <tr>
         * <th>String</th>
         * <td>Not supported.</td>
         * </tr>
         * </table>
        */
        INCREMENT('\uFFFE') { // Converted from ++.
            @Override
            public int getSingleArgumentPriority() {
                return 5;
            }

            @Override
            public Object process(final int value) {
                return value + 1;
            }

            @Override
            public Object process(float value) {
                return ++value;
            }

            @Override
            public Operator negate() {
                return DECREMENT;
            }

            @Override
            protected String complexName() {
                return "++";
            }
        },
        /** <table summary="">
         * <tr>
         * <th>Type</th>
         * <th>Effect</th>
         * </tr>
         * <tr>
         * <th>boolean</th>
         * <td>Not supported.</td>
         * </tr>
         * <tr>
         * <th>int</th>
         * <td>--value</td>
         * </tr>
         * <tr>
         * <th>float</th>
         * <td>--value</td>
         * </tr>
         * <tr>
         * <th>String</th>
         * <td>Not supported.</td>
         * </tr>
         * </table>
        */
        DECREMENT('\uFFFF') { // Converted from --.
            @Override
            public int getSingleArgumentPriority() {
                return 5;
            }

            @Override
            public Object process(final int value) {
                return value - 1;
            }

            @Override
            public Object process(float value) {
                return --value;
            }

            @Override
            public Operator negate() {
                return INCREMENT;
            }

            @Override
            protected String complexName() {
                return "--";
            }
        };

        private final char sign;

        private DefaultOperator(final char sign) {
            this.sign = sign;
        }

        @Override
        public Operator merge(final Operator operator) {
            return null;
        }

        @Override
        public Object process(final String leftValue, final String rightValue) {
            throw new IllegalStateException(
                    this + " operator cannot handle two string arguments: " + leftValue + ", " + rightValue);
        }

        @Override
        public Object process(final String value) {
            throw new IllegalStateException(this + " operator cannot handle string argument: " + value);
        }

        @Override
        public Object process(final float leftValue, final float rightValue) {
            throw new IllegalStateException(
                    this + " operator cannot handle two float arguments: " + leftValue + ", " + rightValue);
        }

        @Override
        public Object process(final float value) {
            throw new IllegalStateException(this + " operator cannot handle float argument: " + value);
        }

        @Override
        public Object process(final int leftValue, final int rightValue) {
            throw new IllegalStateException(
                    this + " operator cannot handle two int arguments: " + leftValue + ", " + rightValue);
        }

        @Override
        public Object process(final int value) {
            throw new IllegalStateException(this + " operator cannot handle int argument: " + value);
        }

        @Override
        public Object process(final boolean leftValue, final boolean rightValue) {
            throw new IllegalStateException(
                    this + " operator cannot handle two boolean arguments: " + leftValue + ", " + rightValue);
        }

        @Override
        public Object process(final boolean value) {
            throw new IllegalStateException(this + " operator cannot handle boolean argument: " + value);
        }

        @Override
        public Operator negate() {
            throw new IllegalStateException(this + " operator cannot be negated.");
        }

        @Override
        public char getSign() {
            return sign;
        }

        /** @return value of a multiple-sign operator. */
        protected String complexName() {
            return null;
        }

        @Override
        public int getDoubleArgumentPriority() {
            return -1;
        }

        @Override
        public int getSingleArgumentPriority() {
            return -1;
        }

        @Override
        public String toString() {
            return (complexName() == null ? String.valueOf(getSign()) : complexName()) + " (" + name() + ')';
        }
    }
}
