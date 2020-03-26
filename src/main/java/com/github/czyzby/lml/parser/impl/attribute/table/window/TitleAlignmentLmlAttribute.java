package com.github.czyzby.lml.parser.impl.attribute.table.window;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

/** Sets alignment of {@link Window#getTitleLabel()}. Mapped to "titleAlign", "titleAlignment".
 *
 * @author MJ */
public class TitleAlignmentLmlAttribute implements LmlAttribute<Window> {
    @Override
    public Class<Window> getHandledType() {
        return Window.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Window actor, final String rawAttributeData) {
        final Cell<?> cell = actor.getTitleTable().getCell(actor.getTitleLabel());
        cell.expand().fill(); // This is necessary for the alignment setting to be actually noticeable.
        actor.getTitleLabel().setAlignment(LmlUtilities.parseAlignment(parser, actor, rawAttributeData));
    }
}
