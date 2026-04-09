package com.t2pellet.discover.registry;

import com.t2pellet.discover.DiscoverTitles;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class DiscoverSounds {

    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(DiscoverTitles.MOD_ID, Registries.SOUND_EVENT);

    public static final SoundHolder DIMENSION_DISCOVERED = new SoundHolder("dimension");
    public static final SoundHolder BIOME_DISCOVERED = new SoundHolder("biome");
    public static final SoundHolder STRUCTURE_DISCOVERED = new SoundHolder("structure");

    private DiscoverSounds() {
    }

    public static void register() {
        SOUNDS.register();
    }

    public static class SoundHolder {
        public final Supplier<SoundEvent> sound;
        public final ResourceLocation location;

        public SoundHolder(String name) {
            this.location = new ResourceLocation(DiscoverTitles.MOD_ID, name);
            this.sound = SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(location));
        }
    }
}
