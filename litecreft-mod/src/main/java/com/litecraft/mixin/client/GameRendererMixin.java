package com.litecraft.mixin.client;

import com.litecraft.LiteCraftMod;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    /**
     * Отключаем пост-обработку (шейдерные эффекты вроде подводного размытия, Nausea и т.д.)
     * На T606 с Mali-G57 это критично.
     */
    @Inject(method = "loadPostProcessor", at = @At("HEAD"), cancellable = true)
    private void litecraft_disablePostProcessing(
            net.minecraft.util.Identifier id, CallbackInfo ci
    ) {
        if (LiteCraftMod.aggressiveMode) {
            ci.cancel();
        }
    }

    /**
     * Ограничиваем дистанцию рендера руки/предметов для экономии.
     */
    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void litecraft_disableBobbing(
            net.minecraft.client.util.math.MatrixStack matrices, float tickDelta,
            CallbackInfo ci
    ) {
        if (LiteCraftMod.reduceAnimations) {
            ci.cancel();
        }
    }
}
