package com.aether.client.setting;

import com.google.gson.JsonElement;

public abstract class Setting<T> {
    private final String name;
    private final T defaultValue;
    protected T value;

    protected Setting(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public String getName() {
        return name;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    public void reset() {
        this.value = defaultValue;
    }

    /** GUI ve komutlarda gosterilen deger metni. */
    public String display() {
        return String.valueOf(value);
    }

    /** Komuttan gelen metni ayrıştırır; gecersizse false doner. */
    public abstract boolean setFromString(String text);

    public abstract JsonElement toJson();

    public abstract void fromJson(JsonElement element);
}
