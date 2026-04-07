package com.t2pellet.discover.client.render;

import com.t2pellet.discover.DiscoveredTitle;
import com.t2pellet.discover.client.util.DiscoverScheduler;
import com.t2pellet.discover.config.DiscoverConfig;
import dev.architectury.event.events.client.ClientGuiEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.HashMap;
import java.util.Map;

public class TextRenderManager extends DiscoverScheduler implements ClientGuiEvent.RenderHud {

    public static final TextRenderManager INSTANCE = new TextRenderManager();

    private final Map<DiscoveredTitle.Type, TextRenderer> renderers = new HashMap<>();
    private final TextRenderer CREDITS = new TextRenderer(DiscoverConfig.INSTANCE.credits);

    private TextRenderManager() {
        super();
        this.renderers.put(DiscoveredTitle.Type.BIOME, new TextRenderer(DiscoverConfig.INSTANCE.biome));
        this.renderers.put(DiscoveredTitle.Type.DIMENSION, new TextRenderer(DiscoverConfig.INSTANCE.dimension));
        this.renderers.put(DiscoveredTitle.Type.STRUCTURE, new TextRenderer(DiscoverConfig.INSTANCE.structure));
    }

    @Override
    public void tick(Minecraft instance) {
        super.tick(instance);
        CREDITS.tick();
        this.renderers.values().forEach(TextRenderer::tick);
    }

    @Override
    public void renderHud(GuiGraphics graphics, float tickDelta) {
        CREDITS.renderHud(graphics, tickDelta);
        this.renderers.values().forEach(renderer -> renderer.renderHud(graphics, tickDelta));
    }

    public boolean isRendering() {
        return this.renderers.values().stream().anyMatch(TextRenderer::isShowing);
    }

    public boolean isRendering(DiscoveredTitle.Type type) {
        return this.renderers.get(type).isShowing();
    }

    public void render(DiscoveredTitle title) {
        this.renderers.get(title.type()).setTitle(title.getFriendlyName());
        renderCredit(title);
    }

    private void renderCredit(DiscoveredTitle title) {
        if (CREDITS.isShowing()) {
            runInTicks(CREDITS.getShowingTime(), () -> renderCredit(title));
        } else {
            CREDITS.setTitle(title.getFriendlyCredit());
        }
    }
}
