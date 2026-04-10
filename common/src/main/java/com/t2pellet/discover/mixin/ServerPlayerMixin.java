package com.t2pellet.discover.mixin;

import com.t2pellet.discover.DiscoverTitles;
import com.t2pellet.discover.config.DiscoverConfig;
import com.t2pellet.discover.network.TitleSyncMessage;
import com.t2pellet.discover.structure.PlayerStructure;
import com.t2pellet.discover.structure.PlayerStructures;
import com.t2pellet.discover.title.LocationGameTitle;
import com.t2pellet.discover.title.LocationRawTitle;
import com.t2pellet.discover.title.LocationTitle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
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
    }

    @Unique
    private void discover$_handlePlayerStructure(SectionPos sectionPos) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        PlayerStructures structures = PlayerStructures.get(self.serverLevel());

        Set<PlayerStructure> structuresInSection = structures.containing(sectionPos.center());
        structuresInSection.stream().findAny().ifPresent(structure -> {
            ServerPlayer player = DiscoverTitles.currentServer.getPlayerList().getPlayer(structure.player);
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
        Optional<StructureStart> found = mixin$_findStructure(sectionPos);
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
    private Optional<StructureStart> mixin$_findStructure(SectionPos sectionPos) {
        ServerPlayer player = (ServerPlayer)(Object)this;
        BlockPos pos = sectionPos.center();
        ServerLevel level = player.serverLevel();

        List<StructureStart> starts = level.structureManager().startsForStructure(sectionPos.chunk(), s -> true);
        return starts.stream().filter(start -> {
            if (!start.isValid()) return false;
            return start.getBoundingBox().isInside(pos);
        }).findFirst();
    }
}
