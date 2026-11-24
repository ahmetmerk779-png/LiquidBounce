package com.aether.client.event;

import net.minecraft.network.protocol.Packet;

/**
 * Sunucuya giden paket. DIKKAT: paketler netty thread'inden de gelebilir; dinleyiciler
 * oyun nesnelerine dokunmadan once veriyi ana thread'e (TickEvent) tasimali.
 */
public final class SendPacketEvent extends Event {
    public final Packet<?> packet;

    public SendPacketEvent(Packet<?> packet) {
        this.packet = packet;
    }
}
