package com.t2pellet.discover.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.t2pellet.discover.DiscoverTitles;
import com.t2pellet.discover.network.ClearBoundariesMessage;
import com.t2pellet.discover.network.RenderBoundariesMessage;
import com.t2pellet.discover.structure.PlayerStructure;
import com.t2pellet.discover.structure.PlayerStructures;
import com.t2pellet.discover.title.LocationGameTitle;
import com.t2pellet.discover.title.LocationRawTitle;
import com.t2pellet.discover.title.LocationTitle;
import com.t2pellet.discover.util.StructureUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class StructureCommand extends BaseCommand {

    public static final StructureCommand INSTANCE = new StructureCommand();

    private StructureCommand() {
        super("structure", 2);
    }

    @Override
    protected int execute(CommandContext<CommandSourceStack> context) {
        try {
            BlockPos pos = BlockPos.containing(context.getSource().getPosition());
            ServerLevel level = context.getSource().getLevel();
            SectionPos sectionPos = SectionPos.of(pos);

            Optional<LocationTitle> structure = getPlayerStructure(level, sectionPos).or(() -> getStructure(level, sectionPos));
            if (structure.isEmpty()) {
                context.getSource().sendFailure(Component.translatable("discover.command.no_structure"));
                return 0;
            }

            LocationTitle title = structure.get();
            context.getSource().sendSuccess(() -> renderTitle(title, pos), false);
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal(e.getMessage()));
            return 0;
        }
        return 1;
    }

    @Override
    protected void addSubcommands(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder.then(Commands.literal("listPlayerStructures")
                .executes(this::listBoundaries));

        builder.then(Commands.literal("removePlayerStructure")
                .then(Commands.argument("id", UuidArgument.uuid())
                        .suggests((context, suggestions) -> {
                            PlayerStructures structures = PlayerStructures.get(context.getSource().getLevel());
                            structures.stream().forEach(s -> suggestions.suggest(s.uuid.toString(), Component.literal(s.name)));
                            return suggestions.buildFuture();
                        })
                        .executes(this::removeBoundary)));

        builder.then(Commands.literal("showPlayerStructures")
                .then(Commands.argument("boolean", BoolArgumentType.bool())
                        .executes(this::toggleBoundaryVisibility)));
    }

    private Optional<LocationTitle> getStructure(ServerLevel level, SectionPos sectionPos) {
        Optional<StructureStart> structure = StructureUtil.findStructure(level, sectionPos);
        return structure.map(found -> {
            ResourceLocation location = level.registryAccess().registry(Registries.STRUCTURE).get().getKey(found.getStructure());
            return new LocationGameTitle(LocationTitle.Type.STRUCTURE, location);
        });
    }

    private Optional<LocationTitle> getPlayerStructure(ServerLevel level, SectionPos sectionPos) {
        Optional<PlayerStructure> structure = StructureUtil.findPlayerStructure(level, sectionPos);
        return structure.map(struct -> {
            Player player = DiscoverTitles.currentServer.getPlayerList().getPlayer(struct.player);
            String name = player != null ? player.getName().getString() : Language.getInstance().getOrDefault("discover.unknown_player");
            return new LocationRawTitle(LocationTitle.Type.PLAYER, struct.name, name);
        });
    }

    private int listBoundaries(CommandContext<CommandSourceStack> context) {
        PlayerStructures structures = PlayerStructures.get(context.getSource().getLevel());

        if (structures.isEmpty()) {
            context.getSource().sendSuccess(() -> Component.translatable("discover.command.no_structures"), false);
            return 0;
        }

        MutableComponent header = Component.empty();

        MutableComponent list = Component.translatable("discover.command.list_boundaries")
                .withStyle(style -> style.withColor(0xFFAA00).withBold(true));

        header.append(list);

        MutableComponent result = structures.stream()
                .map(s -> {
                    Player player = s.getServerPlayer();
                    String name = player != null ? player.getName().getString() : Language.getInstance().getOrDefault("discover.unknown_player");
                    LocationTitle title = new LocationRawTitle(LocationTitle.Type.PLAYER, s.name, name);
                    return renderTitle(title, s.box.getCenter(), s.uuid);
                })
                .reduce(Component.empty(), (a, b) -> a.copy().append("\n").append(b));

        context.getSource().sendSuccess(() -> header.append(result), false);

        return structures.size();
    }

    private int removeBoundary(CommandContext<CommandSourceStack> context) {
        UUID id = UuidArgument.getUuid(context, "id");
        PlayerStructures structures = PlayerStructures.get(context.getSource().getLevel());
        if (structures.contains(id)) {
            PlayerStructure s = structures.get(id);
            structures.remove(id);
            context.getSource().sendSuccess(() -> Component.translatable("discover.command.structure_removed", s.name), false);
            return 1;
        } else {
            context.getSource().sendFailure(Component.translatable("discover.command.no_structure"));
            return 0;
        }
    }

    private int toggleBoundaryVisibility(CommandContext<CommandSourceStack> context) {
        boolean visibility = BoolArgumentType.getBool(context, "boolean");
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendFailure(Component.translatable("commands.help.failed"));
            return 0;
        }
        if (visibility) {
            PlayerStructures structures = PlayerStructures.get(context.getSource().getLevel());
            List<BoundingBox> boxes = structures.stream().map(s -> s.box).toList();
            RenderBoundariesMessage message = new RenderBoundariesMessage(0, boxes);
            message.sendTo(player);
        } else {
            ClearBoundariesMessage message = new ClearBoundariesMessage();
            message.sendTo(player);
        }
        context.getSource().sendSuccess(() -> Component.translatable("discover.command.show_boundaries", visibility), false);
        return 1;
    }
}
