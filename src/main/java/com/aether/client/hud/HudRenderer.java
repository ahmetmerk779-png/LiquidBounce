package com.aether.client.hud;

import com.aether.client.AetherClient;
import com.aether.client.module.Module;
import com.aether.client.module.impl.HudModule;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/** Watermark ve aktif modul listesi. Liste onbellekli, cizimde allocation yok. */
public final class HudRenderer {
    private HudRenderer() {}

    public static void render(GuiGraphics graphics, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null) {
            return;
        }
        HudModule hud = AetherClient.hud();
        if (hud == null || !hud.isEnabled()) {
            return;
        }
        Font font = mc.font;

        if (hud.watermark.get()) {
            graphics.drawString(font, AetherClient.NAME, 4, 4, 0xFF55FFFF, true);
        }

        if (hud.moduleList.get()) {
            int screenWidth = mc.getWindow().getGuiScaledWidth();
            int y = 4;
            for (Module m : AetherClient.modules().enabledByWidth(font::width)) {
                String name = m.getName();
                graphics.drawString(font, name, screenWidth - font.width(name) - 4, y, 0xFFFFFFFF, true);
                y += font.lineHeight + 1;
            }
        }
    }
}
