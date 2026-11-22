package com.aether.client.event;

/** Her client tick sonunda yayinlanir. Tek ornek yeniden kullanilir (allocation yok). */
public final class TickEvent extends Event {
    public static final TickEvent INSTANCE = new TickEvent();

    private TickEvent() {}
}
