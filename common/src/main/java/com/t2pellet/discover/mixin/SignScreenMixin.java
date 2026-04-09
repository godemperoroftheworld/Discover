package com.t2pellet.discover.mixin;

import com.t2pellet.discover.network.CreateBoundaryMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(AbstractSignEditScreen.class)
public class SignScreenMixin extends Screen {

    @Unique
    private static final Component DISCOVER$CREATE_COMPONENT = Component.translatable("discover.boundary.create");

    @Shadow
    @Final
    private SignBlockEntity sign;

    protected SignScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void onInit(CallbackInfo ci) {
        this.addRenderableWidget(Button.builder(DISCOVER$CREATE_COMPONENT, this::discover$_createBoundary).bounds(this.width / 2 - 100, this.height / 4 + 120, 200, 20).build());
    }

    @Unique
    private void discover$_createBoundary(Button button) {
        BlockPos pos = sign.getBlockPos();
        String name = Arrays.stream(sign.getText(true).getMessages(true))
                .filter(s -> !s.getString().isEmpty())
                .map(Component::getString)
                .findFirst().orElse("");
        if (name.isEmpty()) {
            Minecraft.getInstance().player.displayClientMessage(Component.translatable("discover.boundary.name_required"), true);
        }
        new CreateBoundaryMessage(name, pos).sendToServer();
    }
}
