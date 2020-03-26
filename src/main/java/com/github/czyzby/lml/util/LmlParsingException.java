package com.github.czyzby.lml.util;

import com.badlogic.gdx.utils.GdxRuntimeException;

/** Thrown when unable to properly parse passed LML template.
 *
 * @author MJ */
public class LmlParsingException extends GdxRuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_MESSAGE = "Unable to parse LML template.";

    public LmlParsingException() {
        super(DEFAULT_MESSAGE);
    }

    public LmlParsingException(final String message) {
        super(message);
    }

    public LmlParsingException(final Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }

    public LmlParsingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
