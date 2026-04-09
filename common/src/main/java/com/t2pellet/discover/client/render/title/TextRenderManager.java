package com.t2pellet.discover.client.render.title;

import com.t2pellet.discover.client.util.DiscoverLog;
import com.t2pellet.discover.client.util.DiscoverScheduler;
import com.t2pellet.discover.collections.LRUSet;
import com.t2pellet.discover.config.DiscoverConfig;
import com.t2pellet.discover.title.LocationGameTitle;
import com.t2pellet.discover.title.LocationTitle;
import com.t2pellet.discover.util.SoundUtil;
import dev.architectury.event.events.client.ClientGuiEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.HashMap;
import java.util.Map;

import static com.t2pellet.discover.DiscoverTitles.TRAVELER_TITLE_COMPAT_ID;

public class TextRenderManager extends DiscoverScheduler<Minecraft> implements ClientGuiEvent.RenderHud {

    public static final TextRenderManager INSTANCE = new TextRenderManager();

    private final LRUSet<String> recentSet = new LRUSet<>(DiscoverConfig.INSTANCE.cooldownCount.get());

    private final Map<LocationTitle.Type, TextRenderer> renderers = new HashMap<>();
    private final TextRenderer CREDITS = new TextRenderer(DiscoverConfig.INSTANCE.credits);

    private TextRenderManager() {
        super();
        this.renderers.put(LocationTitle.Type.BIOME, new TextRenderer(DiscoverConfig.INSTANCE.biome));
        this.renderers.put(LocationTitle.Type.DIMENSION, new TextRenderer(DiscoverConfig.INSTANCE.dimension));
        this.renderers.put(LocationTitle.Type.STRUCTURE, new TextRenderer(DiscoverConfig.INSTANCE.structure));
        this.renderers.put(LocationTitle.Type.PLAYER, new TextRenderer(DiscoverConfig.INSTANCE.player));
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

    public boolean isRendering(LocationTitle.Type type) {
        return this.renderers.get(type).isShowing();
    }

    public void render(LocationTitle title) {
        TextRenderer renderer = this.renderers.get(title.type());

        // Check blacklist and log for "game" titles (not supported in player titles yet)
        if (title instanceof LocationGameTitle gameTitle) {
            if (renderer.config.blacklist.contains(gameTitle.location)) {
                return;
            }
            if (DiscoverLog.INSTANCE.hasVisited(gameTitle.location)) return;
        }

        if (this.shouldCheckRecency() && recentSet.contains(title.title())) return;

        if (renderer.isEnabled() && !renderer.isShowing()) {
            Integer colour = title.colour();
            if (colour != null) renderer.setColour(colour);
            else renderer.resetColour();
            // Play sound
            if (!this.isRendering()) {
                ResourceLocation sound = renderer.config.sound.get();
                if (SoundUtil.doesSoundExist(sound)) {
                    Minecraft.getInstance().player.playSound(SoundEvent.createVariableRangeEvent(sound));
                } else {
                    ResourceLocation compatSound = new ResourceLocation(TRAVELER_TITLE_COMPAT_ID, title.type().name);
                    if (SoundUtil.doesSoundExist(compatSound)) {
                        Minecraft.getInstance().player.playSound(SoundEvent.createVariableRangeEvent(compatSound));
                    }
                }
            }
            // Show title
            renderer.showTitle(title.title());
            renderCredit(title);
            // Add to log and recent set (for game titles)
            if (title instanceof LocationGameTitle gameTitle) {
                DiscoverLog.INSTANCE.add(gameTitle.location);
            }
            if (this.shouldCheckRecency()) {
                recentSet.add(title.title());
            }
        }
    }

    private void renderCredit(LocationTitle title) {
        if (!CREDITS.isEnabled()) return;
        if (CREDITS.isShowing()) {
            runInTicks(CREDITS.getShowingTime(), () -> renderCredit(title));
        } else {
            CREDITS.showTitle(title.credit());
        }
    }

    private boolean shouldCheckRecency() {
        return DiscoverConfig.INSTANCE.cooldownCount.get() > 0;
    }
}
