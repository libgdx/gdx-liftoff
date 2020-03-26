package com.github.czyzby.lml.vis.parser.impl.attribute.table;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.attribute.table.cell.AbstractCellLmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;
import com.kotcrab.vis.ui.layout.FloatingGroup;

/** See {@link Cell#prefSize(Value, Value)}. See
 * {@link LmlUtilities#parseHorizontalValue(LmlParser, LmlTag, Actor, String)} and
 * {@link LmlUtilities#parseVerticalValue(LmlParser, LmlTag, Actor, String)} for more info on value parsing. Honors
 * {@link FloatingGroup} settings. Mapped to "prefSize".
 *
 * @author MJ */
public class PrefSizeLmlAttribute extends AbstractCellLmlAttribute {
    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final Cell<?> cell,
            final String rawAttributeData) {
        final Value horizontalValue = LmlUtilities.parseHorizontalValue(parser, tag.getParent(), actor,
                rawAttributeData);
        final Value verticalValue = LmlUtilities.parseVerticalValue(parser, tag.getParent(), actor, rawAttributeData);
        cell.prefSize(horizontalValue, verticalValue);
        if (actor instanceof FloatingGroup) {
            ((FloatingGroup) actor).setPrefWidth(horizontalValue.get(actor));
            ((FloatingGroup) actor).setPrefHeight(verticalValue.get(actor));
        }
    }

    @Override
    protected void processForActor(final LmlParser parser, final LmlTag tag, final Actor actor,
            final String rawAttributeData) {
        if (actor instanceof FloatingGroup) {
            final float size = parser.parseFloat(rawAttributeData, actor);
            ((FloatingGroup) actor).setPrefWidth(size);
            ((FloatingGroup) actor).setPrefHeight(size);
        } else {
            super.processForActor(parser, tag, actor, rawAttributeData);
        }
    }
}
