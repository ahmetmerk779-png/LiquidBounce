package com.aether.client.event;

import com.aether.client.AetherClient;
import net.minecraft.network.protocol.Packet;

/** Mixin'lerin cagirdigi tek giris noktasi; mixin sinifi ince kalsin diye. */
public final class PacketHooks {
    private PacketHooks() {}

    /** @return true ise paket iptal edilmeli. */
    public static boolean onSend(Packet<?> packet) {
        EventBus bus = AetherClient.bus();
        if (!bus.hasListeners(SendPacketEvent.class)) {
            return false;
        }
        return bus.post(new SendPacketEvent(packet)).isCancelled();
    }

    /** @return true ise paket islenmeden atilmali. */
    public static boolean onReceive(Packet<?> packet) {
        EventBus bus = AetherClient.bus();
        if (!bus.hasListeners(ReceivePacketEvent.class)) {
            return false;
        }
        return bus.post(new ReceivePacketEvent(packet)).isCancelled();
    }
}
