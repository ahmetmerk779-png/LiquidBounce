package com.aether.client.mixin;

import com.aether.client.event.PacketHooks;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Paket hook'u. require = 0: 1.21.11'de hedef metot bulunamazsa oyun COKMEZ, hook sadece
 * devre disi kalir (.packets sifir gosterir). O durumda latest.log'daki Mixin uyarisini bana at.
 */
@Mixin(Connection.class)
public class ConnectionMixin {

    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;)V",
            at = @At("HEAD"), cancellable = true, require = 0)
    private void aether$onSend(Packet<?> packet, CallbackInfo ci) {
        if (PacketHooks.onSend(packet)) {
            ci.cancel();
        }
    }

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true, require = 0)
    private void aether$onReceive(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
        if (PacketHooks.onReceive(packet)) {
            ci.cancel();
        }
    }
}
