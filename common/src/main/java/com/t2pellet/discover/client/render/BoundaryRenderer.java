package com.t2pellet.discover.client.render;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;

public class BoundaryRenderer {

    private final LevelAccessor level;
    private final BoundingBox box;

    public BoundaryRenderer(LevelAccessor level, BoundingBox box) {
        this.level = level;
        this.box = box;
    }

    public void draw() {
        double minX = box.minX();
        double minY = box.minY();
        double minZ = box.minZ();
        double maxX = box.maxX();
        double maxY = box.maxY();
        double maxZ = box.maxZ();

        System.out.println("DRAWING");

        // Draw the 4 vertical pillars
        drawLine(new Vec3(minX, minY, minZ), new Vec3(minX, maxY, minZ));
        drawLine(new Vec3(maxX, minY, minZ), new Vec3(maxX, maxY, minZ));
        drawLine(new Vec3(minX, minY, maxZ), new Vec3(minX, maxY, maxZ));
        drawLine(new Vec3(maxX, minY, maxZ), new Vec3(maxX, maxY, maxZ));
        // Draw the 4 horizontal edges (bottom)
        drawLine(new Vec3(minX, minY, minZ), new Vec3(maxX, minY, minZ));
        drawLine(new Vec3(minX, minY, minZ), new Vec3(minX, minY, maxZ));
        drawLine(new Vec3(maxX, minY, maxZ), new Vec3(minX, minY, maxZ));
        drawLine(new Vec3(maxX, minY, minZ), new Vec3(maxX, minY, maxZ));
        // Draw the 4 horizontal edges (top)
        drawLine(new Vec3(minX, maxY, minZ), new Vec3(maxX, maxY, minZ));
        drawLine(new Vec3(minX, maxY, minZ), new Vec3(minX, maxY, maxZ));
        drawLine(new Vec3(maxX, maxY, maxZ), new Vec3(minX, maxY, maxZ));
        drawLine(new Vec3(maxX, maxY, minZ), new Vec3(maxX, maxY, maxZ));
    }

    private void drawLine(Vec3 start, Vec3 end) {
        Vec3 direction = end.subtract(start).normalize().scale(0.2);
        Vec3 current = start;
        double totalDist = start.distanceTo(end);

        while (current.distanceTo(start) < totalDist) {
            level.addParticle(ParticleTypes.GLOW, current.x(), current.y(), current.z(), 0, 0, 0);
            current = current.add(direction);
        }
        level.addParticle(ParticleTypes.GLOW, end.x(), end.y(), end.z(), 0, 0, 0);
    }

}
