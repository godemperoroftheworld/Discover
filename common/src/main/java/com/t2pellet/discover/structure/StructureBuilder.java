package com.t2pellet.discover.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.*;

// Utility class to help me get house boundaries
public class StructureBuilder {

    private static final int MAX_SIZE = 40000;
    private static final int SCAN_SIZE = 10;

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
        for (int offset = -1; offset <= SCAN_SIZE; ++offset) {
            BlockPos pos = startPos.relative(facing, offset);
            if (this.isAir(pos) && this.isInsideSomething(pos)) {
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
                if (!this.visited.contains(neighbour) && this.isAir(neighbour)) {
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

    private boolean isInsideSomething(BlockPos pos) {
        BlockPos[] positions = new BlockPos[]{pos.above(50), pos.east(50), pos.south(50), pos.west(50), pos.north(50)};
        return Arrays.stream(positions).allMatch(p -> {
            ItemEntity dummy = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), ItemStack.EMPTY);
            ClipContext context = new ClipContext(pos.getCenter(), p.getCenter(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, dummy);
            BlockHitResult result = level.clip(context);
            return result.getType() == HitResult.Type.BLOCK;
        });
    }

    private Optional<PlayerStructure> getPlayerStructure() {
        if (visited.size() > MAX_SIZE) {
            return Optional.empty();
        }

        return BoundingBox.encapsulatingPositions(visited).map((box) -> {
            BlockPos min = new BlockPos(box.minX(), box.minY(), box.minZ()).offset(-2, -1, -2);
            BlockPos max = new BlockPos(box.maxX(), box.maxY(), box.maxZ()).offset(3, 2, 3);
            SectionPos minSection = SectionPos.of(min);
            SectionPos maxSection = SectionPos.of(max);
            BoundingBox inflated = new BoundingBox(minSection.minBlockX(), minSection.minBlockY(), minSection.minBlockZ(), maxSection.maxBlockX(), maxSection.maxBlockY(), maxSection.maxBlockZ());
            return new PlayerStructure(name, player.getUUID(), inflated);
        });
    }
}
