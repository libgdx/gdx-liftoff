package com.github.czyzby.lml.vis.parser.impl.attribute.table;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.attribute.table.cell.AbstractCellLmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;
import com.kotcrab.vis.ui.layout.FloatingGroup;

/** See {@link Cell#prefHeight(Value)}. See {@link LmlUtilities#parseHorizontalValue(LmlParser, LmlTag, Actor, String)}
 * and {@link LmlUtilities#parseVerticalValue(LmlParser, LmlTag, Actor, String)} for more info on value parsing. Honors
 * {@link FloatingGroup} settings. Mapped to "prefHeight".
 *
 * @author MJ */
public class PrefHeightLmlAttribute extends AbstractCellLmlAttribute {
    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final Cell<?> cell,
            final String rawAttributeData) {
        final Value verticalValue = LmlUtilities.parseVerticalValue(parser, tag.getParent(), actor, rawAttributeData);
        cell.prefHeight(verticalValue);
        if (actor instanceof FloatingGroup) {
            ((FloatingGroup) actor).setPrefHeight(verticalValue.get(actor));
        }
    }

    @Override
    protected void processForActor(final LmlParser parser, final LmlTag tag, final Actor actor,
            final String rawAttributeData) {
        if (actor instanceof FloatingGroup) {
            ((FloatingGroup) actor).setPrefHeight(parser.parseFloat(rawAttributeData, actor));
        } else {
            super.processForActor(parser, tag, actor, rawAttributeData);
        }
    }
}
