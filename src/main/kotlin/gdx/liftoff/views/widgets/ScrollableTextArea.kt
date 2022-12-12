package gdx.liftoff.views.widgets

import com.badlogic.gdx.graphics.g2d.Batch
import com.github.czyzby.kiwi.util.common.Strings
import com.github.czyzby.lml.parser.LmlParser
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder
import com.github.czyzby.lml.parser.tag.LmlTag
import com.github.czyzby.lml.parser.tag.LmlTagProvider
import com.github.czyzby.lml.vis.parser.impl.tag.VisTextAreaLmlTag
import com.kotcrab.vis.ui.widget.VisTextArea
import com.kotcrab.vis.ui.widget.VisTextField

/**
 * Custom [VisTextArea] used to display logs in the generation prompt. Supports embedding in scroll pane by calculating
 * required space needed for current text.
 * @author Kotcrab
 */
class ScrollableTextArea(text: String, styleName: String) : VisTextArea(text, styleName) {
  init {
    style.font.data.markupEnabled = true
  }
  override fun getPrefWidth(): Float {
    return width
  }

  override fun getPrefHeight(): Float {
    return lines * style.font.lineHeight
  }

  override fun setText(str: String) {
    // TextArea seems to have problem when '\r\n' (Windows style) is used as line ending, as it treats it as two
    // lines. Although example templates files are using '\n', Git (when cloning the repository) may replace them
    // with '\r\n' on Windows.
    super.setText(Strings.stripCharacter(str, '\r'))
  }

  override fun draw(batch: Batch?, parentAlpha: Float) {
    try {
      super.draw(batch, parentAlpha)
    } catch (getYourActTogetherScene2D: IndexOutOfBoundsException) {
      copy()
    }
  }

  /**
   * Provides CodeTextArea tags.
   * @author Kotcrab
   */
  class ScrollableTextAreaLmlTagProvider : LmlTagProvider {
    override fun create(parser: LmlParser, parentTag: LmlTag, rawTagData: StringBuilder): LmlTag =
      ScrollableTextAreaLmlTag(parser, parentTag, rawTagData)
  }

  /**
   * Handles CodeTextArea actor.
   * @author Kotcrab
   */
  class ScrollableTextAreaLmlTag(parser: LmlParser, parentTag: LmlTag, rawTagData: StringBuilder) :
    VisTextAreaLmlTag(parser, parentTag, rawTagData) {
    override fun getNewInstanceOfTextField(textBuilder: TextLmlActorBuilder): VisTextField =
      ScrollableTextArea(textBuilder.text, textBuilder.styleName)
  }
}
