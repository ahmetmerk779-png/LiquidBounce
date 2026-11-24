package com.aether.client.module.impl;

import com.aether.client.AetherClient;
import com.aether.client.event.ReceivePacketEvent;
import com.aether.client.event.TickEvent;
import com.aether.client.module.Category;
import com.aether.client.module.Module;
import com.aether.client.setting.BoolSetting;
import com.aether.client.setting.NumberSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;

import java.util.ArrayDeque;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Sunucu seni arka arkaya geri cekiyorsa (rubberband) hareket/dovus modullerini kapatir.
 * Amac kararlilik: sonsuz geri cekilme dongusune girmemek. Sunucu /tp gibi normal
 * isinlanmalar da sayilir, bu yuzden esik makul tutuldu.
 */
public final class SetbackGuardModule extends Module {
    private final BoolSetting autoDisable = add(new BoolSetting("AutoDisable", true));
    private final NumberSetting threshold = add(new NumberSetting("Threshold", 3, 1, 10, 1));
    private final NumberSetting windowSeconds = add(new NumberSetting("WindowSeconds", 5, 1, 30, 1));

    // Paket thread'i sadece sayar; asil islem ana thread'de (tick) yapilir.
    private final AtomicInteger pending = new AtomicInteger();
    private final ArrayDeque<Integer> stamps = new ArrayDeque<>();
    private int tickCounter;

    public SetbackGuardModule() {
        super("SetbackGuard", "Ust uste geri cekilmede hareket/dovus modullerini kapatir.", Category.MISC);
    }

    @Override
    protected void onEnable() {
        pending.set(0);
        stamps.clear();
        bus().subscribe(ReceivePacketEvent.class, this, e -> {
            if (e.packet instanceof ClientboundPlayerPositionPacket) {
                pending.incrementAndGet();
            }
        });
        bus().subscribe(TickEvent.class, this, e -> onTick());
    }

    private void onTick() {
        tickCounter++;
        int n = pending.getAndSet(0);
        for (int i = 0; i < n; i++) {
            stamps.addLast(tickCounter);
        }
        int windowTicks = windowSeconds.getInt() * 20;
        while (!stamps.isEmpty() && tickCounter - stamps.peekFirst() > windowTicks) {
            stamps.removeFirst();
        }
        if (autoDisable.get() && stamps.size() >= threshold.getInt()) {
            stamps.clear();
            disableRisky();
        }
    }

    private void disableRisky() {
        int count = 0;
        for (Category category : List.of(Category.MOVEMENT, Category.COMBAT)) {
            for (Module m : AetherClient.modules().byCategory(category)) {
                if (m.isEnabled()) {
                    m.setEnabled(false);
                    count++;
                }
            }
        }
        Minecraft mc = Minecraft.getInstance();
        if (count > 0 && mc.player != null) {
            mc.player.displayClientMessage(Component.literal(
                    "[" + AetherClient.NAME + "] Ust uste geri cekilme: " + count + " modul kapatildi."), false);
        }
    }
}
