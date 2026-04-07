package com.t2pellet.discover.config;

import com.t2pellet.discover.DiscoverTitles;
import com.t2pellet.discover.client.render.TextRenderer;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.config.Config;
import net.minecraft.resources.ResourceLocation;

public class DiscoverConfig extends Config {

    public static final DiscoverConfig INSTANCE = ConfigApiJava.registerAndLoadConfig(DiscoverConfig::new);
    public TitleConfiguration dimension = new TitleConfiguration.Builder().scale(3.0F).yOffset(-66).build();
    public TitleConfiguration biome = new TitleConfiguration.Builder().scale(2.25F).yOffset(-42).build();
    public TitleConfiguration structure = new TitleConfiguration.Builder().scale(1.5F).yOffset(-18).build();
    public TitleConfiguration credits = new TitleConfiguration.Builder()
            .anchor(TextRenderer.Anchor.BOTTOM_RIGHT)
            .alignText(TextRenderer.Anchor.BOTTOM_RIGHT)
            .colour(0xD3D3D3)
            .timeOffsetTicks(15)
            .fadeInTicks(10)
            .displayTicks(50)
            .xOffset(-2)
            .yOffset(-2)
            .build();
    public DiscoverConfig() {
        super(new ResourceLocation(DiscoverTitles.MOD_ID, "config"));
    }
}
