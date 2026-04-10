package com.t2pellet.discover.network;

import com.t2pellet.discover.client.render.boundary.BoundaryRenderManager;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class BoundaryCreatedMessage extends BaseS2CMessage {

    private final BoundingBox box;

    public BoundaryCreatedMessage(BoundingBox box) {
        this.box = box;
    }

    public BoundaryCreatedMessage(FriendlyByteBuf buf) {
        this.box = new BoundingBox(
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt()
        );
    }

    @Override
    public MessageType getType() {
        return DiscoverNetworking.BOUNDARY_SYNC;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.box.minX());
        buf.writeInt(this.box.minY());
        buf.writeInt(this.box.minZ());
        buf.writeInt(this.box.maxX());
        buf.writeInt(this.box.maxY());
        buf.writeInt(this.box.maxZ());
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> {
            BoundaryRenderManager.INSTANCE.render(this.box.inflatedBy(2));
        });
    }
}
