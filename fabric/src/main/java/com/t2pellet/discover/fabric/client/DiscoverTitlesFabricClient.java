package com.t2pellet.discover.fabric.client;

import com.t2pellet.discover.client.DiscoverTitlesClient;
import net.fabricmc.api.ClientModInitializer;

public final class DiscoverTitlesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        DiscoverTitlesClient.init();
    }
}
