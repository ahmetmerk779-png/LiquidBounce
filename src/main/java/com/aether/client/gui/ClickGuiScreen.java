package com.aether.client.gui;

import com.aether.client.AetherClient;
import com.aether.client.module.Category;
import com.aether.client.module.Module;
import com.aether.client.setting.BoolSetting;
import com.aether.client.setting.EnumSetting;
import com.aether.client.setting.NumberSetting;
import com.aether.client.setting.Setting;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Dokunmatik dostu menu: tek sutun, buyuk butonlar, sayfa dugmeleri, sayi ayari icin -/+.
 * Surukleme veya ozel fare olayi kullanmaz; sadece Button bilesenleri vardir. Bu hem
 * telefonda guvenilir calisir hem de surume bagli giris API'lerinden bagimsizdir.
 */
public final class ClickGuiScreen extends Screen {
    private static final int BTN_H = 20;
    private static final int ROW_H = 24;
    private static final int RESERVED_BOTTOM = 44;

    private Category category = Category.MOVEMENT;
    private Module selected;
    private int page;
    private boolean rebuildRequested;
    private String pageLabel = "";
    private String hint = "";
    private int hintY;

    public ClickGuiScreen() {
        super(Component.literal(AetherClient.NAME));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /** Widget listesi tiklama sirasinda degismesin diye yeniden kurma bir sonraki tick'te yapilir. */
    @Override
    public void tick() {
        if (rebuildRequested) {
            rebuildRequested = false;
            rebuildWidgets();
        }
    }

    private void requestRebuild() {
        rebuildRequested = true;
    }

    @Override
    protected void init() {
        int panelW = Math.min(width - 16, 260);
        int left = (width - panelW) / 2;
        hint = "";

        int top;
        int total;
        if (selected == null) {
            top = addCategoryTabs(left, panelW);
            List<Module> mods = AetherClient.modules().byCategory(category);
            total = mods.size();
            hintY = top + 4;
            if (total == 0) {
                hint = "Bu kategoride henuz modul yok.";
            }
            int[] range = pageRange(top, total);
            for (int i = range[0]; i < range[1]; i++) {
                addModuleRow(mods.get(i), left, panelW, top + (i - range[0]) * ROW_H);
            }
        } else {
            addButton(Component.literal("< Geri"), left, 8, panelW, () -> {
                selected = null;
                page = 0;
                requestRebuild();
            });
            addModuleToggle(selected, left, 32, panelW);
            top = 60;
            List<Setting<?>> settings = selected.getSettings();
            total = settings.size();
            hintY = top + 4;
            if (total == 0) {
                hint = "Bu modulun ayari yok.";
            }
            int[] range = pageRange(top, total);
            for (int i = range[0]; i < range[1]; i++) {
                addSettingRow(settings.get(i), left, panelW, top + (i - range[0]) * ROW_H);
            }
        }
        addFooter(left, panelW);
    }

    /** Kategori sekmeleri: 3'erli iki satir (dar telefon ekranlarina sigsin diye). */
    private int addCategoryTabs(int left, int panelW) {
        Category[] cats = Category.values();
        int perRow = 3;
        int w = panelW / perRow;
        for (int i = 0; i < cats.length; i++) {
            Category c = cats[i];
            int x = left + (i % perRow) * w;
            int y = 8 + (i / perRow) * ROW_H;
            Component label = Component.literal(c.getDisplayName());
            if (c == category) {
                label = Component.literal(c.getDisplayName()).withStyle(ChatFormatting.YELLOW);
            }
            addButton(label, x, y, w - 2, () -> {
                category = c;
                page = 0;
                requestRebuild();
            });
        }
        int rows = (cats.length + perRow - 1) / perRow;
        return 8 + rows * ROW_H + 4;
    }

    private int[] pageRange(int top, int total) {
        int perPage = Math.max(1, (height - top - RESERVED_BOTTOM) / ROW_H);
        int pages = Math.max(1, (total + perPage - 1) / perPage);
        page = Math.max(0, Math.min(page, pages - 1));
        pageLabel = (page + 1) + " / " + pages;
        int start = page * perPage;
        return new int[] {start, Math.min(total, start + perPage)};
    }

    private void addModuleRow(Module m, int left, int panelW, int y) {
        addModuleToggle(m, left, y, panelW - 24);
        Button open = addButton(Component.literal(">"), left + panelW - 20, y, 20, () -> {
            selected = m;
            page = 0;
            requestRebuild();
        });
        open.active = !m.getSettings().isEmpty();
    }

    private void addModuleToggle(Module m, int x, int y, int w) {
        Button[] ref = new Button[1];
        ref[0] = addButton(moduleLabel(m), x, y, w, () -> {
            m.toggle();
            ref[0].setMessage(moduleLabel(m));
        });
    }

    private static Component moduleLabel(Module m) {
        ChatFormatting color = m.isEnabled() ? ChatFormatting.GREEN : ChatFormatting.RED;
        return Component.literal(m.getName() + "  ")
                .append(Component.literal(m.isEnabled() ? "ON" : "OFF").withStyle(color));
    }

    private void addSettingRow(Setting<?> s, int left, int panelW, int y) {
        if (s instanceof BoolSetting b) {
            Button[] ref = new Button[1];
            ref[0] = addButton(settingLabel(b), left, y, panelW, () -> {
                b.toggle();
                ref[0].setMessage(settingLabel(b));
            });
        } else if (s instanceof EnumSetting<?> e) {
            Button[] ref = new Button[1];
            ref[0] = addButton(settingLabel(e), left, y, panelW, () -> {
                e.cycle();
                ref[0].setMessage(settingLabel(e));
            });
        } else if (s instanceof NumberSetting n) {
            Button[] ref = new Button[1];
            addButton(Component.literal("-"), left, y, 20, () -> {
                n.increment(-1);
                ref[0].setMessage(settingLabel(n));
            });
            ref[0] = addButton(settingLabel(n), left + 22, y, panelW - 44, () -> {});
            ref[0].active = false;
            addButton(Component.literal("+"), left + panelW - 20, y, 20, () -> {
                n.increment(1);
                ref[0].setMessage(settingLabel(n));
            });
        }
    }

    private static Component settingLabel(Setting<?> s) {
        return Component.literal(s.getName() + ": " + s.display());
    }

    private void addFooter(int left, int panelW) {
        int y = height - 26;
        int w = panelW / 3;
        addButton(Component.literal("< Onceki"), left, y, w - 2, () -> {
            page--;
            requestRebuild();
        });
        addButton(Component.literal("Kapat"), left + w, y, w - 2, this::onClose);
        addButton(Component.literal("Sonraki >"), left + 2 * w, y, w - 2, () -> {
            page++;
            requestRebuild();
        });
    }

    private Button addButton(Component message, int x, int y, int w, Runnable action) {
        Button button = Button.builder(message, b -> action.run()).bounds(x, y, w, BTN_H).build();
        addRenderableWidget(button);
        return button;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);
        int labelWidth = font.width(pageLabel);
        graphics.drawString(font, pageLabel, (width - labelWidth) / 2, height - 38, 0xFFAAAAAA, true);
        if (!hint.isEmpty()) {
            int hintWidth = font.width(hint);
            graphics.drawString(font, hint, (width - hintWidth) / 2, hintY, 0xFFAAAAAA, true);
        }
    }
}
