package com.github.czyzby.lml.parser.impl.tag.macro;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUserObject.StandardTableTarget;

/** Tables utility. Adds row to the current table. Allows to set row defaults with its attributes. For example:
 *
 * <blockquote>
 *
 * <pre>
 * &lt;table&gt;
 *     First row.
 *     &lt;:row/&gt;
 *     Second row.
 * &lt;/table&gt;
 * </pre>
 *
 * </blockquote>This will append a new row after "First row." label.
 *
 * <blockquote>
 *
 * <pre>
 * &lt;table&gt;
 *     &lt;:row pad=5&gt;
 *         First row.
 *     &lt;/:row&gt;
 *     &lt;:row expandY=true&gt;
 *         Second row.
 *     &lt;/:row&gt;
 * &lt;/table&gt;
 * </pre>
 *
 * </blockquote>This will create 2 separate rows with customized row defaults. Note that row is created BEFORE tags
 * inside macro are parsed. For example:
 *
 * <blockquote>
 *
 * <pre>
 * &lt;table&gt;
 *     &lt;:row pad=5&gt;
 *         First row.
 *     &lt;/:row&gt;
 *     Same row.
 * &lt;/table&gt;
 * </pre>
 *
 * </blockquote>This would cause for both labels to be in the same row, as only 1 row is added - when the macro starts.
 *
 * @author MJ */
public class TableRowLmlMacroTag extends TableCellLmlMacroTag {
    private CharSequence content;

    public TableRowLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    public void handleDataBetweenTags(final CharSequence rawData) {
        content = rawData;
    }

    @Override
    public void closeTag() {
        processCell();
        if (Strings.isNotEmpty(content)) {
            appendTextToParse(content.toString());
        }
    }

    @Override
    protected void processCellWithNoAttributes(final Table table) {
        // Extracting the default table and adding a new row:
        StandardTableTarget.MAIN.extract(table).row();
    }

    @Override
    protected Cell<?> extractCell(final Table table) {
        return table.row();
    }
}
