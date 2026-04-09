package com.t2pellet.discover.network;

import com.t2pellet.discover.client.render.title.TextRenderManager;
import com.t2pellet.discover.title.LocationTitle;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;

public class TitleSyncMessage extends BaseS2CMessage {

    private final LocationTitle title;

    public TitleSyncMessage(LocationTitle title) {
        this.title = title;
    }

    public TitleSyncMessage(FriendlyByteBuf buf) {
        this.title = LocationTitle.read(buf);
    }

    @Override
    public MessageType getType() {
        return DiscoverNetworking.STRUCTURE_SYNC;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        this.title.write(friendlyByteBuf);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        packetContext.queue(() -> TextRenderManager.INSTANCE.render(title));
    }
}
