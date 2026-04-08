package com.t2pellet.discover.config;

import com.t2pellet.discover.client.render.TextRenderer;
import me.fzzyhmstrs.fzzy_config.util.Walkable;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedColor;

import java.awt.*;

public class TitleConfiguration implements Walkable {
    public int fadeInTicks;
    public int displayTicks;
    public int fadeOutTicks;
    public int timeOffsetTicks;
    public int xOffset;
    public int yOffset;
    public float scale;
    public ValidatedColor colour;
    public TextRenderer.Anchor alignText;
    public TextRenderer.Anchor anchor;

    public TitleConfiguration(int fadeInTicks, int displayTicks, int fadeOutTicks, int timeOffsetTicks, int xOffset, int yOffset, float scale, int colour, TextRenderer.Anchor alignText, TextRenderer.Anchor anchor) {
        this.fadeInTicks = fadeInTicks;
        this.displayTicks = displayTicks;
        this.fadeOutTicks = fadeOutTicks;
        this.timeOffsetTicks = timeOffsetTicks;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.scale = scale;
        this.colour = new ValidatedColor(new Color(colour));
        this.alignText = alignText;
        this.anchor = anchor;
    }

    public static class Builder {
        public int fadeInTicks = 20;
        public int displayTicks = 60;
        public int fadeOutTicks = 20;
        public int timeOffsetTicks = 0;
        public int xOffset = 0;
        public int yOffset = 0;
        public float scale = 1.0F;
        public int colour = 0xFFFFFF;
        public TextRenderer.Anchor alignText = TextRenderer.Anchor.CENTER;
        public TextRenderer.Anchor anchor = TextRenderer.Anchor.CENTER;

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

        public Builder timeOffsetTicks(int timeOffsetTicks) {
            this.timeOffsetTicks = timeOffsetTicks;
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

        public Builder alignText(TextRenderer.Anchor alignText) {
            this.alignText = alignText;
            return this;
        }

        public Builder colour(int colour) {
            this.colour = colour;
            return this;
        }

        public Builder anchor(TextRenderer.Anchor anchor) {
            this.anchor = anchor;
            return this;
        }

        public TitleConfiguration build() {
            return new TitleConfiguration(
                    fadeInTicks, displayTicks, fadeOutTicks, timeOffsetTicks, xOffset, yOffset, scale, colour, alignText, anchor
            );
        }
    }
}
