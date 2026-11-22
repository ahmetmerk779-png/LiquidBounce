package com.aether.client.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public final class EnumSetting<E extends Enum<E>> extends Setting<E> {
    private final E[] constants;

    public EnumSetting(String name, E defaultValue) {
        super(name, defaultValue);
        this.constants = defaultValue.getDeclaringClass().getEnumConstants();
    }

    /** Dokunmatik GUI icin: dokununca siradaki secenege gec. */
    public void cycle() {
        value = constants[(value.ordinal() + 1) % constants.length];
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(value.name());
    }

    @Override
    public void fromJson(JsonElement element) {
        if (!element.isJsonPrimitive()) {
            return;
        }
        String name = element.getAsString();
        for (E c : constants) {
            if (c.name().equals(name)) {
                value = c;
                return;
            }
        }
    }
}
