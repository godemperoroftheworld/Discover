package com.t2pellet.discover.util;

import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StringUtil {

    private StringUtil() {
    }

    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String getFriendlyPath(ResourceLocation location) {
        String path = location.getPath();
        return getFriendlyString(path);
    }

    public static String getFriendlyNamespace(ResourceLocation location) {
        String namespace = location.getNamespace();
        return getFriendlyString(namespace);
    }

    private static String getFriendlyString(String str) {
        return Arrays.stream(str.split("_"))
                .map(StringUtil::capitalize)
                .collect(Collectors.joining(" "));
    }
}
