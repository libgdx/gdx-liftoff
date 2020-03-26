package com.github.czyzby.autumn.context.error;

import com.badlogic.gdx.utils.GdxRuntimeException;

/** Thrown when unable to initiate Autumn context.
 *
 * @author MJ */
public class ContextInitiationException extends GdxRuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_MESSAGE = "Unable to initiate context.";

    public ContextInitiationException() {
        super(DEFAULT_MESSAGE);
    }

    public ContextInitiationException(final String message) {
        super(message);
    }

    public ContextInitiationException(final Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }

    public ContextInitiationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
