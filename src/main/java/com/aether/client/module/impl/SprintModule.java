package com.aether.client.module.impl;

import com.aether.client.event.TickEvent;
import com.aether.client.module.Category;
import com.aether.client.module.Module;
import com.aether.client.setting.BoolSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

/**
 * Ornek modul: iskeletin uctan uca calistigini kanitlar.
 * Mojang mappings kullanilir (Minecraft, LocalPlayer).
 */
public final class SprintModule extends Module {
    private final BoolSetting keepWhenHungry = add(new BoolSetting("Aclikta Da", false));

    public SprintModule() {
        super("Sprint", "Ileri giderken otomatik kosar.", Category.MOVEMENT);
    }

    @Override
    protected void onEnable() {
        bus().subscribe(TickEvent.class, this, e -> onTick());
    }

    private void onTick() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) {
            return;
        }
        boolean forward = mc.options.keyUp.isDown();
        boolean fedEnough = keepWhenHungry.get() || player.getFoodData().getFoodLevel() > 6;
        if (forward && fedEnough && !player.isShiftKeyDown() && !player.horizontalCollision) {
            player.setSprinting(true);
        }
    }
}
