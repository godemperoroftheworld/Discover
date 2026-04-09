package com.t2pellet.discover.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.UUID;

public class PlayerStructure {

    public final UUID uuid;
    public final String name;
    public final UUID player;
    public final BoundingBox box;

    public PlayerStructure(String name, UUID player, BoundingBox box) {
        this(UUID.randomUUID(), name, player, box);
    }

    public PlayerStructure(UUID uuid, String name, UUID player, BoundingBox box) {
        this.uuid = uuid;
        this.name = name;
        this.player = player;
        // Inflate box to sections

        this.box = box;
    }

    public boolean contains(BlockPos pos) {
        return this.box.isInside(pos.getX(), pos.getY(), pos.getZ());
    }

    public static PlayerStructure load(CompoundTag tag) {
        int[] positions = tag.getIntArray("box");
        BoundingBox box = new BoundingBox(positions[0], positions[1], positions[2], positions[3], positions[4], positions[5]);
        return new PlayerStructure(
                tag.getString("namespace"),
                tag.getUUID("player"),
                box
        );
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("namespace", name);
        tag.putUUID("player", player);
        tag.putIntArray("box", new int[]{
                box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ()
        });
        return tag;
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PlayerStructure structure && structure.uuid.equals(this.uuid);
    }
}
