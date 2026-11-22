package com.aether.client.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.Locale;

public final class NumberSetting extends Setting<Double> {
    private final double min;
    private final double max;
    private final double step;

    public NumberSetting(String name, double defaultValue, double min, double max, double step) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getStep() {
        return step;
    }

    public float getFloat() {
        return value.floatValue();
    }

    public int getInt() {
        return value.intValue();
    }

    /** Dokunmatik GUI icin: -1 azalt, +1 arttir. */
    public void increment(int direction) {
        double s = step > 0 ? step : (max - min) / 20.0;
        set(value + direction * s);
    }

    @Override
    public void set(Double v) {
        double clamped = Math.max(min, Math.min(max, v));
        if (step > 0) {
            clamped = Math.round(clamped / step) * step;
            clamped = Math.max(min, Math.min(max, clamped));
        }
        this.value = clamped;
    }

    @Override
    public String display() {
        if (step >= 1) {
            return String.valueOf(value.intValue());
        }
        return String.format(Locale.ROOT, "%.2f", value);
    }

    @Override
    public boolean setFromString(String text) {
        try {
            set(Double.parseDouble(text));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(value);
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            set(element.getAsDouble());
        }
    }
}
