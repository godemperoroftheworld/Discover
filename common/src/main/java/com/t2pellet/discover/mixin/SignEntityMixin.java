package com.t2pellet.discover.mixin;

import com.t2pellet.discover.util.SignWithBoundary;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(SignBlockEntity.class)
public class SignEntityMixin extends BlockEntity implements SignWithBoundary {

    @Unique
    private UUID discover$uuid;

    protected SignEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Unique
    @Override
    public UUID discover$_getUUID() {
        return this.discover$uuid;
    }

    @Unique
    @Override
    public void discover$_setUUID(UUID uuid) {
        this.discover$uuid = uuid;
    }


    @Inject(method = "saveAdditional", at = @At("TAIL"))
    public void onSave(CompoundTag tag, CallbackInfo ci) {
        if (discover$uuid != null) {
            tag.putUUID("discover_uuid", discover$uuid);
        }
    }

    @Inject(method = "load", at = @At("TAIL"))
    public void onLoad(CompoundTag tag, CallbackInfo ci) {
        if (tag.hasUUID("discover_uuid")) {
            discover$uuid = tag.getUUID("discover_uuid");
        }
    }
}
