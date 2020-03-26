package com.github.czyzby.autumn.processor.event;

/** Utility interface for context message listeners. Instead of annotating methods (turning them into listeners with
 * reflection), this interface might be used to limit reflection-based method invocations. Message listener classes
 * annotated with {@link com.github.czyzby.autumn.annotation.OnMessage} will be normally initiated (as in: their fields
 * can be injected, initiation methods invoked, etc.) and registered as listeners in
 * {@link MessageDispatcher}. Since they won't rely on reflection, they are
 * advised to be used for actions invoked very often.
 *
 * @author MJ */
public interface MessageListener {
    /** Listener's message was just posted. This method will be fired each time the message occurs, unless it is
     * removed.
     *
     * @return true if the listener should be kept, false if removed. Overrides settings in
     *         {@link com.github.czyzby.autumn.annotation.OnMessage}.
     * @see com.github.czyzby.autumn.annotation.OnMessage#REMOVE
     * @see com.github.czyzby.autumn.annotation.OnMessage#KEEP */
    boolean processMessage();
}
