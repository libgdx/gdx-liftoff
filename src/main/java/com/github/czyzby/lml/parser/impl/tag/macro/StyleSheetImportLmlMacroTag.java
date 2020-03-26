package com.github.czyzby.lml.parser.impl.tag.macro;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractMacroLmlTag;
import com.github.czyzby.lml.parser.tag.LmlTag;

/**
 * Style sheet import macros allow to import LML style sheet files inside LML templates.
 * <blockquote>
 * <pre>
 * &lt;:importStyleSheet file.lss /&gt;
 * &lt;:importStyleSheet&gt; file.lss &lt;/:importStyleSheet&gt;
 * </pre>
 * </blockquote>
 * Import macro can be also used with named parameters:
 * <blockquote>
 * <pre>
 * &lt;:importStyleSheet path="file.lss" fileType="Internal" /&gt;
 * </pre>
 * </blockquote>
 * @author MJ
 */
public class StyleSheetImportLmlMacroTag extends AbstractMacroLmlTag {
    /** Optional name of the first attribute. Path to the imported style sheet. */
    public static final String PATH_ATTRIBUTE = "path";
    /** Optional name of the second attribute. Determines file handle type. */
    public static final String FILE_TYPE_ATTRIBUTE = "filetype";

    private String path;

    public StyleSheetImportLmlMacroTag(LmlParser parser, LmlTag parentTag, StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    public void handleDataBetweenTags(CharSequence rawData) {
        if (Strings.isNotWhitespace(rawData)) {
            path = rawData.toString().trim();
        }
    }

    @Override
    public void closeTag() {
        getParser().parseStyleSheet(Gdx.files.getFileHandle(getStyleSheetFileName(), getFileType()));
    }

    /** @return attribute with the name of the style sheet file. */
    protected String getStyleSheetFileName() {
        if (hasAttribute(PATH_ATTRIBUTE)) {
            return getAttribute(PATH_ATTRIBUTE);
        } else if (path != null) {
            return path;
        } else if (GdxMaps.isNotEmpty(getNamedAttributes())) {
            getParser().throwError(
                    "Import macro has to have a 'path' attribute. Attributes found: " + getNamedAttributes());
        } else if (GdxArrays.isEmpty(getAttributes())) {
            getParser().throwError("Import macro has to have at least one argument: path to the imported style sheet.");
        }
        return getAttributes().get(0);
    }

    protected Files.FileType getFileType() {
        if (hasAttribute(FILE_TYPE_ATTRIBUTE)) {
            try {
                return Files.FileType.valueOf(getAttribute(FILE_TYPE_ATTRIBUTE));
            } catch (Exception exception) {
                getParser().throwErrorIfStrict("Invalid file type.", exception);
            }
        }
        return Files.FileType.Internal;
    }

    @Override
    public String[] getExpectedAttributes() {
        return new String[] { PATH_ATTRIBUTE, FILE_TYPE_ATTRIBUTE };
    }
}
