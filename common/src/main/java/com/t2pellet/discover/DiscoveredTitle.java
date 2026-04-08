package com.t2pellet.discover;

import com.t2pellet.discover.util.StringUtil;
import dev.architectury.platform.Platform;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;

public record DiscoveredTitle(Type type, ResourceLocation location) {

    public Integer getColour() {
        String key = this.type.name + "." + this.location.getNamespace() + "." + this.location.getPath() + ".color";
        if (Language.getInstance().has(key)) {
            String colorString = Language.getInstance().getOrDefault(key);
            try {
                return Integer.parseInt(colorString, 16);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public String getFriendlyName() {
        String backupName = StringUtil.getFriendlyPath(this.location);
        String key = this.type.name + "." + this.location.getNamespace() + "." + this.location.getPath();
        return Language.getInstance().getOrDefault(key, backupName);
    }

    public String getFriendlyCredit() {
        String key = "discover.type." + this.type.name;
        String localizedType = Language.getInstance().getOrDefault(key, StringUtil.capitalize(this.type.name));
        String localizedBy = Language.getInstance().getOrDefault("book.byAuthor", "by %1$s");
        String modName = Platform.isModLoaded(this.location.getNamespace()) ? Platform.getMod(this.location.getNamespace()).getName() : StringUtil.getFriendlyNamespace(this.location);
        String formattedBy = String.format(localizedBy, modName);
        return localizedType + " " + formattedBy;
    }

    public enum Type {
        BIOME("biome"),
        STRUCTURE("structure"),
        DIMENSION("dimension");

        public final String name;

        Type(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }
}
