package com.t2pellet.discover.network;

import com.t2pellet.discover.client.render.BoundaryRenderer;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class BoundaryMessage extends BaseS2CMessage {

    private final BoundingBox box;

    public BoundaryMessage(BoundingBox box) {
        this.box = box;
    }

    public BoundaryMessage(FriendlyByteBuf buf) {
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
            BoundaryRenderer renderer = new BoundaryRenderer(
                    Minecraft.getInstance().level,
                    this.box
            );
            System.out.println("GOT IT!");
            renderer.draw();
        });
    }
}
