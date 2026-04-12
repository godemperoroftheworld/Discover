package com.t2pellet.discover.network;

import com.t2pellet.discover.client.render.boundary.BoundaryRenderManager;
import com.t2pellet.discover.config.DiscoverConfig;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.List;

public class RenderBoundariesMessage extends BaseS2CMessage {

    private final List<BoundingBox> boxes;
    private final int ticks;

    public RenderBoundariesMessage(BoundingBox... boxes) {
        this(DiscoverConfig.INSTANCE.renderTime.get(), boxes);
    }

    public RenderBoundariesMessage(List<BoundingBox> boxes) {
        this(DiscoverConfig.INSTANCE.renderTime.get(), boxes);
    }

    public RenderBoundariesMessage(int ticks, BoundingBox... boxes) {
        this(ticks, List.of(boxes));
    }

    public RenderBoundariesMessage(int ticks, List<BoundingBox> boxes) {
        this.boxes = boxes;
        this.ticks = ticks;
    }

    public RenderBoundariesMessage(FriendlyByteBuf buf) {
        this.boxes = buf.readList(friendlyByteBuf -> new BoundingBox(
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt()
        ));
        this.ticks = buf.readInt();
    }

    @Override
    public MessageType getType() {
        return DiscoverNetworking.BOUNDARY_SYNC;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeCollection(this.boxes, (friendlyByteBuf, box) -> {
            buf.writeInt(box.minX());
            buf.writeInt(box.minY());
            buf.writeInt(box.minZ());
            buf.writeInt(box.maxX());
            buf.writeInt(box.maxY());
            buf.writeInt(box.maxZ());
        });
        buf.writeInt(this.ticks);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> {
            this.boxes.forEach(box -> {
                if (this.ticks > 0 && this.ticks < Integer.MAX_VALUE) {
                    BoundaryRenderManager.INSTANCE.render(box, this.ticks);
                } else {
                    BoundaryRenderManager.INSTANCE.renderForever(box);
                }
            });
        });
    }
}
