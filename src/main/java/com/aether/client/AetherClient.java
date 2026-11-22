package com.aether.client;

import com.aether.client.config.ConfigManager;
import com.aether.client.event.EventBus;
import com.aether.client.event.TickEvent;
import com.aether.client.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public final class AetherClient implements ClientModInitializer {
    public static final String NAME = "Aether";

    private static final EventBus BUS = new EventBus();
    private static final ModuleManager MODULES = new ModuleManager();
    private static ConfigManager config;

    public static EventBus bus() {
        return BUS;
    }

    public static ModuleManager modules() {
        return MODULES;
    }

    public static ConfigManager config() {
        return config;
    }

    @Override
    public void onInitializeClient() {
        MODULES.registerAll();
        config = new ConfigManager(MODULES);
        config.load();

        // Mixin yok: Faz 1 sadece Fabric API tick olayina baglanir.
        ClientTickEvents.END_CLIENT_TICK.register(client -> BUS.post(TickEvent.INSTANCE));
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> config.save());
    }
}
