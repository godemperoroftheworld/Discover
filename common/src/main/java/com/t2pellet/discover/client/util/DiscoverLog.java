package com.t2pellet.discover.client.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.t2pellet.discover.config.DiscoverConfig;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.platform.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class DiscoverLog implements ClientPlayerEvent.ClientPlayerJoin, ClientPlayerEvent.ClientPlayerQuit {

    public static final DiscoverLog INSTANCE = new DiscoverLog();

    private final Set<ResourceLocation> locations = new HashSet<>();
    private Path path;

    private DiscoverLog() {
    }

    @Override
    public void join(LocalPlayer player) {
        if (Minecraft.getInstance().getCurrentServer() != null) {
            String sanitizedIP = Minecraft.getInstance().getCurrentServer().ip.replaceAll("[^a-zA-Z0-9.-]", "_");
            this.path = Platform.getGameFolder().resolve("discover").resolve(sanitizedIP);
            load();
        } else if (Minecraft.getInstance().isSingleplayer()) {
            this.path = Minecraft.getInstance().getSingleplayerServer().getWorldPath(LevelResource.ROOT);
            load();
        }
    }

    @Override
    public void quit(@Nullable LocalPlayer player) {
        save();
        this.path = null;
    }

    public void add(ResourceLocation location) {
        locations.add(location);
    }

    public void remove(ResourceLocation location) {
        locations.remove(location);
    }

    public boolean hasVisited(ResourceLocation location) {
        if (DiscoverConfig.INSTANCE.mode.get() == DiscoverConfig.Mode.ALWAYS) {
            return false;
        }
        return locations.contains(location);
    }

    public void save() {
        if (DiscoverConfig.INSTANCE.mode.get() != DiscoverConfig.Mode.ALWAYS) {
            return;
        }
        if (this.path == null) return;

        try {
            Files.createDirectories(path);
            File file = path.resolve("discover.json").toFile();
            FileWriter writer = new FileWriter(file);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject object = new JsonObject();
            JsonArray elements = new JsonArray();
            locations.forEach((l) -> elements.add(l.toString()));
            object.add("locations", elements);
            gson.toJson(object, writer);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void load() {
        if (DiscoverConfig.INSTANCE.mode.get() != DiscoverConfig.Mode.ALWAYS) {
            return;
        }
        if (this.path == null) return;

        try {
            Files.createDirectories(path);
            File file = path.resolve("discover.json").toFile();
            if (!file.exists()) return;
            FileReader reader = new FileReader(file);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            if (json == null) return;
            JsonArray elements = json.getAsJsonArray("locations");
            elements.forEach((l) -> {
                ResourceLocation location = new ResourceLocation(l.getAsString());
                add(location);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
