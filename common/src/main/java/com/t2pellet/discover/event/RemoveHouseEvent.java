package com.t2pellet.discover.event;

import com.t2pellet.discover.config.ConfigHelpers;
import com.t2pellet.discover.config.DiscoverConfig;
import com.t2pellet.discover.structure.PlayerStructures;
import com.t2pellet.discover.util.SignWithBoundary;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.utils.value.IntValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class RemoveHouseEvent implements InteractionEvent.RightClickBlock, BlockEvent.Break {
    @Override
    public EventResult click(Player player, InteractionHand hand, BlockPos pos, Direction face) {
        if (player.level().isClientSide()) {
            return EventResult.pass();
        }

        ServerLevel level = (ServerLevel) player.level();
        ItemStack stack = player.getItemInHand(hand);
        if (level.getBlockEntity(pos) instanceof SignBlockEntity sign && !stack.isEmpty() && this.isRemovingItem(stack)) {
            EventResult result = this.removeHouse(level, sign, (ServerPlayer) player);
            if (result.isTrue()) {
                stack.shrink(1);
            }
            return result;
        }
        return EventResult.pass();
    }

    @Override
    public EventResult breakBlock(Level level, BlockPos pos, BlockState state, ServerPlayer player, @Nullable IntValue xp) {
        if (level.isClientSide()) {
            return EventResult.pass();
        }

        if (level.getBlockEntity(pos) instanceof SignBlockEntity sign) {
            return this.removeHouse((ServerLevel) level, sign, player);
        }
        return EventResult.pass();
    }

    private EventResult removeHouse(ServerLevel level, SignBlockEntity sign, ServerPlayer player) {
        SignWithBoundary boundarySign = (SignWithBoundary) sign;
        UUID id = boundarySign.discover$_getUUID();
        if (id == null) return EventResult.pass();
        PlayerStructures.get(level).remove(id);
        boundarySign.discover$_setUUID(null);
        player.displayClientMessage(Component.translatable("discover.boundary.removed"), true);
        return EventResult.interruptTrue();
    }

    private boolean isRemovingItem(ItemStack item) {
        return ConfigHelpers.matchesTagOrItem(DiscoverConfig.INSTANCE.houseRemovingItem, item);
    }
}
