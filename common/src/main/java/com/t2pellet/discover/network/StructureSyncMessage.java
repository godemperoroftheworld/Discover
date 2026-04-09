package com.t2pellet.discover.network;

import com.t2pellet.discover.DiscoveredTitle;
import com.t2pellet.discover.client.render.title.TextRenderManager;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.Structure;

public class StructureSyncMessage extends BaseS2CMessage {

    private final ResourceLocation structure;

    public StructureSyncMessage(ServerLevel level, Structure structure) {
        this.structure = level.registryAccess().registry(Registries.STRUCTURE).orElseThrow().getKey(structure);
    }

    public StructureSyncMessage(FriendlyByteBuf buf) {
        this.structure = buf.readResourceLocation();
    }

    @Override
    public MessageType getType() {
        return DiscoverNetworking.STRUCTURE_SYNC;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeResourceLocation(this.structure);
    }

    @Override
    public void handle(NetworkManager.PacketContext packetContext) {
        packetContext.queue(() -> {
            DiscoveredTitle title = new DiscoveredTitle(DiscoveredTitle.Type.STRUCTURE, this.structure);
            TextRenderManager.INSTANCE.render(title);
        });
    }
}
