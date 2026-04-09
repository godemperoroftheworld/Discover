package com.t2pellet.discover.client;

import com.t2pellet.discover.DiscoveredTitle;
import com.t2pellet.discover.client.render.TextRenderManager;
import com.t2pellet.discover.client.util.DiscoverLog;
import com.t2pellet.discover.registry.DiscoverSounds;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

public class DiscoverTitlesClient {

    private static MinecraftServer currentServer;

    public static void init() {
        ClientGuiEvent.RENDER_HUD.register(TextRenderManager.INSTANCE);
        ClientTickEvent.CLIENT_POST.register(TextRenderManager.INSTANCE::tick);
        PlayerEvent.CHANGE_DIMENSION.register((player, oldKey, newKey) -> {
            ResourceLocation location = newKey.location();
            DiscoveredTitle title = new DiscoveredTitle(DiscoveredTitle.Type.DIMENSION, location);
            TextRenderManager.INSTANCE.render(title);
        });
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(DiscoverLog.INSTANCE);
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(DiscoverLog.INSTANCE);
        DiscoverSounds.register();
    }
}
