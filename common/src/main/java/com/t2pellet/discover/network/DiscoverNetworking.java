package com.t2pellet.discover.network;

import com.t2pellet.discover.DiscoverTitles;
import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;

public class DiscoverNetworking {

    public static SimpleNetworkManager NET = SimpleNetworkManager.create(DiscoverTitles.MOD_ID);
    public static MessageType STRUCTURE_SYNC = NET.registerS2C("title_sync", TitleSyncMessage::new);
    public static MessageType BOUNDARY_REQUEST = NET.registerC2S("boundary_request", CreateBoundaryMessage::new);
    public static MessageType BOUNDARY_SYNC = NET.registerS2C("boundary_sync", RenderBoundariesMessage::new);
    public static MessageType BOUNDARY_CLEAR = NET.registerS2C("boundary_clear", ClearBoundariesMessage::new);

    private DiscoverNetworking() {
    }
}

