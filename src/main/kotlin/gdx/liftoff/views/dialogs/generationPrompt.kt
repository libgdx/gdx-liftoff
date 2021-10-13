package gdx.liftoff.views.dialogs

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.utils.ObjectSet
import com.github.czyzby.autumn.annotation.Destroy
import com.github.czyzby.autumn.annotation.Inject
import com.github.czyzby.autumn.mvc.component.i18n.LocaleService
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewDialogShower
import com.github.czyzby.autumn.mvc.stereotype.ViewDialog
import com.github.czyzby.kiwi.util.common.Exceptions
import com.github.czyzby.lml.annotation.LmlActor
import gdx.liftoff.data.project.ProjectLogger
import gdx.liftoff.views.MainView
import gdx.liftoff.views.widgets.ScrollableTextArea
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicLong

/**
 * Displayed after generation request was sent.
 */
@ViewDialog(id = "generation", value = "templates/dialogs/generation.lml", cacheInstance = false)
@Suppress("unused") // Referenced via reflection.
class GenerationPrompt : ViewDialogShower, ProjectLogger {
    @Inject private lateinit var locale: LocaleService
    @Inject private lateinit var mainView: MainView

    @LmlActor("close", "exit") private lateinit var buttons: ObjectSet<Button>
    @LmlActor("console") private lateinit var console: ScrollableTextArea
    @LmlActor("scroll") private lateinit var scrollPane: ScrollPane

    private val executor = Executors.newSingleThreadExecutor(PrefixedThreadFactory("ProjectGenerator"))
    private val loggingBuffer = ConcurrentLinkedQueue<String>()

    override fun doBeforeShow(dialog: Window) {
        dialog.invalidate()
        executor.execute {
            try {
                logNls("copyStart")
                val project = mainView.createProject()
                project.generate()
                logNls("copyEnd")
                mainView.revalidateForm()
                project.includeGradleWrapper(this)
                logNls("generationEnd")
            } catch (exception: Exception) {
                log(exception.javaClass.name + ": " + exception.message)
                exception.stackTrace.forEach { log("  at $it") }
                exception.printStackTrace()
                logNls("generationFail")
            } finally {
                buttons.forEach { it.isDisabled = false }
            }
        }
    }

    override fun logNls(bundleLine: String) = log(locale.i18nBundle.get(bundleLine))
    override fun log(message: String) {
        loggingBuffer.offer(message)
        Gdx.app.postRunnable {
            while (loggingBuffer.isNotEmpty()) {
                if (console.text.isNotBlank()) console.text += '\n'
                console.text += loggingBuffer.poll()
            }
            Gdx.app.postRunnable {
                console.invalidateHierarchy()
                scrollPane.layout()
                scrollPane.scrollPercentY = 1f
            }
        }
    }

    @Destroy
    fun shutdownExecutor() {
        try {
            executor.shutdownNow()
        } catch (exception: Exception) {
            Exceptions.ignore(exception)
        }
    }
}

/**
 * Generates sane thread names for [Executors].
 * @author Kotcrab
 */
private class PrefixedThreadFactory(threadPrefix: String) : ThreadFactory {
    private val count = AtomicLong(0)
    private val threadPrefix: String

    init {
        this.threadPrefix = "$threadPrefix-"
    }

    override fun newThread(runnable: Runnable): Thread {
        val thread = Executors.defaultThreadFactory().newThread(runnable)
        thread.name = threadPrefix + count.andIncrement
        return thread
    }
}
