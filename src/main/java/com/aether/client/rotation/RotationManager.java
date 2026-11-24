package com.aether.client.rotation;

import com.aether.client.AetherClient;
import com.aether.client.event.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

/**
 * Merkezi bakis yonu yoneticisi. Moduller dogrudan yaw/pitch degistirmek yerine buradan
 * istek yapar; en yuksek oncelikli istek kazanir, ayni anda iki modul kavga etmez.
 *
 * Istek her tick yenilenmezse 1 tick sonra duser (modul kapaninca takili kalmaz).
 * Hold istekleri hedefe varana kadar kendini yeniler. Donus hizi maxStep ile sinirlidir.
 */
public final class RotationManager {
    private static final float REACHED = 0.5f;

    private Object owner;
    private Rotation target;
    private int priority;
    private float maxStep;
    private boolean hold;
    private int ttl;

    public void init() {
        // Moduller (oncelik 0) bu tick'te istegini yaptiktan SONRA uygulansin diye dusuk oncelik.
        AetherClient.bus().subscribe(TickEvent.class, this, -100, e -> onTick());
    }

    public void request(Object requester, Rotation wanted, int requestPriority, float step) {
        accept(requester, wanted, requestPriority, step, false);
    }

    /** Hedefe varana kadar tutar (komutlar icin). */
    public void requestHold(Object requester, Rotation wanted, int requestPriority, float step) {
        accept(requester, wanted, requestPriority, step, true);
    }

    private void accept(Object requester, Rotation wanted, int requestPriority, float step, boolean holdUntilReached) {
        if (ttl > 0 && owner != requester && requestPriority < priority) {
            return;
        }
        owner = requester;
        target = wanted;
        priority = requestPriority;
        maxStep = Math.max(1.0f, step);
        hold = holdUntilReached;
        ttl = 1;
    }

    public void release(Object requester) {
        if (owner == requester) {
            clear();
        }
    }

    private void clear() {
        owner = null;
        target = null;
        hold = false;
        ttl = 0;
    }

    private void onTick() {
        if (ttl <= 0 || target == null) {
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            clear();
            return;
        }
        Rotation current = new Rotation(player.getYRot(), player.getXRot());
        Rotation next = current.stepToward(target, maxStep);
        player.setYRot(next.yaw());
        player.setXRot(next.pitch());

        if (hold && next.distanceTo(target) > REACHED) {
            ttl = 1; // hedefe varana kadar yenile
        } else {
            ttl--;
            if (ttl <= 0) {
                clear();
            }
        }
    }
}
