package com.aether.client.module;

import com.aether.client.module.impl.SprintModule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ModuleManager {
    private final Map<String, Module> modules = new LinkedHashMap<>();

    /** Yeni modul eklemek icin tek yapman gereken buraya bir satir eklemek. */
    public void registerAll() {
        register(new SprintModule());
    }

    private void register(Module module) {
        modules.put(module.getName().toLowerCase(Locale.ROOT), module);
    }

    public Module get(String name) {
        return modules.get(name.toLowerCase(Locale.ROOT));
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
}
