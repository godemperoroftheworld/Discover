package com.t2pellet.discover;

import com.t2pellet.discover.util.BoundaryFinder;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.Optional;

public final class DiscoverTitles {
    public static final String MOD_ID = "discover";
    public static final String TRAVELER_TITLE_COMPAT_ID = "travelerstitles";

    public static void init() {
        BlockEvent.PLACE.register((level, pos, state, placer) -> {
            if (state.getBlock() == Blocks.WALL_TORCH) {
                Direction direction = placer.getDirection();
                Optional<BoundingBox> bounds = new BoundaryFinder(level, pos, direction).search();
            }
            return EventResult.pass();
        });
    }
}
