package com.t2pellet.discover.mixin;

import com.t2pellet.discover.client.render.boundary.BoundaryRenderManager;
import com.t2pellet.discover.structure.PlayerStructure;
import com.t2pellet.discover.structure.StructureBuilder;
import com.t2pellet.discover.util.SignUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(AbstractSignEditScreen.class)
public class SignScreenMixin extends Screen {

    @Unique
    private static final Component DISCOVER$CREATE_COMPONENT = Component.translatable("discover.boundary.check");

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
        String name = SignUtil.getFirstText(sign).orElse("");
        Player player = Minecraft.getInstance().player;
        Optional<PlayerStructure> structure = new StructureBuilder(name, player, pos).search();
        structure.ifPresentOrElse((struct) -> {
            BoundaryRenderManager.INSTANCE.render(struct.box.inflatedBy(2));
        }, () -> {
            player.displayClientMessage(Component.translatable("discover.boundary.error"), true);
        });
        this.onClose();
    }
}
