package com.github.czyzby.lml.vis.parser.impl.attribute.input;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.util.highlight.BaseHighlighter;
import com.kotcrab.vis.ui.widget.HighlightTextArea;

/**
 * See {@link HighlightTextArea#setHighlighter(BaseHighlighter)}. Expects ID of a method that returns a
 * {@link BaseHighlighter} instance. Mapped to "highlighter".
 * @author MJ
 */
public class HighlighterLmlAttribute implements LmlAttribute<HighlightTextArea> {
    @Override
    public Class<HighlightTextArea> getHandledType() {
        return HighlightTextArea.class;
    }

    @Override
    public void process(LmlParser parser, LmlTag tag, HighlightTextArea highlightTextArea, String rawAttributeData) {
        @SuppressWarnings("unchecked")
        final ActorConsumer<BaseHighlighter, HighlightTextArea> action = (ActorConsumer<BaseHighlighter, HighlightTextArea>) parser
                .parseAction(rawAttributeData, highlightTextArea);
        if (action == null) {
            parser.throwErrorIfStrict(
                    "Highlighter attribute expects action ID of a method returning BaseHighlighter implementation. No method found for ID: "
                            + rawAttributeData);
            return;
        }
        highlightTextArea.setHighlighter(action.consume(highlightTextArea));
        highlightTextArea.processHighlighter();
    }
}
