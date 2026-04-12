package com.t2pellet.discover.mixin;

import com.t2pellet.discover.compat.ModCompat;
import com.t2pellet.discover.compat.WaystonesCompat;
import com.t2pellet.discover.config.DiscoverConfig;
import com.t2pellet.discover.network.TitleSyncMessage;
import com.t2pellet.discover.structure.PlayerStructure;
import com.t2pellet.discover.structure.PlayerStructures;
import com.t2pellet.discover.title.LocationGameTitle;
import com.t2pellet.discover.title.LocationRawTitle;
import com.t2pellet.discover.title.LocationTitle;
import com.t2pellet.discover.util.StructureUtil;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.Set;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Unique
    private Structure discover$lastStructure = null;
    @Unique
    private long discover$lastTime = 0;

    @Inject(method = "setLastSectionPos", at = @At("HEAD"))
    private void onLastSectionUpdated(SectionPos sectionPos, CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        SectionPos lastSection = self.getLastSectionPos();

        // Early return chunk check (for performance)
        if (sectionPos.equals(lastSection)) {
            return;
        }

        // Early return cooldown check
        long time = self.level().getGameTime();
        long diff = Math.abs(time - discover$lastTime);
        if (diff < DiscoverConfig.INSTANCE.cooldownTicks.get()) {
            return;
        }

        discover$_handleStructure(sectionPos);
        discover$_handlePlayerStructure(sectionPos);
        if (ModCompat.isModLoaded(ModCompat.WAYSTONES)) {
            discover$_handleWaystone();
        }
    }

    @Unique
    private void discover$_handlePlayerStructure(SectionPos sectionPos) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        PlayerStructures structures = PlayerStructures.get(self.serverLevel());

        Set<PlayerStructure> structuresInSection = structures.containing(sectionPos.center());
        structuresInSection.stream().findAny().ifPresent(structure -> {
            ServerPlayer player = structure.getServerPlayer();
            String playerName = player != null ? player.getName().getString() : Language.getInstance().getOrDefault("discover.unknown_player");
            new TitleSyncMessage(new LocationRawTitle(
                    LocationTitle.Type.PLAYER,
                    structure.name,
                    playerName
            )).sendTo(self);
        });
    }

    @Unique
    private void discover$_handleStructure(SectionPos sectionPos) {
        ServerPlayer self = (ServerPlayer)(Object)this;

        // Get the current structure, early return if none
        Optional<StructureStart> found = StructureUtil.findStructure(self.serverLevel(), sectionPos);
        if (found.isEmpty()) {
            discover$lastStructure = null;
            return;
        }
        StructureStart currentStructure = found.get();

        // Early return if in the same structure
        if (currentStructure.getStructure().equals(discover$lastStructure)) {
            return;
        }

        // Get ResourceLocation
        ResourceLocation location = self.serverLevel().registryAccess().registry(Registries.STRUCTURE).get().getKey(currentStructure.getStructure());;
        if (location == null) {
            return;
        }

        // Show title
        new TitleSyncMessage(new LocationGameTitle(
                LocationTitle.Type.STRUCTURE,
                location
        )).sendTo(self);
        discover$lastStructure = currentStructure.getStructure();
    }

    @Unique
    private void discover$_handleWaystone() {
        ServerPlayer self = (ServerPlayer) (Object) this;
        Optional<PlayerStructure> waystone = WaystonesCompat.getForWaystone(self);
        if (waystone.isPresent()) {
            ServerPlayer player = waystone.get().getServerPlayer();
            String playerName = player != null ? player.getName().getString() : Language.getInstance().getOrDefault("discover.unknown_player");
            new TitleSyncMessage(new LocationRawTitle(
                    LocationTitle.Type.PLAYER,
                    waystone.get().name,
                    playerName
            )).sendTo(self);
        }
    }
}
