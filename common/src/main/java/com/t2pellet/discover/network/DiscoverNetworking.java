package com.t2pellet.discover.network;

import com.t2pellet.discover.DiscoverTitles;
import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;

public interface DiscoverNetworking {
    SimpleNetworkManager NET = SimpleNetworkManager.create(DiscoverTitles.MOD_ID);
    MessageType STRUCTURE_SYNC = NET.registerS2C("title_sync", StructureSyncMessage::new);
}
