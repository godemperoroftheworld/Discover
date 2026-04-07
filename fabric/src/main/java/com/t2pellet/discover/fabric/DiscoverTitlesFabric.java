package com.t2pellet.discover.fabric;

import com.t2pellet.discover.DiscoverTitles;
import net.fabricmc.api.ModInitializer;

public final class DiscoverTitlesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        DiscoverTitles.init();
    }
}
