package com.t2pellet.discover.client.render;

import com.t2pellet.discover.DiscoveredTitle;
import com.t2pellet.discover.client.util.DiscoverScheduler;
import dev.architectury.event.events.client.ClientGuiEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class TextRenderManager extends DiscoverScheduler implements ClientGuiEvent.RenderHud {

    public static final TextRenderManager INSTANCE = new TextRenderManager();

    private final TextRenderer CREDITS = new TextRenderer.Builder().anchor(TextRenderer.Anchor.BOTTOM_RIGHT)
            .alignText(TextRenderer.Anchor.BOTTOM_RIGHT)
            .colour(0xD3D3D3)
            .timeOffsetTicks(15)
            .fadeInTicks(10)
            .displayTicks(50)
            .xOffset(-2)
            .yOffset(-2)
            .build();
    private final TextRenderer DIMENSION = new TextRenderer.Builder().scale(3.0F).yOffset(-62).build();
    private final TextRenderer BIOME = new TextRenderer.Builder().scale(2.25F).yOffset(-40).build();
    private final TextRenderer STRUCTURE = new TextRenderer.Builder().scale(1.5F).yOffset(-20).build();

    private TextRenderManager() {
        super();
    }

    @Override
    public void tick(Minecraft instance) {
        super.tick(instance);
        CREDITS.tick();
        DIMENSION.tick();
        BIOME.tick();
        STRUCTURE.tick();
    }

    @Override
    public void renderHud(GuiGraphics graphics, float tickDelta) {
        CREDITS.renderHud(graphics, tickDelta);
        DIMENSION.renderHud(graphics, tickDelta);
        BIOME.renderHud(graphics, tickDelta);
        STRUCTURE.renderHud(graphics, tickDelta);
    }

    public boolean isRendering() {
        return BIOME.isShowing() || STRUCTURE.isShowing() || DIMENSION.isShowing();
    }

    public boolean isRendering(DiscoveredTitle.Type type) {
        return switch (type) {
            case STRUCTURE -> STRUCTURE.isShowing();
            case BIOME -> BIOME.isShowing();
            case DIMENSION -> DIMENSION.isShowing();
        };
    }

    public void render(DiscoveredTitle title) {
        switch (title.type) {
            case BIOME -> BIOME.setTitle(title.title);
            case STRUCTURE -> STRUCTURE.setTitle(title.title);
            case DIMENSION -> DIMENSION.setTitle(title.title);
        }
        renderCredit(title);
    }

    private void renderCredit(DiscoveredTitle title) {
        if (CREDITS.isShowing()) {
            runInTicks(CREDITS.getShowingTime() + 10, () -> renderCredit(title));
        } else {
            CREDITS.setTitle(title.type + " by " + title.credit);
        }
    }
}
