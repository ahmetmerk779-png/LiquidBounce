package com.aether.client.config;

import com.aether.client.module.Module;
import com.aether.client.module.ModuleManager;
import com.aether.client.setting.Setting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Moduller ve ayarlar icin tek dosyali JSON config. */
public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path file = FabricLoader.getInstance().getConfigDir().resolve("aether.json");
    private final ModuleManager modules;

    public ConfigManager(ModuleManager modules) {
        this.modules = modules;
    }

    public void load() {
        if (!Files.exists(file)) {
            return;
        }
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            for (Module module : modules.all()) {
                JsonObject node = root.getAsJsonObject(module.getName());
                if (node == null) {
                    continue;
                }
                for (Setting<?> setting : module.getSettings()) {
                    JsonElement el = node.get(setting.getName());
                    if (el != null) {
                        setting.fromJson(el);
                    }
                }
                JsonElement enabled = node.get("enabled");
                if (enabled != null && enabled.isJsonPrimitive()) {
                    module.setEnabled(enabled.getAsBoolean());
                }
            }
        } catch (IOException | RuntimeException e) {
            // Bozuk config oyunu cokertmesin; varsayilanlarla devam et.
            System.err.println("[Aether] Config okunamadi: " + e.getMessage());
        }
    }

    public void save() {
        JsonObject root = new JsonObject();
        for (Module module : modules.all()) {
            JsonObject node = new JsonObject();
            node.addProperty("enabled", module.isEnabled());
            for (Setting<?> setting : module.getSettings()) {
                node.add(setting.getName(), setting.toJson());
            }
            root.add(module.getName(), node);
        }
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            GSON.toJson(root, writer);
        } catch (IOException e) {
            System.err.println("[Aether] Config yazilamadi: " + e.getMessage());
        }
    }
}
