package com.t2pellet.discover.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class SectionUtil {

    private SectionUtil() {
    }

    public static Tuple<SectionPos, SectionPos> getSectionBounds(BoundingBox box) {
        BlockPos min = new BlockPos(box.minX(), box.minY(), box.minZ());
        BlockPos max = new BlockPos(box.maxX(), box.maxY(), box.maxZ());
        SectionPos minSection = SectionPos.of(min);
        SectionPos maxSection = SectionPos.of(max);
        return new Tuple<>(minSection, maxSection);
    }
}
