package com.aether.client;

import com.aether.client.command.CommandManager;
import com.aether.client.config.ConfigManager;
import com.aether.client.event.EventBus;
import com.aether.client.event.TickEvent;
import com.aether.client.gui.ClickGuiScreen;
import com.aether.client.hud.HudRenderer;
import com.aether.client.module.ModuleManager;
import com.aether.client.module.impl.HudModule;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

public final class AetherClient implements ClientModInitializer {
    public static final String NAME = "Aether";

    private static final EventBus BUS = new EventBus();
    private static final ModuleManager MODULES = new ModuleManager();
    private static final CommandManager COMMANDS = new CommandManager();
    private static ConfigManager config;
    private static HudModule hud;
    private static boolean guiRequested;

    public static EventBus bus() {
        return BUS;
    }

    public static ModuleManager modules() {
        return MODULES;
    }

    public static ConfigManager config() {
        return config;
    }

    public static HudModule hud() {
        return hud;
    }

    /**
     * Menuyu bir sonraki tick'te acar. Sohbetten komutla acarken dogrudan setScreen
     * cagirmak olmaz, cunku sohbet ekrani mesajdan sonra kendini kapatir.
     */
    public static void requestGui() {
        guiRequested = true;
    }

    @Override
    public void onInitializeClient() {
        MODULES.registerAll();
        hud = (HudModule) MODULES.get("Hud");
        hud.setEnabled(true); // ilk acilis varsayilani; config varsa asagida ustune yazilir

        config = new ConfigManager(MODULES);
        config.load();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            BUS.post(TickEvent.INSTANCE);
            if (guiRequested) {
                guiRequested = false;
                client.setScreen(new ClickGuiScreen());
            }
        });

        // "." ile baslayan mesajlar komut sayilir ve sunucuya gonderilmez.
        ClientSendMessageEvents.ALLOW_CHAT.register(message -> !COMMANDS.handle(message));

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath("aether", "hud"),
                HudRenderer::render);

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> config.save());
    }
}
