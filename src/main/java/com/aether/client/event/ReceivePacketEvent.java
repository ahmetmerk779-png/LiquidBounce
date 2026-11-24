package com.aether.client.event;

import net.minecraft.network.protocol.Packet;

/** Sunucudan gelen paket. Netty thread'inde yayinlanir; ayni uyari gecerli. */
public final class ReceivePacketEvent extends Event {
    public final Packet<?> packet;

    public ReceivePacketEvent(Packet<?> packet) {
        this.packet = packet;
    }
}
