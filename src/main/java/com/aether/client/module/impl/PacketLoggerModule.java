package com.aether.client.module.impl;

import com.aether.client.event.ReceivePacketEvent;
import com.aether.client.event.SendPacketEvent;
import com.aether.client.module.Category;
import com.aether.client.module.Module;
import com.aether.client.setting.BoolSetting;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * Gelistirme araci: giden/gelen paket turlerini sayar, istenirse dosyaya yazar.
 * Paketler netty thread'inden gelir; sayaclar thread-safe, dosya yazimi senkronize.
 * Telefon depolamasini korumak icin dosya satir sayisi sinirlidir.
 */
public final class PacketLoggerModule extends Module {
    private static final int MAX_FILE_LINES = 20_000;

    private final BoolSetting toFile = add(new BoolSetting("ToFile", false));

    private final Map<String, LongAdder> sent = new ConcurrentHashMap<>();
    private final Map<String, LongAdder> received = new ConcurrentHashMap<>();

    private final Object fileLock = new Object();
    private BufferedWriter writer;
    private int lines;

    public PacketLoggerModule() {
        super("PacketLogger", "Paket turlerini sayar (.packets ile goruntule).", Category.MISC);
    }

    @Override
    protected void onEnable() {
        if (toFile.get()) {
            openFile();
        }
        bus().subscribe(SendPacketEvent.class, this, e -> record(">", sent, e.packet.getClass().getSimpleName()));
        bus().subscribe(ReceivePacketEvent.class, this, e -> record("<", received, e.packet.getClass().getSimpleName()));
    }

    @Override
    protected void onDisable() {
        closeFile();
    }

    private void record(String direction, Map<String, LongAdder> counters, String name) {
        counters.computeIfAbsent(name, k -> new LongAdder()).increment();
        synchronized (fileLock) {
            if (writer != null && lines < MAX_FILE_LINES) {
                try {
                    writer.write(System.currentTimeMillis() + " " + direction + " " + name);
                    writer.newLine();
                    lines++;
                } catch (IOException ignored) {
                    closeFile();
                }
            }
        }
    }

    private void openFile() {
        synchronized (fileLock) {
            try {
                Path path = FabricLoader.getInstance().getGameDir().resolve("aether-packets.log");
                writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
                lines = 0;
            } catch (IOException e) {
                writer = null;
            }
        }
    }

    private void closeFile() {
        synchronized (fileLock) {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {
                    // kapanirken hata onemsiz
                }
                writer = null;
            }
        }
    }

    public void reset() {
        sent.clear();
        received.clear();
    }

    /** Her yon icin en cok gorulen 8 paket turu. */
    public List<String> summary() {
        List<String> out = new ArrayList<>();
        appendTop(out, "Giden", sent);
        appendTop(out, "Gelen", received);
        return out;
    }

    private static void appendTop(List<String> out, String title, Map<String, LongAdder> counters) {
        List<Map.Entry<String, LongAdder>> entries = new ArrayList<>(counters.entrySet());
        entries.sort((a, b) -> Long.compare(b.getValue().sum(), a.getValue().sum()));
        long total = 0;
        for (Map.Entry<String, LongAdder> e : entries) {
            total += e.getValue().sum();
        }
        out.add(title + " (toplam " + total + "):");
        for (int i = 0; i < Math.min(8, entries.size()); i++) {
            out.add("  " + entries.get(i).getKey() + " x" + entries.get(i).getValue().sum());
        }
    }
}
