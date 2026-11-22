package com.aether.client.module;

import com.aether.client.AetherClient;
import com.aether.client.event.EventBus;
import com.aether.client.setting.Setting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tum modullerin tabani. Acikken olaylara abone olur, kapaninca abonelikleri
 * otomatik temizlenir; kapali moduller sifir maliyetlidir.
 */
public abstract class Module {
    private final String name;
    private final String description;
    private final Category category;
    private final List<Setting<?>> settings = new ArrayList<>();
    private boolean enabled;

    protected Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    protected final <S extends Setting<?>> S add(S setting) {
        settings.add(setting);
        return setting;
    }

    /** Burada bus().subscribe(...) ile olaylara abone ol. */
    protected void onEnable() {}

    protected void onDisable() {}

    /** HUD'daki aktif modul listesinde gorunsun mu? */
    public boolean isVisibleInList() {
        return true;
    }

    public final void setEnabled(boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }
        this.enabled = enabled;
        AetherClient.modules().markDirty();
        if (enabled) {
            onEnable();
        } else {
            AetherClient.bus().unsubscribeAll(this);
            onDisable();
        }
    }

    public final void toggle() {
        setEnabled(!enabled);
    }

    protected final EventBus bus() {
        return AetherClient.bus();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<Setting<?>> getSettings() {
        return Collections.unmodifiableList(settings);
    }
}
