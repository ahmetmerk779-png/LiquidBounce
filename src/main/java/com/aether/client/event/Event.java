package com.aether.client.event;

/**
 * Tum olaylarin tabani. Iptal edilebilir olaylar icin cancelled bayragi vardir.
 */
public abstract class Event {
    private boolean cancelled;

    public boolean isCancelled() {
        return cancelled;
    }

    public void cancel() {
        this.cancelled = true;
    }

    /** Olay nesneleri yeniden kullanildigi icin her post oncesi sifirlanir. */
    final void reset() {
        this.cancelled = false;
    }
}
