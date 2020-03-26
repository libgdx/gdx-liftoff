package com.github.czyzby.lml.parser.impl.tag.macro;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.ObjectSet;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.kiwi.util.gdx.collection.GdxSets;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.LmlSyntax;
import com.github.czyzby.lml.parser.impl.attribute.table.cell.AbstractCellLmlAttribute;
import com.github.czyzby.lml.parser.impl.tag.AbstractMacroLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlBuildingAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUserObject.StandardTableTarget;
import com.github.czyzby.lml.util.LmlUserObject.TableTarget;

/** Tables utility. Allows to append an empty cell to a table, without having to use mock-up actors to fill it. For
 * example: <blockquote>
 *
 * <pre>
 * &lt;table&gt;
 *     First cell.
 *     &lt;:cell/&gt;
 *     Third cell.
 * &lt;/table&gt;
 * </pre>
 *
 * </blockquote> Thanks to cell macro usage, such table would have 3 cells total. Cell macro tag can have any cell
 * attributes if you feel the need to customize it:<blockquote>
 *
 * <pre>
 * &lt;table&gt;
 *     First row.
 *     &lt;:cell row="true" pad="8"/&gt;
 *     Second row.
 * &lt;/table&gt;
 * </pre>
 *
 * </blockquote>However, you should generally use row macro to handle row defaults and append new rows.
 *
 * @author MJ
 * @see TableRowLmlMacroTag
 * @see TableColumnLmlMacroTag */
public class TableCellLmlMacroTag extends AbstractMacroLmlTag {
    public TableCellLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected boolean supportsNamedAttributes() {
        return true;
    }

    @Override
    public void handleDataBetweenTags(final CharSequence rawData) {
        if (Strings.isNotBlank(rawData)) {
            getParser().throwErrorIfStrict("Table cell macro cannot parse text between tags.");
        }
    }

    @Override
    public void closeTag() {
        processCell();
    }

    /** This method is invoked when the tag is closed. Extracts a cell from the table. */
    protected void processCell() {
        final ObjectMap<String, String> attributes = getNamedAttributes();
        if (GdxMaps.isEmpty(attributes)) {
            processCellWithNoAttributes(getTable());
            return;
        }
        final LmlActorBuilder builder = new LmlActorBuilder(); // Used to determine table.
        final ObjectSet<String> processedAttributes = GdxSets.newSet();
        processBuildingAttributeToDetermineTable(attributes, processedAttributes, builder);
        final Table targetTable = getTarget(builder).extract(getTable());
        final Cell<?> cell = extractCell(targetTable);
        processCellAttributes(attributes, processedAttributes, targetTable, cell);
    }

    /** @param table should have a cell extracted.
     * @return cell selected by the macro. By default, adds a new cell to the table. */
    protected Cell<?> extractCell(final Table table) {
        return table.add();
    }

    /** This method is invoked if the cell macro has no additional attributes.
     *
     * @param table is a parent of the macro tag. */
    protected void processCellWithNoAttributes(final Table table) {
        // Adding empty cell:
        table.add();
    }

    /** @return table extracted from parent tag. */
    protected Table getTable() {
        final Actor table = getParent().getActor();
        if (table instanceof Table) {
            return (Table) table;
        }
        getParser().throwError(
                getTagName() + " macro can be used only inside table tags (or one of its subclasses). Found \""
                        + getTagName() + "\" tag with no direct table parent.");
        return null;
    }

    /** @param builder may contain specific table target.
     * @return table target chosen by the builder. */
    protected TableTarget getTarget(final LmlActorBuilder builder) {
        return builder.getTableTarget() == null ? StandardTableTarget.MAIN : builder.getTableTarget();
    }

    /** This is meant to handle toButtonTable, toTitleTable, toDialogTable to choose which table should have a row
     * appended.
     *
     * @param attributes named attributes of the macro.
     * @param processedAttributes should contain processed building attributes after this method invocation.
     * @param builder used to process named attributes. */
    protected void processBuildingAttributeToDetermineTable(final ObjectMap<String, String> attributes,
            final ObjectSet<String> processedAttributes, final LmlActorBuilder builder) {
        final LmlSyntax syntax = getParser().getSyntax();
        for (final Entry<String, String> attribute : attributes) {
            final LmlBuildingAttribute<LmlActorBuilder> buildingAttribute = syntax
                    .getBuildingAttributeProcessor(builder, attribute.key);
            if (buildingAttribute != null) {
                buildingAttribute.process(getParser(), getParent(), builder, attribute.value);
                processedAttributes.add(attribute.key);
            }
        }
    }

    /** This is meant to handle cell attributes that will modify the extracted cell.
     *
     * @param attributes named attributes of the macro.
     * @param processedAttributes already processed attributes. Should be ignored.
     * @param table owner of the cell.
     * @param cell cell of the row. Should have its defaults set. */
    protected void processCellAttributes(final ObjectMap<String, String> attributes,
            final ObjectSet<String> processedAttributes, final Table table, final Cell<?> cell) {
        final LmlSyntax syntax = getParser().getSyntax();
        for (final Entry<String, String> attribute : attributes) {
            if (processedAttributes.contains(attribute.key)) {
                continue;
            }
            final LmlAttribute<?> cellAttribute = syntax.getAttributeProcessor(table, attribute.key);
            if (cellAttribute instanceof AbstractCellLmlAttribute) {
                ((AbstractCellLmlAttribute) cellAttribute).process(getParser(), getParent(), table, cell,
                        attribute.value);
            } else {
                if (!isInternalMacroAttribute(attribute.key)) {
                    getParser().throwErrorIfStrict(getTagName()
                            + " macro can process only cell attributes. Found unknown or invalid attribute: "
                            + attribute.key);
                }
            }
        }
    }

    /** @param key lower-case attribute name present in the tag.
     * @return true if the attribute is used internally by the macro and should not be processed by the cell. */
    protected boolean isInternalMacroAttribute(final String key) {
        // Basic cell macro has no internal attributes.
        return false;
    }
}
