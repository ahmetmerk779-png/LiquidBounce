package com.aether.client.event;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Hafif olay yolu. Yansima (reflection) kullanmaz, post sirasinda nesne uretmez;
 * telefonda GC baskisini dusuk tutmak icin bilincli olarak sade tutuldu.
 */
public final class EventBus {

    private record Listener<T>(Object owner, int priority, Consumer<T> handler) {}

    private final Map<Class<?>, CopyOnWriteArrayList<Listener<?>>> listeners = new ConcurrentHashMap<>();

    public <T extends Event> void subscribe(Class<T> type, Object owner, Consumer<T> handler) {
        subscribe(type, owner, 0, handler);
    }

    /** Yuksek priority once calisir. */
    public <T extends Event> void subscribe(Class<T> type, Object owner, int priority, Consumer<T> handler) {
        CopyOnWriteArrayList<Listener<?>> list =
                listeners.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>());
        list.add(new Listener<>(owner, priority, handler));
        list.sort(Comparator.comparingInt((Listener<?> l) -> l.priority()).reversed());
    }

    public void unsubscribeAll(Object owner) {
        for (List<Listener<?>> list : listeners.values()) {
            list.removeIf(l -> l.owner() == owner);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> T post(T event) {
        List<Listener<?>> list = listeners.get(event.getClass());
        if (list == null || list.isEmpty()) {
            return event;
        }
        event.reset();
        for (Listener<?> l : list) {
            ((Listener<T>) l).handler().accept(event);
        }
        return event;
    }
}
