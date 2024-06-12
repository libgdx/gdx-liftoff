package gdx.liftoff.config

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.czyzby.autumn.annotation.Component
import com.github.czyzby.autumn.annotation.Destroy
import com.github.czyzby.autumn.annotation.Initiate
import com.github.czyzby.autumn.mvc.component.i18n.LocaleService
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService
import com.github.czyzby.autumn.mvc.component.ui.SkinService
import com.github.czyzby.autumn.mvc.config.AutumnActionPriority
import com.github.czyzby.autumn.mvc.stereotype.preference.AvailableLocales
import com.github.czyzby.autumn.mvc.stereotype.preference.I18nBundle
import com.github.czyzby.autumn.mvc.stereotype.preference.I18nLocale
import com.github.czyzby.autumn.mvc.stereotype.preference.LmlMacro
import com.github.czyzby.autumn.mvc.stereotype.preference.LmlParserSyntax
import com.github.czyzby.autumn.mvc.stereotype.preference.Preference
import com.github.czyzby.autumn.mvc.stereotype.preference.StageViewport
import com.github.czyzby.kiwi.util.common.Exceptions
import com.github.czyzby.kiwi.util.gdx.asset.lazy.provider.ObjectProvider
import com.github.czyzby.lml.parser.LmlParser
import com.github.czyzby.lml.parser.tag.LmlAttribute
import com.github.czyzby.lml.parser.tag.LmlTag
import com.github.czyzby.lml.vis.parser.impl.VisLmlSyntax
import com.github.czyzby.lml.vis.parser.impl.nongwt.ExtendedVisLml
import com.kotcrab.vis.ui.Locales
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.Tooltip
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.file.FileChooser
import gdx.liftoff.views.widgets.ScrollableTextArea

/**
 * Configures Autumn MVC application.
 */
@Component
@Suppress("unused") // Fields accessed via reflection.
class Configuration {
  companion object {
    const val VERSION = "1.12.1.10-PREVIEW-SNAPSHOT"
    const val WIDTH = 600
    const val HEIGHT = 700
    const val PREFERENCES_PATH = "gdx-liftoff-prefs"
  }

  @LmlParserSyntax
  val syntax = VisLmlSyntax()

  @LmlMacro
  val macro = "templates/macros.lml"

  @I18nBundle
  val bundle = "i18n/nls"

  @I18nLocale(propertiesPath = PREFERENCES_PATH, defaultLocale = "en")
  val localePreference = "locale"

  @AvailableLocales
  val availableLocales = arrayOf("en")

  @Preference
  val preferencesPath = PREFERENCES_PATH

  @StageViewport
  val viewportProvider = ObjectProvider<Viewport> { FitViewport(WIDTH.toFloat(), HEIGHT.toFloat()) }

  @Initiate(priority = AutumnActionPriority.TOP_PRIORITY)
  fun initiate(skinService: SkinService, interfaceService: InterfaceService, localeService: LocaleService) {
    VisUI.setSkipGdxVersionCheck(true)
    if (!VisUI.isLoaded()) {
      VisUI.load(Gdx.files.internal("skin/tinted.json"))
    }
    skinService.addSkin("default", VisUI.getSkin())
    FileChooser.setDefaultPrefsName(PREFERENCES_PATH)

    // Adding tags and attributes related to the file chooser:
    ExtendedVisLml.registerFileChooser(syntax)
    ExtendedVisLml.registerFileValidators(syntax)
    // Adding custom ScrollableTextArea widget:
    syntax.addTagProvider(ScrollableTextArea.ScrollableTextAreaLmlTagProvider(), "console")

    // Changing FileChooser locale bundle:
    interfaceService.setActionOnBundlesReload {
      Locales.setFileChooserBundle(localeService.i18nBundle)
    }

    // Adding custom tooltip tag attribute:
    interfaceService.parser.syntax.addAttributeProcessor(
      object : LmlAttribute<Actor> {
        override fun getHandledType(): Class<Actor> = Actor::class.java
        override fun process(parser: LmlParser, tag: LmlTag, actor: Actor, rawAttributeData: String) {
          val tooltip = Tooltip()
          val label = VisLabel(parser.parseString(rawAttributeData, actor), "small")
          label.wrap = true
          tooltip.clear()
          tooltip.add(label).width(200f)
          tooltip.pad(3f)
          tooltip.target = actor
          tooltip.pack()
        }
      },
      "tooltip"
    )
  }

  @Destroy
  fun destroyThreadPool() {
    try {
      threadPool.shutdownNow()
    } catch (exception: Exception) {
      Exceptions.ignore(exception)
    }
  }
}
