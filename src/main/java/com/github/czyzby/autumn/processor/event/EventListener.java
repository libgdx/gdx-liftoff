package com.github.czyzby.autumn.processor.event;

/** Utility interface for context event listeners. Instead of annotating methods (turning them into listeners with
 * reflection), this interface might be used to limit reflection-based method invocations. Event listener classes
 * annotated with {@link com.github.czyzby.autumn.annotation.OnEvent} will be normally initiated (as in: their fields
 * can be injected, initiation methods invoked, etc.) and registered as listeners in
 * {@link EventDispatcher}. Since they won't rely on reflection, they are
 * advised to be used for actions invoked very often.
 *
 * @author MJ
 *
 * @param <Event> type of processed event. */
public interface EventListener<Event> {
    /** @param event was just posted. Should be processed. Its type should match type passed in annotation.
     * @return if true, listener will be kept. If false, it will be removed. Overrides settings in
     *         {@link com.github.czyzby.autumn.annotation.OnEvent}.
     * @see com.github.czyzby.autumn.annotation.OnEvent#REMOVE
     * @see com.github.czyzby.autumn.annotation.OnEvent#KEEP */
    boolean processEvent(Event event);
}
