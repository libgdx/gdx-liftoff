package com.github.czyzby.lml.parser.impl.tag.macro;

import com.badlogic.gdx.Gdx;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.Lml;

/** Logs passed messages with {@link com.badlogic.gdx.Application#log(String, String)}. For example: <blockquote>
 *
 * <pre>
 * &lt;:log Info: {arg} /&gt;
 * &lt;:log Info:&gt;{arg}&lt;/:log&gt;
 * &lt;:log&gt;Info: {arg}&lt;/:log&gt;
 * &lt;:log log="Info: {arg}" /&gt;
 * </pre>
 *
 * </blockquote>All of these macro invocations will log "Info:" string joined with the current value assigned to "arg"
 * argument. This logger can be turned off by setting {@link Lml#INFO_LOGS_ON} to false.
 *
 * @author MJ */
public class LoggerInfoLmlMacroTag extends AbstractLoggerLmlMacroTag {
    public LoggerInfoLmlMacroTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected void log(final String loggerTag, final String message) {
        Gdx.app.log(loggerTag, message);
    }

    @Override
    protected boolean isOn() {
        return Lml.INFO_LOGS_ON;
    }
}
