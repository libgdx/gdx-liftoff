package com.github.czyzby.setup.views.widgets

import com.badlogic.gdx.scenes.scene2d.Actor
import com.github.czyzby.kiwi.util.common.Strings
import com.github.czyzby.lml.parser.LmlParser
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder
import com.github.czyzby.lml.parser.tag.LmlActorBuilder
import com.github.czyzby.lml.parser.tag.LmlTag
import com.github.czyzby.lml.parser.tag.LmlTagProvider
import com.github.czyzby.lml.vis.parser.impl.tag.VisLabelLmlTag
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTextArea

/** Custom [VisTextArea] used to display logs in the generation prompt. Supports embedding in scroll pane by calculating
 * required space needed for current text.

 * @author Kotcrab
 */
class ScrollableTextArea(text: String, styleName: String) : VisLabel(text, styleName) {
    init {
        style.font.data.markupEnabled = true
        wrap = true;
    }
    override fun getPrefWidth(): Float {
        return width
    }

    override fun getPrefHeight(): Float {
        return glyphLayout.height
    }

    override fun setText(str: CharSequence) {
        // TextArea seems to have problem when '\r\n' (Windows style) is used as line ending, as it treats it as two
        // lines. Although example templates files are using '\n', Git (when cloning the repository) may replace them
        // with '\r\n' on Windows.
        super.setText(Strings.stripCharacter(str, '\r'))
    }

    /** Provides [CodeTextArea] tags.

     * @author Kotcrab
     */
    class ScrollableTextAreaLmlTagProvider : LmlTagProvider {
        override fun create(parser: LmlParser, parentTag: LmlTag, rawTagData: StringBuilder): LmlTag
                = ScrollableTextAreaLmlTag(parser, parentTag, rawTagData)

    }

    /** Handles [CodeTextArea] actor.

     * @author Kotcrab
     */
    class ScrollableTextAreaLmlTag(parser: LmlParser, parentTag: LmlTag, rawTagData: StringBuilder) :
            VisLabelLmlTag(parser, parentTag, rawTagData) {
        override fun getNewInstanceOfActor(builder: LmlActorBuilder?): Actor {
            val textBuilder = builder as TextLmlActorBuilder
            return ScrollableTextArea(textBuilder.getText(), builder.getStyleName())

        }
    }
    //        override fun getNewInstanceOfTextField(textBuilder: TextLmlActorBuilder): Label =
    //                ScrollableTextArea(textBuilder.text, textBuilder.styleName)
}
