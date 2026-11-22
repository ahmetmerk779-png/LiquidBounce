package com.aether.client.module.impl;

import com.aether.client.module.Category;
import com.aether.client.module.Module;
import com.aether.client.setting.BoolSetting;

/** HUD ayarlari. Ilk acilista aciktir (AetherClient icinde). */
public final class HudModule extends Module {
    public final BoolSetting watermark = add(new BoolSetting("Watermark", true));
    public final BoolSetting moduleList = add(new BoolSetting("ModuleList", true));

    public HudModule() {
        super("Hud", "Ekran ustu bilgi ve aktif modul listesi.", Category.RENDER);
    }

    @Override
    public boolean isVisibleInList() {
        return false;
    }
}
