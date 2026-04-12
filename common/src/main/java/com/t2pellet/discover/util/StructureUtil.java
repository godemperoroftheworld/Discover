package com.t2pellet.discover.util;

import com.t2pellet.discover.structure.PlayerStructure;
import com.t2pellet.discover.structure.PlayerStructures;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.List;
import java.util.Optional;

public class StructureUtil {

    private StructureUtil() {
    }

    public static Optional<StructureStart> findStructure(ServerLevel level, SectionPos sectionPos) {
        BoundingBox sectionBox = new BoundingBox(
                sectionPos.minBlockX(), sectionPos.minBlockY(), sectionPos.minBlockZ(),
                sectionPos.maxBlockX(), sectionPos.maxBlockY(), sectionPos.maxBlockZ()
        );
        List<StructureStart> starts = level.structureManager().startsForStructure(sectionPos.chunk(), s -> true);
        return starts.stream()
                .filter(StructureStart::isValid)
                .filter(start -> start.getBoundingBox().intersects(sectionBox))
                .findFirst();
    }

    public static Optional<PlayerStructure> findPlayerStructure(ServerLevel level, SectionPos sectionPos) {
        return PlayerStructures.get(level).containing(sectionPos.center()).stream().findFirst();
    }
}
