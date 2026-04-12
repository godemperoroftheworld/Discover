package com.t2pellet.discover.config;

import com.t2pellet.discover.DiscoverTitles;
import com.t2pellet.discover.client.render.title.TextRenderer;
import com.t2pellet.discover.registry.DiscoverSounds;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedEnum;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import net.minecraft.resources.ResourceLocation;

public class DiscoverConfig extends Config {

    public enum Mode {
        ONCE_PER_SESSION,
        ONCE_PER_WORLD,
        ALWAYS,
    }

    public static final DiscoverConfig INSTANCE = ConfigApiJava.registerAndLoadConfig(DiscoverConfig::new);

    public ValidatedEnum<Mode> mode = new ValidatedEnum<>(Mode.ALWAYS);

    public ValidatedInt cooldownTicks = new ValidatedInt(120);
    public ValidatedInt cooldownCount = new ValidatedInt(5);

    public ValidatedInt renderTime = new ValidatedInt(120);

    public ValidatedIdentifier houseOrdainingItem = ConfigHelpers.forAnyItemOrTag(new ResourceLocation("minecraft", "honeycomb"));
    public ValidatedIdentifier houseRemovingItem = ConfigHelpers.forAnyItemOrTag(new ResourceLocation("minecraft", "axes"));

    public TitleConfiguration dimension = new TitleConfiguration.Builder().scale(3.0F).yOffset(-66).sound(DiscoverSounds.DIMENSION_DISCOVERED.location).build();
    public TitleConfiguration biome = new TitleConfiguration.Builder().scale(2.25F).yOffset(-42).sound(DiscoverSounds.BIOME_DISCOVERED.location).build();
    public TitleConfiguration structure = new TitleConfiguration.Builder().scale(1.5F).yOffset(-18).sound(DiscoverSounds.STRUCTURE_DISCOVERED.location).build();

    public TitleConfiguration credits = new TitleConfiguration.Builder()
            .anchor(TextRenderer.Anchor.BOTTOM_RIGHT)
            .alignText(TextRenderer.Anchor.BOTTOM_RIGHT)
            .colour(0xD3D3D3)
            .timeOffsetTicks(15)
            .fadeInTicks(10)
            .displayTicks(50)
            .xOffset(-2)
            .yOffset(-2)
            .shadow(false)
            .build();


    public DiscoverConfig() {
        super(new ResourceLocation(DiscoverTitles.MOD_ID, "config"));
    }
}
