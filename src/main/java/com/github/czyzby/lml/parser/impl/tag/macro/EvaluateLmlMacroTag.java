package com.github.czyzby.lml.parser.impl.tag.macro;

import com.github.czyzby.kiwi.util.common.Nullables;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.impl.tag.AbstractMacroLmlTag;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Macro that allows to invoke registered LML methods during template parsing. First attribute is the method name to
 * invoke (method marker optional). The second - optional - attribute is the name of LML argument that should be set as
 * the result of the method. If the macro tag is a child, it will pass its parent's actor as the method argument; if the
 * macro tag is a parent, it will look for method consuming a string (or no-arg) and pass its data between tags as
 * method argument. For example:
 *
 * <blockquote>
 *
 * <pre>
 * &lt;label id=someLabel&gt;&lt;:evaluate methodName/&gt;&lt;/label&gt;
 * </pre>
 *
 * </blockquote>This macro will look for method with "methodName" ID (either {@literal @}LmlAction-mapped, actual method
 * name or a field name) and invoke it with label (id: "someLabel") as its argument - if it consumes one.
 *
 * <blockquote>
 *
 * <pre>
 * &lt;:evaluate methodId argumentName&gt;Method argument&lt;/:evaluate&gt;
 * </pre>
 *
 * </blockquote>This macro will look for a method with "methodId" ID (consuming a string or no-arg) and invoke it with
 * "Method argument" string argument. Its result will be assigned to "argumentName" all will be accessible like any
 * other LML argument: {argumentName}. Note that tags between evaluate macro tags are NOT parsed and will be sent as
 * plain text to the method; LML arguments, on the other hand, will be properly replaced.
 *
 * <p>
 * This macro can be also used with named parameters: <blockquote>
 *
 * <pre>
 * &lt;:evaluate method="methodId" id="argumentName"&gt;Method argument&lt;/:evaluate&gt;
 * </pre>
 *
 * </blockquote>
 *
 * @author MJ */
public class EvaluateLmlMacroTag extends AbstractMacroLmlTag {
    /** Optional name of first attribute: method name. */
    public static final String METHOD_ATTRIBUTE = "method";
    /** Optional name of second attribute: argument ID. */
    public static final String ID_ATTRIBUTE = "id";

    private String methodArgument;

    public EvaluateLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    public void handleDataBetweenTags(final CharSequence rawMacroContent) {
        if (Strings.isNotEmpty(rawMacroContent)) {
            methodArgument = replaceArguments(rawMacroContent, getParser().getData().getArguments()).toString();
        }
    }

    @Override
    public void closeTag() {
        if (GdxArrays.isEmpty(getAttributes())) {
            getParser().throwErrorIfStrict("Evaluate macro needs at least one attribute: method ID.");
            return;
        }
        final Object result;
        if (methodArgument != null) {
            result = executeMethod(methodArgument);
        } else {
            result = executeMethod(getParent() != null ? getParent().getActor() : null);
        }
        processMethodResult(result);
    }

    /** @param result was returned by the evaluated method. */
    protected void processMethodResult(final Object result) {
        if (hasAssignmentArgumentName()) {
            getParser().getData().addArgument(getAssignmentArgumentName(), Nullables.toString(result));
        }
    }

    /** @param value method argument. Finds a method that consumes this type of object (or no-arg).
     * @return result of the invoked method.
     * @param <ArgumentType> type of argument consumed by the method. */
    protected <ArgumentType> Object executeMethod(final ArgumentType value) {
        final ActorConsumer<?, ArgumentType> action = getParser().parseAction(getMethodId(), value);
        if (action == null) {
            getParser().throwErrorIfStrict("Cannot process evaluate macro. Did not found method with ID: "
                    + getMethodId() + " for value: " + value);
            return null;
        }
        return action.consume(value);
    }

    /** @return attribute assigned to method ID. */
    protected String getMethodId() {
        if (hasAttribute(METHOD_ATTRIBUTE)) {
            return getAttribute(METHOD_ATTRIBUTE);
        } else if (GdxMaps.isNotEmpty(getNamedAttributes())) {
            getParser().throwError("Evaluate macro needs 'method' attribute. Got: " + getNamedAttributes());
        }
        return getAttributes().get(0);
    }

    /** @return true if has at least 2 attributes. */
    protected boolean hasAssignmentArgumentName() {
        return GdxArrays.isNotEmpty(getAttributes()) && GdxArrays.sizeOf(getAttributes()) > 1;
    }

    /** @return name of the argument that should be assigned to the result of the evaluated method. */
    protected String getAssignmentArgumentName() {
        if (hasAttribute(ID_ATTRIBUTE)) {
            return getAttribute(ID_ATTRIBUTE);
        }
        return getAttributes().get(1);
    }

    @Override
    public String[] getExpectedAttributes() {
        return new String[] { METHOD_ATTRIBUTE, ID_ATTRIBUTE };
    }
}
