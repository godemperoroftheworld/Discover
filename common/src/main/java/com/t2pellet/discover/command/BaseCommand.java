package com.t2pellet.discover.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.t2pellet.discover.title.LocationTitle;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class BaseCommand {
    private final String path;
    private final int permission;

    protected BaseCommand(String path, int permission) {
        this.path = path;
        this.permission = permission;
    }

    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(this.path)
                .requires(s -> s.hasPermission(this.permission))
                .executes(this::execute);

        addSubcommands(builder);

        dispatcher.register(builder);
    }

    protected void addSubcommands(LiteralArgumentBuilder<CommandSourceStack> builder) {
    }

    ;

    protected abstract int execute(CommandContext<CommandSourceStack> context);

    protected MutableComponent renderTitle(LocationTitle title, @Nullable BlockPos pos) {
        return renderTitle(title, pos, null);
    }

    protected MutableComponent renderTitle(LocationTitle title, @Nullable BlockPos pos, @Nullable UUID structureId) {
        int colour = title.colour() != null ? title.colour() : 0xFFFFFF;

        MutableComponent nameComponent = Component.literal(title.title())
                .withStyle(s -> s.withColor(colour).withBold(true));

        if (structureId != null) {
            nameComponent.withStyle(s -> s
                    .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, structureId.toString()))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            Component.translatable("discover.command.copy", title.title()))));
        }

        MutableComponent header = Component.literal("» ")
                .withStyle(s -> s.withColor(0x555555))
                .append(nameComponent);

        if (structureId != null) {
            MutableComponent delete = Component.literal(" [X]")
                    .withStyle(s -> s
                            .withColor(0xFF5555)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/structure removeBoundary " + structureId))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    Component.translatable("discover.command.delete", title.title()))));
            header.append(delete);
        }

        MutableComponent creditLine = Component.literal("\n  " + title.credit())
                .withStyle(s -> s.withColor(0xAAAAAA).withItalic(true));

        header.append(creditLine);

        if (pos != null) {
            MutableComponent posLine = Component.literal("\n  ")
                    .append(Component.literal(pos.getX() + ", " + pos.getY() + ", " + pos.getZ())
                            .withStyle(s -> s.withColor(0x00CCFF)
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                                            "/tp @s " + pos.getX() + " " + pos.getY() + " " + pos.getZ()))
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                            Component.translatable("discover.command.teleport", title.title())))));
            header.append(posLine);
        }

        return header;
    }
}