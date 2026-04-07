package com.t2pellet.discover.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class DiscoverTags {
    private DiscoverTags() {}

    public static final TagKey<Biome> IS_CAVE = TagKey.create(
            Registries.BIOME,
            new ResourceLocation("minecraft", "is_cave")
    );
}
