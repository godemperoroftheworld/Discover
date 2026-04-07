package com.t2pellet.discover.client;

import com.t2pellet.discover.DiscoveredTitle;
import com.t2pellet.discover.client.render.TextRenderManager;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.resources.ResourceLocation;

public class DiscoverTitlesClient {

    public static void init() {
        ClientGuiEvent.RENDER_HUD.register(TextRenderManager.INSTANCE);
        ClientTickEvent.CLIENT_POST.register(TextRenderManager.INSTANCE::tick);
        PlayerEvent.CHANGE_DIMENSION.register((player, oldKey, newKey) -> {
            ResourceLocation location = newKey.location();
            DiscoveredTitle title = new DiscoveredTitle(DiscoveredTitle.Type.DIMENSION, location);
            TextRenderManager.INSTANCE.render(title);
        });
    }
}
