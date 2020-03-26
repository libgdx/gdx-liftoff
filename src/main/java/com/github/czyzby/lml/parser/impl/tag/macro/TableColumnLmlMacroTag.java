package com.github.czyzby.lml.parser.impl.tag.macro;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Tables utility. Allows to set column defaults for a selected column. For example:
 *
 * <blockquote>
 *
 * <pre>
 * &lt;table&gt;
 *     &lt;:column 2 pad=3 grow=true /&gt;
 * &lt;/table&gt;
 * </pre>
 *
 * </blockquote>This will set cell defaults of column with 2 index.
 *
 * <p>
 * This macro cannot parse text between its tags. Strict parser will throw an exception if you attempt to do so.
 *
 * <p>
 * This macro supports named attributes:<blockquote>
 *
 * <pre>
 * &lt;table&gt;
 *     &lt;:column column="2" pad="3" grow="true" /&gt;
 * &lt;/table&gt;
 * </pre>
 *
 * </blockquote>
 *
 * @author MJ */
public class TableColumnLmlMacroTag extends TableCellLmlMacroTag {
    private static final String COLUMN_ATTRIBUTE = "column";

    public TableColumnLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected boolean supportsNamedAttributes() {
        return false;
    }

    @Override
    protected boolean supportsOptionalNamedAttributes() {
        return true;
    }

    @Override
    protected void processCellWithNoAttributes(final Table table) {
        getParser().throwErrorIfStrict("Column macro needs at least one attribute: column number.");
    }

    @Override
    protected Cell<?> extractCell(final Table table) {
        return table.columnDefaults(getColumnId());
    }

    /** @return parsed value of the attribute that represents column ID. */
    protected int getColumnId() {
        return getParser().parseInt(getColumnAttribute(), getTable());
    }

    /** @return unparsed value of attribute that represents column ID. */
    protected String getColumnAttribute() {
        if (hasAttribute(COLUMN_ATTRIBUTE)) {
            return getAttribute(COLUMN_ATTRIBUTE);
        }
        return getAttributes().first();
    }

    @Override
    protected boolean isInternalMacroAttribute(final String key) {
        return COLUMN_ATTRIBUTE.equals(key);
    }

    @Override
    public String[] getExpectedAttributes() {
        return new String[] { COLUMN_ATTRIBUTE };
    }
}
