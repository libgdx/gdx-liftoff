package com.github.czyzby.lml.parser.impl.tag.macro;

import com.badlogic.gdx.Gdx;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.Lml;

/** Logs passed messages with {@link com.badlogic.gdx.Application#debug(String, String)}. For example: <blockquote>
 *
 * <pre>
 * &lt;:debug Debug: {arg} /&gt;
 * &lt;:debug Debug:&gt;{arg}&lt;/:debug&gt;
 * &lt;:debug&gt;Debug: {arg}&lt;/:debug&gt;
 * &lt;:debug log="Debug: {arg}" /&gt;
 * </pre>
 *
 * </blockquote>All of these macro invocations will log "Debug:" string joined with the current value assigned to "arg"
 * argument. This logger can be turned off by setting {@link Lml#DEBUG_LOGS_ON} to false.
 *
 * @author MJ */
public class LoggerDebugLmlMacroTag extends AbstractLoggerLmlMacroTag {
    public LoggerDebugLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected void log(final String loggerTag, final String message) {
        Gdx.app.debug(loggerTag, message);
    }

    @Override
    protected boolean isOn() {
        return Lml.DEBUG_LOGS_ON;
    }
}
