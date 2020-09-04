package com.github.czyzby.autumn.processor.event;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.Method;
import com.github.czyzby.autumn.annotation.OnMessage;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.context.error.ContextInitiationException;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;
import com.github.czyzby.autumn.processor.event.impl.ReflectionMessageListener;
import com.github.czyzby.kiwi.util.gdx.collection.lazy.LazyObjectMap;

/** Processes messages. Can be injected and used to invoke registered listeners with {@link #postMessage(String)}. If
 * this processor is not injected into any component, it will mostly likely get garbage-collected after context
 * initiation, even if there are methods or components annotated with {@link OnMessage}.
 *
 * @author MJ */
public class MessageDispatcher extends AbstractAnnotationProcessor<OnMessage> {
    private final ObjectMap<String, ObjectSet<MessageListener>> listeners = LazyObjectMap.newMapOfSets();
    private final ObjectMap<String, ObjectSet<MessageListener>> mainThreadListeners = LazyObjectMap.newMapOfSets();

    @Override
    public Class<OnMessage> getSupportedAnnotationType() {
        return OnMessage.class;
    }

    @Override
    public boolean isSupportingMethods() {
        return true;
    }

    @Override
    public void processMethod(final Method method, final OnMessage annotation, final Object component,
            final Context context, final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        addListener(new ReflectionMessageListener(method, component, context, annotation.removeAfterInvocation(),
                annotation.strict()), annotation);
    }

    @Override
    public boolean isSupportingTypes() {
        return true;
    }

    @Override
    public void processType(final Class<?> type, final OnMessage annotation, final Object component,
            final Context context, final ContextInitializer initializer, final ContextDestroyer contextDestroyer) {
        if (component instanceof MessageListener) {
            addListener((MessageListener) component, annotation);
        } else {
            throw new ContextInitiationException("Unable to register listener. " + component
                    + " is annotated with OnMessage, but does not implement MessageListener interface.");
        }
    }

    /** @param listener will be registered.
     * @param annotation contains listener's data. */
    public void addListener(final MessageListener listener, final OnMessage annotation) {
        addListener(listener, annotation.value(), annotation.forceMainThread());
    }
    /**
     * @param listener will be registered. Invoked as soon as the message is posted.
     * @param messageContent content of handled message. If the message is posted, listener will be invoked.
     */
    public void addListener(final MessageListener listener, final String messageContent) {
        addListener(listener, messageContent, false);
    }

    /**
     * @param listener will be registered.
     * @param messageContent content of handled message. If the message is posted, listener will be invoked.
     * @param forceMainThread if true, listener will be invoked only on main libGDX thread with
     * Gdx.app.postRunnable(Runnable). Otherwise the listener is invoked as soon as the message is posted.
     */
    public void addListener(final MessageListener listener, final String messageContent,
            final boolean forceMainThread) {
        if (forceMainThread) {
            mainThreadListeners.get(messageContent).add(listener);
        } else {
            listeners.get(messageContent).add(listener);
        }
    }

    /**
     * @param listener will be removed (if registered).
     * @param messageContent content of message that the listener is registered to handle.
     */
    public void removeListener(final MessageListener listener, final String messageContent) {
        listeners.get(messageContent).remove(listener);
        mainThreadListeners.get(messageContent).remove(listener);
    }

    /**
     * @param messageContent all listeners registered to handle this message will be removed.
     */
    public void removeListenersForMessage(final String messageContent) {
        listeners.remove(messageContent);
        mainThreadListeners.remove(messageContent);
    }

    /**
     * Removes all registered listeners. Use with care.
     */
    public void clearListeners() {
        listeners.clear();
        mainThreadListeners.clear();
    }

    /** @param message will be posted and invoke all listeners registered to its exact content. Nulls are ignored. */
    public void postMessage(final String message) {
        if (message == null) {
            return;
        }
        if (listeners.containsKey(message)) {
            invokeMessageListeners(listeners.get(message));
        }
        if (mainThreadListeners.containsKey(message)) {
            Gdx.app.postRunnable(new MessageRunnable(mainThreadListeners.get(message)));
        }
    }

    /** @param listeners their message was just posted, so they will be invoked. */
    protected static void invokeMessageListeners(final ObjectSet<MessageListener> listeners) {
        for (final Iterator<MessageListener> iterator = listeners.iterator(); iterator.hasNext();) {
            final MessageListener listener = iterator.next();
            if (!listener.processMessage()) {
                iterator.remove();
            }
        }
    }

    /** Invokes listeners.
     *
     * @author MJ */
    public static class MessageRunnable implements Runnable {
        private final ObjectSet<MessageListener> listeners;

        public MessageRunnable(final ObjectSet<MessageListener> listeners) {
            this.listeners = listeners;
        }

        @Override
        public void run() {
            invokeMessageListeners(listeners);
        }
    }
}
