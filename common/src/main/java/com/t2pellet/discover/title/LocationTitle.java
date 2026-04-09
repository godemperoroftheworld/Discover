package com.t2pellet.discover.title;

import net.minecraft.network.FriendlyByteBuf;

public interface LocationTitle {

    static LocationTitle read(FriendlyByteBuf buf) {
        LocationTitle.Type type = buf.readEnum(LocationTitle.Type.class);
        return switch (type) {
            case PLAYER -> new LocationRawTitle(type, buf);
            default -> new LocationGameTitle(type, buf);
        };
    }

    Type type();

    String title();

    String credit();

    Integer colour();

    void write(FriendlyByteBuf buf);

    enum Type {
        BIOME("biome"),
        STRUCTURE("structure"),
        DIMENSION("dimension"),
        PLAYER("player");

        public final String name;

        Type(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }
}
