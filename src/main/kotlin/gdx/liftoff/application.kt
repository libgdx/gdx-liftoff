package gdx.liftoff

import java.io.PrintWriter
import java.io.StringWriter

fun Throwable.stackTraceToString(): String {
    val sw = StringWriter()
    val pw = PrintWriter(sw, true)
    this.printStackTrace(pw)
    return sw.buffer.toString()
}
