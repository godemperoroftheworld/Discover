package com.t2pellet.discover.client.render.boundary;

import com.t2pellet.discover.client.util.DiscoverScheduler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.HashSet;
import java.util.Set;

public class BoundaryRenderManager extends DiscoverScheduler<Minecraft> {

    public static final BoundaryRenderManager INSTANCE = new BoundaryRenderManager();

    private static final int RENDER_FREQUENCY = 10;
    private static final int RENDER_TIME = 240;

    private final Set<BoundaryRenderer> renderers = new HashSet<>();

    private BoundaryRenderManager() {
    }

    public void render(BoundingBox box) {
        BoundaryRenderer renderer = new BoundaryRenderer(box);
        renderers.add(renderer);
        runInTicks(RENDER_TIME, () -> renderers.remove(renderer));
    }

    @Override
    public void tick(Minecraft instance) {
        super.tick(instance);
        if (this.currentTick % RENDER_FREQUENCY == 0) {
            this.renderers.forEach(BoundaryRenderer::draw);
        }
    }
}
