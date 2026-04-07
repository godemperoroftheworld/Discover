package com.t2pellet.discover.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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

    public static class Builder {
        private int fadeInTicks = 20;
        private int displayTicks = 60;
        private int fadeOutTicks = 20;
        private int xOffset = 0;
        private int yOffset = 0;
        private float scale = 1.0F;
        private boolean centerText = true;
        private int colour = 0xFFFFFF;
        private Anchor anchor = Anchor.CENTER;

        public Builder fadeInTicks(int fadeInTicks) {
            this.fadeInTicks = fadeInTicks;
            return this;
        }

        public Builder displayTicks(int displayTicks) {
            this.displayTicks = displayTicks;
            return this;
        }

        public Builder fadeOutTicks(int fadeOutTicks) {
            this.fadeOutTicks = fadeOutTicks;
            return this;
        }

        public Builder xOffset(int xOffset) {
            this.xOffset = xOffset;
            return this;
        }

        public Builder yOffset(int yOffset) {
            this.yOffset = yOffset;
            return this;
        }

        public Builder scale(float scale) {
            this.scale = scale;
            return this;
        }

        public Builder centerText(boolean centerText) {
            this.centerText = centerText;
            return this;
        }

        public Builder colour(int colour) {
            this.colour = colour;
            return this;
        }

        public Builder anchor(Anchor anchor) {
            this.anchor = anchor;
            return this;
        }

        public TextRenderer build() {
            return new TextRenderer(fadeInTicks, displayTicks, fadeOutTicks, xOffset, yOffset, scale, centerText, colour, anchor);
        }
    }

    public final int fadeInTicks;
    public final int displayTicks;
    public final int fadeOutTicks;
    public final int totalTicks;
    public final int xOffset;
    public final int yOffset;
    public final float scale;
    public final boolean centerText;
    public final int colour;
    public final Anchor anchor;

    private String title;
    private int titleTime;

    public TextRenderer(int fadeInTicks, int displayTicks, int fadeOutTicks, int xOffset, int yOffset, float scale, boolean centerText, int colour, Anchor anchor) {
        this.fadeInTicks = fadeInTicks;
        this.displayTicks = displayTicks;
        this.fadeOutTicks = fadeOutTicks;
        this.totalTicks = fadeInTicks + fadeOutTicks + displayTicks;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.scale = scale;
        this.centerText = centerText;
        this.colour = colour;
        this.anchor = anchor;
    }

    public void setTitle(String title) {
        this.title = title;
        this.titleTime = this.totalTicks;
    }

    public boolean isShowing() {
        return this.title != null;
    }

    public void tick() {
        if (this.titleTime > 0) {
            --this.titleTime;
        } else if (this.titleTime == 0) {
            this.clearTitle();
        }
    }

    public void clearTitle() {
        this.titleTime = -1;
        this.title = null;
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
        poseStack.scale(this.scale, this.scale, 0);
        int correctedXOffset = (int) (this.xOffset / scale);
        int correctedYOffset = (int) (this.yOffset / scale);
        int centerTextXOffset = -1 * (this.centerText ? font.width(this.title) : 0) / 2;
        int centerTextYOffset = -1 * (this.centerText ? font.lineHeight : 0);
        int colorWithAlpha = this.alphaToColour(alpha, this.colour);
        graphics.drawString(font, this.title, correctedXOffset + centerTextXOffset, correctedYOffset + centerTextYOffset, colorWithAlpha);

        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private int getAnchorY() {
        return switch (this.anchor) {
            case TOP_CENTER, TOP_LEFT, TOP_RIGHT -> 0;
            case CENTER, CENTER_LEFT, CENTER_RIGHT -> Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2;
            default -> Minecraft.getInstance().getWindow().getGuiScaledHeight();
        };
    }

    private int getAnchorX() {
        return switch (this.anchor) {
            case TOP_LEFT, CENTER_LEFT, BOTTOM_LEFT -> 0;
            case TOP_CENTER, CENTER, BOTTOM_CENTER -> Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2;
            default -> Minecraft.getInstance().getWindow().getGuiScaledWidth();
        };
    }

    private int getAlpha(float partialTicks) {
        return this.getAlpha(partialTicks, 0);
    }

    private int getAlpha(float partialTicks, int offset) {
        float timeWithPartial = this.titleTime - partialTicks;
        // Shrink the window by offset on both sides
        float shiftedTime = timeWithPartial - offset;
        float shiftedTotal = this.totalTicks - offset * 2;

        float alpha = 1.0F;
        if (shiftedTime < this.fadeOutTicks) {
            alpha = shiftedTime / this.fadeOutTicks;
        } else if (shiftedTime > shiftedTotal - this.fadeInTicks) {
            float timeForFade = shiftedTime - (shiftedTotal - this.fadeInTicks);
            alpha = 1.0F - timeForFade / this.fadeInTicks;
        }
        return Mth.clamp(Math.round(255 * alpha), 0, 255);
    }

    private int alphaToColour(int alpha, int colour) {
        return (alpha << 24) | colour;
    }
}
