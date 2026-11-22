package com.aether.client.util;

import java.util.Locale;

public final class Names {
    private Names() {}

    /** Komutlarda bosluk ve buyuk/kucuk harf farkini yok sayar. */
    public static String norm(String s) {
        return s.toLowerCase(Locale.ROOT).replace(" ", "");
    }
}
