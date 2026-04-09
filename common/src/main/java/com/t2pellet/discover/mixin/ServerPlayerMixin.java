package com.t2pellet.discover.mixin;

import com.t2pellet.discover.network.TitleSyncMessage;
import com.t2pellet.discover.title.LocationGameTitle;
import com.t2pellet.discover.title.LocationTitle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
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

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Unique
    private Structure discover$lastStructure = null;

    @Inject(method = "setLastSectionPos", at = @At("HEAD"))
    private void onLastSectionUpdated(SectionPos sectionPos, CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer)(Object)this;
        SectionPos lastSection = self.getLastSectionPos();

        // Early return chunk check (for performance)
        if (sectionPos.equals(lastSection)) {
            return;
        }

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
                LocationTitle.Type.DIMENSION,
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
