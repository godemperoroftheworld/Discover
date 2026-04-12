package com.t2pellet.discover.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.*;

// Utility class to help me get house boundaries
public class StructureBuilder {

    private static final int MAX_SIZE = 100000;
    private static final int SCAN_SIZE = 10;
    private static final int RAYCAST_COUNT = 50;

    private final Player player;
    private final String name;
    private final Level level;
    private final BlockPos startPos;
    private final Queue<BlockPos> queue = new ArrayDeque<>();
    private final Set<BlockPos> visited = new HashSet<>();

    public StructureBuilder(String name, Player player, BlockPos startPos) {
        this.name = name;
        this.player = player;
        Direction facing = player.getDirection();
        this.level = player.level();
        // Query for air block in the vicinity of startPos
        BlockPos airPos = null;
        for (int offset = 1; offset <= SCAN_SIZE; ++offset) {
            BlockPos pos = startPos.relative(facing, offset);
            if (this.isAir(pos) && this.isSurrounded(pos)) {
                airPos = pos;
                break;
            }
        }
        this.startPos = Objects.requireNonNullElse(airPos, startPos);
    }

    public Optional<PlayerStructure> search() {
        queue.add(startPos);
        visited.add(startPos);

        while (!queue.isEmpty() && visited.size() <= MAX_SIZE) {
            BlockPos current = queue.poll();

            for (BlockPos neighbour : this.getNeighbours(current)) {
                boolean isInteriorBlock = (this.isInsideSomething(neighbour) && this.isNextToSolid(neighbour)) || this.isSurrounded(neighbour);
                if (!this.visited.contains(neighbour) && this.isAir(neighbour) && isInteriorBlock) {
                    this.visited.add(neighbour);
                    this.queue.add(neighbour);
                }
            }
        }

        return this.getPlayerStructure();
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

    private boolean isNextToSolid(BlockPos pos) {
        return this.getNeighbours(pos).stream().anyMatch(this::isSolid);
    }

    private boolean isSolid(BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return !state.isAir() && !state.is(BlockTags.DIRT) && !state.is(BlockTags.LEAVES);
    }

    private boolean isInsideSomething(BlockPos pos) {
        return checkRaycast(pos, 5);
    }

    private boolean isSurrounded(BlockPos pos) {
        return checkRaycast(pos, 6);
    }

    private boolean checkRaycast(BlockPos pos, int count) {
        BlockPos[] positions = new BlockPos[]{pos.above(RAYCAST_COUNT), pos.east(RAYCAST_COUNT), pos.south(RAYCAST_COUNT), pos.west(RAYCAST_COUNT), pos.north(RAYCAST_COUNT), pos.below(RAYCAST_COUNT)};
        return Arrays.stream(positions).filter(p -> {
            ItemEntity dummy = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), ItemStack.EMPTY);
            ClipContext context = new ClipContext(pos.getCenter(), p.getCenter(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, dummy);
            BlockHitResult result = level.clip(context);
            if (result.getType() == HitResult.Type.MISS) return false;
            return this.isSolid(result.getBlockPos());
        }).count() >= count;
    }

    private Optional<PlayerStructure> getPlayerStructure() {
        if (visited.size() > MAX_SIZE) {
            return Optional.empty();
        }

        return BoundingBox.encapsulatingPositions(visited).map((box) -> {
            BlockPos min = new BlockPos(box.minX(), box.minY(), box.minZ()).offset(-2, -1, -2);
            BlockPos max = new BlockPos(box.maxX(), box.maxY(), box.maxZ()).offset(3, 2, 3);
            BoundingBox inflated = new BoundingBox(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ()).inflatedBy(1);
            return new PlayerStructure(name, player.getUUID(), inflated);
        });
    }
}
