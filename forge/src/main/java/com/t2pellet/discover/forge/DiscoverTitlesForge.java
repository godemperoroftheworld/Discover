package com.t2pellet.discover.forge;

import com.t2pellet.discover.DiscoverTitles;
import com.t2pellet.discover.client.DiscoverTitlesClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(DiscoverTitles.MOD_ID)
public final class DiscoverTitlesForge {
    public DiscoverTitlesForge() {
        // Run our common setup.
        registerCommon();
        // Run our client setup
        if (FMLLoader.getDist() == Dist.CLIENT) {
            registerClient();
        }
    }

    private void registerCommon() {
        DiscoverTitles.init();
    }

    @OnlyIn(Dist.CLIENT)
    private void registerClient() {
        DiscoverTitlesClient.init();
    }
}
