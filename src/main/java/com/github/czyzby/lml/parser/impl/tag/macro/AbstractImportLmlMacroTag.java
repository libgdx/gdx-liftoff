package com.github.czyzby.lml.parser.impl.tag.macro;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractMacroLmlTag;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.collection.IgnoreCaseStringMap;

/** Import macros are used to read other templates and append their content into the currently passed template. There
 * are two ways to invoke the macros:
 *
 * <blockquote>
 *
 * <pre>
 * &lt;:import file.lml /&gt;
 * &lt;:import file.lml contentArg&gt; Content &lt;/:import&gt;
 * </pre>
 *
 * </blockquote>In the first example, file.lml will be read and appended to the current template without any changes. In
 * the second, every argument named contentArg (by default, arguments are represented like this: {contentArg}) will be
 * replaced with the text between import tags: " Content ".
 *
 * <p>
 * Import macros can be also used with named parameters: <blockquote>
 *
 * <pre>
 * &lt;:import path="file.lml" replace="contentArg"&gt; Content &lt;/:import&gt;
 * </pre>
 *
 * </blockquote>
 *
 * @author MJ */
public abstract class AbstractImportLmlMacroTag extends AbstractMacroLmlTag {
    /** Optional name of the first attribute. Path to the template. */
    public static final String PATH_ATTRIBUTE = "path";
    /** Optional name of the second attribute. Name of the argument to replace. */
    public static final String REPLACE_ATTRIBUTE = "replace";

    private CharSequence content;

    public AbstractImportLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    public void handleDataBetweenTags(final CharSequence rawMacroContent) {
        content = rawMacroContent;
    }

    @Override
    public void closeTag() {
        if (GdxArrays.isEmpty(getAttributes())) {
            getParser().throwErrorIfStrict("Import macros need at least one argument: template name to import.");
            return;
        }
        final FileHandle template = getFileHandle(getTemplateFileName());
        final CharSequence textToAppend;
        if (isReplacingArguments()) {
            if (content == null) {
                getParser().throwErrorIfStrict(
                        "Import macros with content name attribute (second attribute) have to be parental and contain some content that can be replaced in the imported template. Remove second import attribute or add data between macro tags.");
            }
            textToAppend = replaceArguments(template.readString(), getArguments());
        } else {
            textToAppend = template.readString();
        }
        appendTextToParse(textToAppend);
    }

    /** @return arguments to replace in the imported template. */
    protected ObjectMap<String, CharSequence> getArguments() {
        final ObjectMap<String, CharSequence> arguments = new IgnoreCaseStringMap<CharSequence>();
        arguments.put(getArgumentName(), content);
        return arguments;
    }

    /** @param templateFileName path to the template to import.
     * @return an instance of FileHandle used to construct with the passed template path. */
    protected abstract FileHandle getFileHandle(String templateFileName);

    /** @return attribute with the name of the template file. Validate attributes list before usage. */
    protected String getTemplateFileName() {
        if (hasAttribute(PATH_ATTRIBUTE)) {
            return getAttribute(PATH_ATTRIBUTE);
        } else if (GdxMaps.isNotEmpty(getNamedAttributes())) {
            getParser().throwError(
                    "Import macro has to have a 'path' attribute. Attributes found: " + getNamedAttributes());
        }
        return getAttributes().get(0);
    }

    /** @return attribute with the name of the argument that should replace template's content. Validate attributes list
     *         before usage. */
    protected String getArgumentName() {
        if (hasAttribute(REPLACE_ATTRIBUTE)) {
            return getAttribute(REPLACE_ATTRIBUTE);
        }
        return getAttributes().get(1);
    }

    /** @return true if contains at least 2 arguments: template file and content argument. */
    protected boolean isReplacingArguments() {
        return hasAttribute(REPLACE_ATTRIBUTE) || GdxArrays.sizeOf(getAttributes()) > 1;
    }

    @Override
    public String[] getExpectedAttributes() {
        return new String[] { PATH_ATTRIBUTE, REPLACE_ATTRIBUTE };
    }
}
