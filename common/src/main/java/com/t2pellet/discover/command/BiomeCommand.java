package com.t2pellet.discover.command;

import com.mojang.brigadier.context.CommandContext;
import com.t2pellet.discover.title.LocationGameTitle;
import com.t2pellet.discover.title.LocationTitle;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public class BiomeCommand extends BaseCommand {

    public static final BiomeCommand INSTANCE = new BiomeCommand();

    private BiomeCommand() {
        super("biome", 2);
    }

    @Override
    protected int execute(CommandContext<CommandSourceStack> context) {
        try {
            BlockPos pos = BlockPos.containing(context.getSource().getPosition());
            Holder<Biome> biome = context.getSource().getLevel().getBiome(pos);
            ResourceLocation location = biome.unwrapKey().map(ResourceKey::location).orElseThrow();
            LocationTitle title = new LocationGameTitle(LocationTitle.Type.BIOME, location);
            context.getSource().sendSuccess(() -> renderTitle(title, null), false);
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal(e.getMessage()));
            return 0;
        }
        return 1;
    }
}
