package com.t2pellet.discover.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.*;

// Utility class to help me get house boundaries
public class BoundaryFinder {

    private static final int MAX_SIZE = 40000;
    private static final int SCAN_SIZE = 10;
    private static final List<Direction> DIRECTIONS = Arrays.asList(Direction.values());

    private final LevelAccessor level;
    private final BlockPos startPos;
    private final Queue<BlockPos> queue = new ArrayDeque<>();
    private final Set<BlockPos> visited = new HashSet<>();

    public BoundaryFinder(LevelAccessor level, BlockPos startPos, Direction facing) {
        this.level = level;
        // Query for air block in the vicinity of startPos
        BlockPos airPos = null;
        for (int offset = -1; offset <= SCAN_SIZE; ++offset) {
            BlockPos pos = startPos.relative(facing, offset);
            if (this.isAir(pos) && !level.canSeeSky(pos)) {
                airPos = pos;
                break;
            }
        }
        this.startPos = Objects.requireNonNullElse(airPos, startPos);
    }

    public Optional<BoundingBox> search() {
        queue.add(startPos);
        visited.add(startPos);

        while (!queue.isEmpty() && visited.size() <= MAX_SIZE) {
            BlockPos current = queue.poll();

            for (BlockPos neighbour : this.getNeighbours(current)) {
                if (!this.visited.contains(neighbour) && this.isAir(neighbour)) {
                    this.visited.add(neighbour);
                    this.queue.add(neighbour);
                }
            }
        }

        if (visited.size() > MAX_SIZE) {
            return null;
        }

        return BoundingBox.encapsulatingPositions(visited).map((box) -> {
            // encapsulate is deprecated for some reason
            BlockPos min = new BlockPos(box.minX(), box.minY(), box.minZ()).offset(-1, -1, -1);
            BlockPos max = new BlockPos(box.maxX(), box.maxY(), box.maxZ());
            return BoundingBox.fromCorners(min, max).inflatedBy(2);
        });
    }

    private List<BlockPos> getNeighbours(BlockPos pos) {
        List<BlockPos> neighbors = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            neighbors.add(pos.relative(direction));
        }
        return neighbors;
    }

    private boolean isAir(BlockPos pos) {
        return level.getBlockState(pos).isAir();
    }
}
