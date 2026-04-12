package com.t2pellet.discover.network;

import com.t2pellet.discover.client.render.boundary.BoundaryRenderManager;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;

public class ClearBoundariesMessage extends BaseS2CMessage {

    public ClearBoundariesMessage() {
    }

    public ClearBoundariesMessage(FriendlyByteBuf buf) {
    }

    @Override
    public MessageType getType() {
        return DiscoverNetworking.BOUNDARY_CLEAR;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(BoundaryRenderManager.INSTANCE::clear);
    }
}
