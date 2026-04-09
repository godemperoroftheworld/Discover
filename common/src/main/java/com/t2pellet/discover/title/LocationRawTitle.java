package com.t2pellet.discover.title;

import net.minecraft.network.FriendlyByteBuf;

public class LocationRawTitle implements LocationTitle {

    private final Type type;
    private final String title;
    private final String credit;

    public LocationRawTitle(Type type, String title, String credit) {
        this.type = type;
        this.title = title;
        this.credit = credit;
    }

    public LocationRawTitle(Type type, FriendlyByteBuf buf) {
        this.type = type;
        this.title = buf.readUtf();
        this.credit = buf.readUtf();
    }

    @Override
    public Type type() {
        return this.type;
    }

    @Override
    public String title() {
        return this.title;
    }

    @Override
    public String credit() {
        return this.credit;
    }

    @Override
    public Integer colour() {
        return null;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(this.type);
        buf.writeUtf(this.title);
        buf.writeUtf(this.credit);
    }
}
