package com.t2pellet.discover.mixin;

import com.t2pellet.discover.DiscoveredTitle;
import com.t2pellet.discover.client.render.TextRenderManager;
import com.t2pellet.discover.config.DiscoverConfig;
import com.t2pellet.discover.registry.DiscoverTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Unique
    private long discover$lastTime = 0;
    @Unique
    private Biome discover$lastBiome = null;
    @Unique
    private SectionPos discover$lastSection = null;


    @Inject(method = "tick", at = @At("TAIL"))
    public void onTick(CallbackInfo ci) {
        LocalPlayer self = (LocalPlayer)(Object)this;

        // Early return when changing / loading dimension
        if (Minecraft.getInstance().level == null || !Minecraft.getInstance().level.isLoaded(self.blockPosition())) {
            return;
        }

        // Early return chunk check (for performance)
        SectionPos currentSection = SectionPos.of(self);
        if (currentSection.equals(discover$lastSection)) {
            return;
        } else {
            discover$lastSection = currentSection;
        }

        // Early return cooldown check
        long time = Minecraft.getInstance().level.getGameTime();
        long diff = Math.abs(time - discover$lastTime);
        if (diff < DiscoverConfig.INSTANCE.cooldownTicks.get()) {
            return;
        }

        // Get the current biome, early return if the same
        Holder<Biome> biome = Minecraft.getInstance().level.getBiome(self.blockPosition());
        if (biome.value().equals(discover$lastBiome)) {
            return;
        }

        // Special handling when underground, only consider cave biomes
        boolean dimensionHasSky = Minecraft.getInstance().level.dimensionType().hasSkyLight();
        boolean isSkyObstructed = !Minecraft.getInstance().level.canSeeSkyFromBelowWater(self.blockPosition());
        // My hacky attempt at figuring out what's a cave biome
        boolean isCaveBiome = biome.is(DiscoverTags.IS_CAVE);
        if (dimensionHasSky && isSkyObstructed && !isCaveBiome) {
            return;
        }

        // Update last biome
        discover$lastBiome = biome.value();

        // Get ResourceLocation
        ResourceLocation location = biome.unwrapKey().map(ResourceKey::location).orElse(null);
        if (location == null) {
            return;
        }

        // Show title
        DiscoveredTitle title = new DiscoveredTitle(DiscoveredTitle.Type.BIOME, location);
        TextRenderManager.INSTANCE.render(title);
        discover$lastTime = time;
    }
}
