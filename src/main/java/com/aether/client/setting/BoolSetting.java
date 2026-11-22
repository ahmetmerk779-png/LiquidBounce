package com.aether.client.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.Locale;

public final class BoolSetting extends Setting<Boolean> {

    public BoolSetting(String name, boolean defaultValue) {
        super(name, defaultValue);
    }

    public void toggle() {
        value = !value;
    }

    @Override
    public String display() {
        return value ? "ON" : "OFF";
    }

    @Override
    public boolean setFromString(String text) {
        String t = text.toLowerCase(Locale.ROOT);
        if (t.equals("true") || t.equals("on") || t.equals("1")) {
            value = true;
            return true;
        }
        if (t.equals("false") || t.equals("off") || t.equals("0")) {
            value = false;
            return true;
        }
        if (t.equals("toggle")) {
            toggle();
            return true;
        }
        return false;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(value);
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
            value = element.getAsBoolean();
        }
    }
}
