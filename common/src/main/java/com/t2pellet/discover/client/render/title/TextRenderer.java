package com.t2pellet.discover.client.render.title;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.t2pellet.discover.config.TitleConfiguration;
import dev.architectury.event.events.client.ClientGuiEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public class TextRenderer implements ClientGuiEvent.RenderHud {
    public enum Anchor {
        TOP_LEFT,
        TOP_CENTER,
        TOP_RIGHT,
        CENTER_LEFT,
        CENTER,
        CENTER_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_CENTER,
        BOTTOM_RIGHT
    }

    public final TitleConfiguration config;
    private final int totalTicks;
    private String title;
    private int titleTime;
    private int titleColour;

    public TextRenderer(TitleConfiguration config) {
        this.config = config;
        this.totalTicks = config.fadeInTicks + config.displayTicks + config.fadeOutTicks;
        resetColour();
    }

    public boolean isEnabled() {
        return config.enabled.get();
    }

    public void setColour(int colour) {
        this.titleColour = colour & 0xFFFFFF;
    }

    public void resetColour() {
        setColour(config.colour.toInt());
    }

    public void showTitle(String title) {
        this.title = title;
        this.titleTime = this.totalTicks;
    }

    public void tick() {
        if (this.titleTime > 0) {
            --this.titleTime;
        } else if (this.titleTime == 0) {
            this.clearTitle();
        }
    }

    public boolean isShowing() {
        return this.title != null && this.titleTime > 0;
    }

    public int getShowingTime() {
        return this.titleTime;
    }

    public void clearTitle() {
        this.title = null;
        this.titleTime = -1;
    }

    @Override
    public void renderHud(GuiGraphics graphics, float tickDelta) {
        // Early return
        if (this.title == null || this.titleTime < 0) return;

        // Calculate alpha
        Font font = Minecraft.getInstance().font;
        int alpha = this.getAlpha(tickDelta);

        // Early return, prevent flicker when alpha < 4
        if (alpha < 4) {
            return;
        }

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Things
        poseStack.translate(this.getAnchorX(), this.getAnchorY(), 0);
        poseStack.scale(config.scale, config.scale, 0);
        int correctedXOffset = (int) (config.xOffset / config.scale);
        int correctedYOffset = (int) (config.yOffset / config.scale);
        int colorWithAlpha = this.alphaToColour(alpha, this.titleColour);
        graphics.drawString(font, this.title, correctedXOffset + this.getTextOffsetX(), correctedYOffset + this.getTextOffsetY(), colorWithAlpha, config.shadow.get());

        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private int getTextOffsetY() {
        return switch (config.alignText) {
            case TOP_CENTER, TOP_LEFT, TOP_RIGHT -> 0;
            case CENTER, CENTER_LEFT, CENTER_RIGHT -> -1 * Minecraft.getInstance().font.lineHeight / 2;
            default -> -1 * Minecraft.getInstance().font.lineHeight;
        };
    }

    private int getTextOffsetX() {
        return switch (config.alignText) {
            case TOP_LEFT, CENTER_LEFT, BOTTOM_LEFT -> 0;
            case TOP_CENTER, CENTER, BOTTOM_CENTER -> -1 * Minecraft.getInstance().font.width(this.title) / 2;
            default -> -1 * Minecraft.getInstance().font.width(this.title);
        };
    }

    private int getAnchorY() {
        return switch (config.anchor) {
            case TOP_CENTER, TOP_LEFT, TOP_RIGHT -> 0;
            case CENTER, CENTER_LEFT, CENTER_RIGHT -> Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2;
            default -> Minecraft.getInstance().getWindow().getGuiScaledHeight();
        };
    }

    private int getAnchorX() {
        return switch (config.anchor) {
            case TOP_LEFT, CENTER_LEFT, BOTTOM_LEFT -> 0;
            case TOP_CENTER, CENTER, BOTTOM_CENTER -> Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2;
            default -> Minecraft.getInstance().getWindow().getGuiScaledWidth();
        };
    }

    private int getAlpha(float partialTicks) {
        float timeWithPartial = this.titleTime - partialTicks;
        // Shrink the window by offset on both sides
        float shiftedTime = timeWithPartial - config.timeOffsetTicks;
        float shiftedTotal = this.totalTicks - config.timeOffsetTicks * 2;

        float alpha = 1.0F;
        if (shiftedTime < config.fadeOutTicks) {
            alpha = shiftedTime / config.fadeOutTicks;
        } else if (shiftedTime > shiftedTotal - config.fadeInTicks) {
            float timeForFade = shiftedTime - (shiftedTotal - config.fadeInTicks);
            alpha = 1.0F - timeForFade / config.fadeInTicks;
        }
        return Mth.clamp(Math.round(255 * alpha), 0, 255);
    }

    private int alphaToColour(int alpha, int colour) {
        return (alpha << 24) | colour;
    }
}
