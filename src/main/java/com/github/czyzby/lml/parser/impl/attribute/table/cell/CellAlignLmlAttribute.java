package com.github.czyzby.lml.parser.impl.attribute.table.cell;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** See {@link Cell#align(int)}. Expends string matching name of
 * {@link com.github.czyzby.kiwi.util.gdx.scene2d.Alignment} enum constant. Mapped to "align".
 *
 * @author MJ */
public class CellAlignLmlAttribute extends AbstractCellLmlAttribute {
    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final Cell<?> cell,
            final String rawAttributeData) {
        cell.align(LmlUtilities.parseAlignment(parser, actor, rawAttributeData));
    }

    @Override
    protected void processForActor(final LmlParser parser, final LmlTag tag, final Actor actor,
            final String rawAttributeData) {
        // Applied if not in a cell.
        if (actor instanceof Label) {
            ((Label) actor).setAlignment(LmlUtilities.parseAlignment(parser, actor, rawAttributeData));
        } else if (actor instanceof Table) {
            ((Table) actor).align(LmlUtilities.parseAlignment(parser, actor, rawAttributeData));
        } else if (actor instanceof Image) {
            ((Image) actor).setAlign(LmlUtilities.parseAlignment(parser, actor, rawAttributeData));
        } else if (actor instanceof HorizontalGroup) {
            ((HorizontalGroup) actor).align(LmlUtilities.parseAlignment(parser, actor, rawAttributeData));
        } else if (actor instanceof VerticalGroup) {
            ((VerticalGroup) actor).align(LmlUtilities.parseAlignment(parser, actor, rawAttributeData));
        } else if (actor instanceof TextField) {
            ((TextField) actor).setAlignment(LmlUtilities.parseAlignment(parser, actor, rawAttributeData));
        } else {
            // Exception:
            super.processForActor(parser, tag, actor, rawAttributeData);
        }
    }
}
