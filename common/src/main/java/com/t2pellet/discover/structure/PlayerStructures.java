package com.t2pellet.discover.structure;

import com.t2pellet.discover.network.TitleSyncMessage;
import com.t2pellet.discover.title.LocationRawTitle;
import com.t2pellet.discover.title.LocationTitle;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
        ListTag listTag = compoundTag.getList("structures", Tag.TAG_COMPOUND);
        PlayerStructures structures = new PlayerStructures();
        listTag.forEach((tag) -> structures.add(PlayerStructure.load((CompoundTag) tag)));
        return structures;
    }

    public void add(PlayerStructure structure) {
        this.structures.add(structure);
        ServerPlayer player = structure.getServerPlayer();
        new TitleSyncMessage(new LocationRawTitle(LocationTitle.Type.PLAYER, structure.name, player.getName().getString())).sendTo(player);
        this.setDirty();
    }

    public void remove(PlayerStructure structure) {
        this.structures.remove(structure);
        this.setDirty();
    }

    public void remove(UUID id) {
        this.structures.removeIf(s -> s.uuid.equals(id));
    }

    public Set<PlayerStructure> containing(BlockPos pos) {
        return this.structures.stream().filter((s) -> s.contains(pos)).collect(Collectors.toSet());
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag) {
        ListTag listTag = new ListTag();
        structures.forEach(structure -> listTag.add(structure.save()));
        compoundTag.put("structures", listTag);
        return compoundTag;
    }

}
