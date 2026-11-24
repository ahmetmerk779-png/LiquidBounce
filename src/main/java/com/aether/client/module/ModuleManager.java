package com.aether.client.module;

import com.aether.client.module.impl.HudModule;
import com.aether.client.module.impl.PacketLoggerModule;
import com.aether.client.module.impl.SetbackGuardModule;
import com.aether.client.module.impl.SprintModule;
import com.aether.client.util.Names;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

public final class ModuleManager {
    private final Map<String, Module> modules = new LinkedHashMap<>();

    // HUD listesi onbellegi: sadece bir modul acilip kapanınca yeniden siralanir.
    private List<Module> enabledCache = List.of();
    private boolean dirty = true;

    /** Yeni modul eklemek icin tek yapman gereken buraya bir satir eklemek. */
    public void registerAll() {
        register(new HudModule());
        register(new SprintModule());
        register(new PacketLoggerModule());
        register(new SetbackGuardModule());
    }

    private void register(Module module) {
        modules.put(Names.norm(module.getName()), module);
    }

    public Module get(String name) {
        return modules.get(Names.norm(name));
    }

    public List<Module> all() {
        return Collections.unmodifiableList(new ArrayList<>(modules.values()));
    }

    public List<Module> byCategory(Category category) {
        List<Module> out = new ArrayList<>();
        for (Module m : modules.values()) {
            if (m.getCategory() == category) {
                out.add(m);
            }
        }
        return out;
    }

    public void markDirty() {
        dirty = true;
    }

    /** Acik ve listede gorunur moduller, en genis isim ustte. Cizim basina allocation yok. */
    public List<Module> enabledByWidth(ToIntFunction<String> width) {
        if (dirty) {
            List<Module> list = new ArrayList<>();
            for (Module m : modules.values()) {
                if (m.isEnabled() && m.isVisibleInList()) {
                    list.add(m);
                }
            }
            list.sort(Comparator.comparingInt((Module m) -> width.applyAsInt(m.getName())).reversed());
            enabledCache = list;
            dirty = false;
        }
        return enabledCache;
    }
}
