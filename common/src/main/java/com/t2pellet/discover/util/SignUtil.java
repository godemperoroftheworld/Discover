package com.t2pellet.discover.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.SignBlockEntity;

import java.util.Arrays;
import java.util.Optional;

public class SignUtil {

    private SignUtil() {
    }

    public static Optional<String> getFirstText(SignBlockEntity sign) {
        return Arrays.stream(sign.getText(true).getMessages(true))
                .map(Component::getString)
                .filter(string -> !string.isEmpty())
                .findFirst();
    }
}
