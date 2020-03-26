package com.github.czyzby.lml.parser.impl.tag.macro;

import com.badlogic.gdx.Gdx;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.Lml;

/** Logs passed messages with {@link com.badlogic.gdx.Application#error(String, String)}. For example: <blockquote>
 *
 * <pre>
 * &lt;:logError Error: {arg} /&gt;
 * &lt;:logError Error:&gt;{arg}&lt;/:logError&gt;
 * &lt;:logError&gt;Error: {arg}&lt;/:logError&gt;
 * &lt;:logError log="Error: {arg}" /&gt;
 * </pre>
 *
 * </blockquote>All of these macro invocations will log "Error:" string joined with the current value assigned to "arg"
 * argument. This logger can be turned off by setting {@link Lml#ERROR_LOGS_ON} to false.
 *
 * @author MJ */
public class LoggerErrorLmlMacroTag extends AbstractLoggerLmlMacroTag {
    public LoggerErrorLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected void log(final String loggerTag, final String message) {
        Gdx.app.error(loggerTag, message);
    }

    @Override
    protected boolean isOn() {
        return Lml.ERROR_LOGS_ON;
    }
}
