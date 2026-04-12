package com.t2pellet.discover.compat;

import dev.architectury.platform.Platform;

public class ModCompat {

    public static final String WAYSTONES = "waystones";

    private ModCompat() {
    }

    public static boolean isModLoaded(String modId) {
        return Platform.isModLoaded(modId);
    }
}
