package com.t2pellet.discover.network;

import com.t2pellet.discover.structure.PlayerStructure;
import com.t2pellet.discover.structure.PlayerStructures;
import com.t2pellet.discover.structure.StructureBuilder;
import com.t2pellet.discover.title.LocationRawTitle;
import com.t2pellet.discover.title.LocationTitle;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class CreateBoundaryMessage extends BaseC2SMessage {

    private final String name;
    private final BlockPos pos;

    public CreateBoundaryMessage(String name, BlockPos pos) {
        this.name = name;
        this.pos = pos;
    }

    public CreateBoundaryMessage(FriendlyByteBuf buf) {
        this.name = buf.readUtf();
        this.pos = buf.readBlockPos();
    }

    @Override
    public MessageType getType() {
        return DiscoverNetworking.BOUNDARY_REQUEST;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.name);
        buf.writeBlockPos(this.pos);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> {
            ServerPlayer player = (ServerPlayer) context.getPlayer();
            StructureBuilder finder = new StructureBuilder(this.name, player, this.pos);
            Optional<PlayerStructure> boundary = finder.search();
            boundary.ifPresentOrElse(structure -> {
                PlayerStructures.get(player.serverLevel()).add(structure);
                new RenderBoundariesMessage(structure.box).sendTo(player);
                new TitleSyncMessage(new LocationRawTitle(LocationTitle.Type.PLAYER, this.name, player.getName().getString())).sendTo(player);
                player.displayClientMessage(Component.translatable("discover.boundary.created"), true);
            }, () -> {
                context.getPlayer().displayClientMessage(Component.translatable("discover.boundary.error"), true);
            });
        });
    }
}
