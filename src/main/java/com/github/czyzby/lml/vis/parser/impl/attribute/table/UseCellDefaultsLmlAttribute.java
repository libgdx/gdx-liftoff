package com.github.czyzby.lml.vis.parser.impl.attribute.table;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUserObject.StandardTableTarget;
import com.kotcrab.vis.ui.util.TableUtils;

/** See {@link TableUtils#setSpacingDefaults(Table)}. If true, will extract main table from the actor (returns table
 * itself for most widgets; returns content table for dialogs) and invoke Vis utility method to set its default
 * spacings. Mapped to "useCellDefaults", "useVisDefaults".
 *
 * @author MJ */
public class UseCellDefaultsLmlAttribute implements LmlAttribute<Table> {
    @Override
    public Class<Table> getHandledType() {
        return Table.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Table actor, final String rawAttributeData) {
        if (parser.parseBoolean(rawAttributeData, actor)) {
            TableUtils.setSpacingDefaults(StandardTableTarget.MAIN.extract(actor));
        }
    }
}
