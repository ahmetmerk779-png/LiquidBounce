package com.aether.client.command;

import com.aether.client.AetherClient;
import com.aether.client.module.Category;
import com.aether.client.module.Module;
import com.aether.client.setting.Setting;
import com.aether.client.util.Names;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Locale;

/**
 * Sohbetten prefix'li komutlar (varsayilan "."). Mixin gerektirmez; Fabric API'nin
 * ClientSendMessageEvents olayina AetherClient icinden baglanir.
 * Normal sohbette "." ile baslayan bir mesaj gondermek icin ".." ile basla.
 */
public final class CommandManager {
    public static final String PREFIX = ".";

    /** @return true ise mesaj komut olarak islendi ve sunucuya gitmemeli. */
    public boolean handle(String message) {
        if (!message.startsWith(PREFIX) || message.length() == 1 || message.startsWith(PREFIX + PREFIX)) {
            return false;
        }
        String[] args = message.substring(1).trim().split("\\s+");
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "help", "?" -> help();
            case "list" -> list();
            case "toggle", "t" -> toggle(args);
            case "set" -> set(args);
            case "gui" -> AetherClient.requestGui();
            case "save" -> {
                AetherClient.config().save();
                reply("Config kaydedildi.");
            }
            default -> reply("Bilinmeyen komut. " + PREFIX + "help yaz.");
        }
        return true;
    }

    private void help() {
        reply(PREFIX + "gui                          dokunmatik menuyu acar");
        reply(PREFIX + "list                         modulleri listeler (+ acik)");
        reply(PREFIX + "toggle <modul>               modulu acar/kapatir");
        reply(PREFIX + "set <modul> <ayar> [deger]   ayari gosterir/degistirir");
        reply(PREFIX + "save                         config'i kaydeder");
    }

    private void list() {
        for (Category c : Category.values()) {
            List<Module> mods = AetherClient.modules().byCategory(c);
            if (mods.isEmpty()) {
                continue;
            }
            StringBuilder sb = new StringBuilder(c.getDisplayName()).append(": ");
            for (Module m : mods) {
                sb.append(m.isEnabled() ? "+" : "").append(m.getName()).append("  ");
            }
            reply(sb.toString());
        }
    }

    private void toggle(String[] args) {
        if (args.length < 2) {
            reply("Kullanim: " + PREFIX + "toggle <modul>");
            return;
        }
        Module m = AetherClient.modules().get(args[1]);
        if (m == null) {
            reply("Modul bulunamadi: " + args[1]);
            return;
        }
        m.toggle();
        reply(m.getName() + (m.isEnabled() ? " acildi." : " kapatildi."));
    }

    private void set(String[] args) {
        if (args.length < 3) {
            reply("Kullanim: " + PREFIX + "set <modul> <ayar> [deger]");
            return;
        }
        Module m = AetherClient.modules().get(args[1]);
        if (m == null) {
            reply("Modul bulunamadi: " + args[1]);
            return;
        }
        Setting<?> setting = null;
        for (Setting<?> s : m.getSettings()) {
            if (Names.norm(s.getName()).equals(Names.norm(args[2]))) {
                setting = s;
                break;
            }
        }
        if (setting == null) {
            reply("Ayar bulunamadi: " + args[2]);
            return;
        }
        if (args.length == 3) {
            reply(setting.getName() + " = " + setting.display());
            return;
        }
        if (setting.setFromString(args[3])) {
            reply(setting.getName() + " = " + setting.display());
        } else {
            reply("Gecersiz deger: " + args[3]);
        }
    }

    private static void reply(String text) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.displayClientMessage(Component.literal("[" + AetherClient.NAME + "] " + text), false);
        }
    }
}
