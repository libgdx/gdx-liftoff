package com.github.czyzby.lml.parser.impl.tag.macro;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Imports templates using local file handles.
 *
 * @author MJ
 * @see AbstractImportLmlMacroTag */
public class ImportLocalLmlMacroTag extends AbstractImportLmlMacroTag {
    public ImportLocalLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected FileHandle getFileHandle(final String templateFileName) {
        return Gdx.files.local(templateFileName);
    }
}
