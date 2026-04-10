package com.t2pellet.discover.event;

import com.t2pellet.discover.config.ConfigHelpers;
import com.t2pellet.discover.config.DiscoverConfig;
import com.t2pellet.discover.network.BoundaryCreatedMessage;
import com.t2pellet.discover.structure.PlayerStructure;
import com.t2pellet.discover.structure.PlayerStructures;
import com.t2pellet.discover.structure.StructureBuilder;
import com.t2pellet.discover.util.SignUtil;
import com.t2pellet.discover.util.SignWithBoundary;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;

import java.util.Optional;

public class OrdainHouseEvent implements InteractionEvent.RightClickBlock {
    @Override
    public EventResult click(Player player, InteractionHand hand, BlockPos pos, Direction face) {
        if (player.level().isClientSide()) {
            return EventResult.pass();
        }
        return this.ordainHouse(player, hand, pos);
    }

    private EventResult ordainHouse(Player player, InteractionHand hand, BlockPos pos) {
        ServerLevel level = (ServerLevel) player.level();
        ItemStack stack = player.getItemInHand(hand);
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof SignBlockEntity sign && !stack.isEmpty() && this.isOrdainingItem(stack)) {
            Optional<String> name = SignUtil.getFirstText(sign);
            if (name.isEmpty()) {
                player.displayClientMessage(Component.translatable("discover.boundary.name_required"), true);
                return EventResult.pass();
            }
            StructureBuilder finder = new StructureBuilder(name.get(), player, pos);
            Optional<PlayerStructure> structure = finder.search();
            if (structure.isPresent()) {
                PlayerStructures.get(level).add(structure.get());
                ((SignWithBoundary) sign).discover$_setUUID(structure.get().uuid);
                new BoundaryCreatedMessage(structure.get().box).sendTo((ServerPlayer) player);
                player.displayClientMessage(Component.translatable("discover.boundary.created"), true);
                stack.shrink(1);
                sign.setWaxed(true);
                return EventResult.pass();
            } else {
                player.displayClientMessage(Component.translatable("discover.boundary.error"), true);
                return EventResult.pass();
            }
        }
        return EventResult.pass();
    }

    private boolean isOrdainingItem(ItemStack item) {
        return ConfigHelpers.matchesTagOrItem(DiscoverConfig.INSTANCE.houseOrdainingItem, item);
    }
}
