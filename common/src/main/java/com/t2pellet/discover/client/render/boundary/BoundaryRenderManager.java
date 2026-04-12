package com.t2pellet.discover.client.render.boundary;

import com.t2pellet.discover.client.util.ClientScheduler;
import com.t2pellet.discover.config.DiscoverConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.HashSet;
import java.util.Set;

public class BoundaryRenderManager extends ClientScheduler {

    public static final BoundaryRenderManager INSTANCE = new BoundaryRenderManager();

    private static final int RENDER_FREQUENCY = 10;

    private final Set<BoundaryRenderer> renderers = new HashSet<>();

    private BoundaryRenderManager() {
    }

    public void render(BoundingBox box) {
        this.render(box, DiscoverConfig.INSTANCE.renderTime.get());
    }

    public void renderForever(BoundingBox box) {
        BoundaryRenderer renderer = new BoundaryRenderer(box);
        renderers.add(renderer);
    }

    public void render(BoundingBox box, int ticks) {
        BoundaryRenderer renderer = new BoundaryRenderer(box);
        renderers.add(renderer);
        runInTicks(ticks, () -> {
            renderers.remove(renderer);
        });
    }

    public void clear() {
        renderers.clear();
    }

    @Override
    public void tick(Minecraft instance) {
        super.tick(instance);
        if (this.currentTick % RENDER_FREQUENCY == 0) {
            this.renderers.forEach(BoundaryRenderer::draw);
        }
    }
}
