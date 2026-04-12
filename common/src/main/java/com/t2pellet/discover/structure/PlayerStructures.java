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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerStructures extends SavedData {

    private static final String ID = "discover_player_structures";
    private final Set<PlayerStructure> structures = new HashSet<>();
    private final Map<UUID, PlayerStructure> structureMap = new HashMap<>();

    private PlayerStructures() {
    }

    public static PlayerStructures get(ServerLevel level) {
        DimensionDataStorage storage = level.getDataStorage();
        return storage.computeIfAbsent(PlayerStructures::load, PlayerStructures::new, ID);
    }

    static PlayerStructures load(CompoundTag compoundTag) {
        ListTag listTag = compoundTag.getList("structures", Tag.TAG_COMPOUND);
        PlayerStructures structures = new PlayerStructures();
        listTag.forEach((tag) -> {
            PlayerStructure s = PlayerStructure.load((CompoundTag) tag);
            structures.add(s, false);
        });
        return structures;
    }

    public PlayerStructure get(UUID id) {
        return this.structureMap.get(id);
    }

    public void add(PlayerStructure structure) {
        this.add(structure, true);
    }

    public void add(PlayerStructure structure, boolean sendTitle) {
        this.structures.add(structure);
        this.structureMap.put(structure.uuid, structure);
        ServerPlayer player = structure.getServerPlayer();
        if (sendTitle && player != null) {
            LocationTitle title = new LocationRawTitle(LocationTitle.Type.PLAYER, structure.name, player.getName().getString());
            new TitleSyncMessage(title).sendTo(player);
        }
        this.setDirty();
    }

    public int size() {
        return this.structures.size();
    }

    public boolean contains(PlayerStructure structure) {
        return this.structures.contains(structure);
    }

    public boolean contains(UUID id) {
        return this.structureMap.containsKey(id);
    }

    public void remove(PlayerStructure structure) {
        this.structures.remove(structure);
        this.structureMap.remove(structure.uuid);
        this.setDirty();
    }

    public void remove(UUID id) {
        this.structureMap.remove(id);
        this.structures.removeIf(s -> s.uuid.equals(id));
    }

    public Set<PlayerStructure> containing(BlockPos pos) {
        return this.structures.stream().filter((s) -> s.contains(pos)).collect(Collectors.toSet());
    }

    public boolean isEmpty() {
        return this.structures.isEmpty();
    }

    public Stream<PlayerStructure> stream() {
        return this.structures.stream();
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag) {
        ListTag listTag = new ListTag();
        structures.forEach(structure -> listTag.add(structure.save()));
        compoundTag.put("structures", listTag);
        return compoundTag;
    }

}
