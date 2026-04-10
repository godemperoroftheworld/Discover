package com.t2pellet.discover;

import com.t2pellet.discover.event.OrdainHouseEvent;
import com.t2pellet.discover.event.RemoveHouseEvent;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.InteractionEvent;
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
        InteractionEvent.RIGHT_CLICK_BLOCK.register(new OrdainHouseEvent());
        InteractionEvent.RIGHT_CLICK_BLOCK.register(new RemoveHouseEvent());
        BlockEvent.BREAK.register(new RemoveHouseEvent());
    }
}
