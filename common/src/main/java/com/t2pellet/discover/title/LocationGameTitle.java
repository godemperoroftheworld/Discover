package com.t2pellet.discover.title;

import com.t2pellet.discover.DiscoverTitles;
import com.t2pellet.discover.util.StringUtil;
import dev.architectury.platform.Platform;
import net.minecraft.Util;
import net.minecraft.locale.Language;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import static com.t2pellet.discover.DiscoverTitles.TRAVELER_TITLE_COMPAT_ID;

public class LocationGameTitle implements LocationTitle {

    public final ResourceLocation location;
    private final LocationTitle.Type type;

    public LocationGameTitle(LocationTitle.Type type, ResourceLocation location) {
        this.type = type;
        this.location = location;
    }

    public LocationGameTitle(Type type, FriendlyByteBuf buf) {
        this.type = type;
        this.location = buf.readResourceLocation();
    }

    @Override
    public Type type() {
        return this.type;
    }

    @Override
    public String title() {
        // Try first with override of format `discover.${type}.${namespace}.${path}`
        String prefix = DiscoverTitles.MOD_ID + "." + this.type.name;
        String key = Util.makeDescriptionId(prefix, this.location);
        if (Language.getInstance().has(key)) {
            return Language.getInstance().getOrDefault(key);
        }
        // Then try with compat format `travelerstitles.${type}.${namespace}.${path}`
        String compatPrefix = this.type == LocationTitle.Type.DIMENSION ? TRAVELER_TITLE_COMPAT_ID : TRAVELER_TITLE_COMPAT_ID + "." + this.type.name;
        String compatKey = Util.makeDescriptionId(compatPrefix, this.location);
        return Language.getInstance().getOrDefault(compatKey, this.getLocaleName());
    }

    @Override
    public String credit() {
        String key = "discover.type." + this.type.name;
        String localizedType = Language.getInstance().getOrDefault(key, StringUtil.capitalize(this.type.name));
        String localizedBy = Language.getInstance().getOrDefault("book.byAuthor", "by %1$s");
        String modName = Platform.isModLoaded(this.location.getNamespace()) ? Platform.getMod(this.location.getNamespace()).getName() : StringUtil.getFriendlyNamespace(this.location);
        String formattedBy = String.format(localizedBy, modName);
        return localizedType + " " + formattedBy;
    }

    @Override
    public @Nullable Integer colour() {
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

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(this.type);
        buf.writeResourceLocation(this.location);
    }

    private String getLocaleName() {
        // Used when overrides are not present, the default description ID, falling back to a friendl-i-fied version of the ResourecLocation
        String backupName = StringUtil.getFriendlyPath(this.location);
        String key = Util.makeDescriptionId(this.type.name, this.location);
        return Language.getInstance().getOrDefault(key, backupName);
    }
}
