package com.t2pellet.discover;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Collectors;

public class DiscoveredTitle {

    public enum Type {
        BIOME("Biome"),
        STRUCTURE("Structure"),
        DIMENSION("Dimension");

        public final String name;
        Type(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

    public static DiscoveredTitle forResourceLocation(Type type, ResourceLocation location) {
        String string = location.toString();
        String[] parts = string.split(":");
        String title = Arrays.stream(parts[1].split("_"))
                .map(w -> w.substring(0, 1).toUpperCase() + w.substring(1))
                .collect(Collectors.joining(" "));
        String credit = parts[0].substring(0, 1).toUpperCase() + parts[0].substring(1);

        return new DiscoveredTitle(type, title, credit);
    }

    public final Type type;
    public final String title;
    @Nullable public final String credit;

    public DiscoveredTitle(Type type, String title) {
        this(type, title, null);
    }

    public DiscoveredTitle(Type type, String title, @Nullable String credit) {
        this.type = type;
        this.title = title;
        this.credit = credit;
    }
}
