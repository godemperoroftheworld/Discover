package com.t2pellet.discover.structure;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class PlayerStructures extends SavedData {

    private static final String ID = "discover_player_structures";
    private final Set<PlayerStructure> structures = new HashSet<>();

    private PlayerStructures() {
    }

    public static PlayerStructures get(ServerLevel level) {
        DimensionDataStorage storage = level.getDataStorage();
        return storage.computeIfAbsent(PlayerStructures::load, PlayerStructures::new, ID);
    }

    static PlayerStructures load(CompoundTag compoundTag) {
        return new PlayerStructures();
    }

    public void add(PlayerStructure structure) {
        this.structures.add(structure);
        this.setDirty();
    }

    public void remove(PlayerStructure structure) {
        this.structures.remove(structure);
        this.setDirty();
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag) {
        structures.forEach(structure -> compoundTag.put(structure.uuid.toString(), structure.save()));
        return compoundTag;
    }

}
