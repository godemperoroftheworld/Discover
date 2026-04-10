package com.t2pellet.discover.config;

import me.fzzyhmstrs.fzzy_config.util.AllowableIdentifiers;
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.stream.Stream;

public class ConfigHelpers {

    private ConfigHelpers() {
    }

    public static ValidatedIdentifier forAnyItemOrTag(ResourceLocation defaultLoc) {
        return new ValidatedIdentifier(
                defaultLoc,
                new AllowableIdentifiers(location -> {
                    boolean isItem = BuiltInRegistries.ITEM.containsKey(location);
                    boolean isTag = BuiltInRegistries.ITEM.getTagNames().anyMatch(t -> t.location().equals(location));
                    return isItem || isTag;
                }, () -> {
                    List<ResourceLocation> items = BuiltInRegistries.ITEM.keySet().stream().toList();
                    List<ResourceLocation> tags = BuiltInRegistries.ITEM.getTagNames().map(TagKey::location).toList();
                    return Stream.concat(items.stream(), tags.stream()).toList();
                }, true)
        );
    }

    public static boolean matchesTagOrItem(ValidatedIdentifier identifier, ItemStack stack) {
        ResourceLocation location = stack.getItem().arch$registryName();
        if (location == null) return false;

        ResourceLocation configName = identifier.get();
        if (location.equals(configName)) return true;

        TagKey<Item> configTag = TagKey.create(Registries.ITEM, configName);
        return stack.is(configTag);
    }
}
