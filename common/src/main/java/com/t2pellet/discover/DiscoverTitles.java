package com.t2pellet.discover;

import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.server.MinecraftServer;

public final class DiscoverTitles {
    public static final String MOD_ID = "discover";
    public static final String TRAVELER_TITLE_COMPAT_ID = "travelerstitles";

    public static MinecraftServer currentServer = null;

    public static void init() {
        LifecycleEvent.SERVER_STARTED.register(server -> {
            currentServer = server;
        });
    }
}
