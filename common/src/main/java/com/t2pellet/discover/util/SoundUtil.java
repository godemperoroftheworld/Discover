package com.t2pellet.discover.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;

public class SoundUtil {

    private SoundUtil() {
    }

    public static boolean doesSoundExist(ResourceLocation sound) {
        if (sound == null) return false;
        SoundManager manager = Minecraft.getInstance().getSoundManager();
        WeighedSoundEvents location = manager.getSoundEvent(sound);
        return location != null;
    }
}
