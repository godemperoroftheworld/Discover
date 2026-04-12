package com.t2pellet.discover.compat;

import com.t2pellet.discover.network.TitleSyncMessage;
import com.t2pellet.discover.structure.PlayerStructure;
import com.t2pellet.discover.title.LocationRawTitle;
import com.t2pellet.discover.title.LocationTitle;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.BalmEvents;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.WaystoneActivatedEvent;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.Optional;
import java.util.UUID;

public class WaystonesCompat {

    private WaystonesCompat() {
    }

    public static Optional<PlayerStructure> getForWaystone(Player player) {
        return WaystonesAPI.getNearestWaystone(player).map(waystone -> {
            String name = waystone.getName();
            BlockPos pos = waystone.getPos();
            UUID owner = waystone.getOwnerUid();
            return new PlayerStructure(name, owner, new BoundingBox(pos).inflatedBy(6));
        });
    }

    public static void handleWaystoneCreation() {
        BalmEvents events = Balm.getEvents();
        events.onEvent(WaystoneActivatedEvent.class, event -> {
            if (event.getPlayer().level().isClientSide) return;
            IWaystone waystone = event.getWaystone();
            ServerPlayer player = (ServerPlayer) event.getPlayer();
            LocationTitle title = new LocationRawTitle(LocationTitle.Type.PLAYER, waystone.getName(), player.getName().getString());
            new TitleSyncMessage(title).sendTo(player);
        });
    }

}
