package com.litecraft.mixin.client;

import com.litecraft.LiteCraftMod;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {

    @Unique
    private int litecraft_updateCounter = 0;

    /**
     * Обновляем lightmap не каждый кадр, а раз в N кадров.
     * Визуально почти незаметно, но на T606 освещение — дорогая операция.
     */
    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void litecraft_throttleLightmapUpdate(float delta, CallbackInfo ci) {
        if (LiteCraftMod.config == null || !LiteCraftMod.config.simplifiedLighting) return;

        litecraft_updateCounter++;

        int interval = LiteCraftMod.aggressiveMode ? 6 : 3;

        if (litecraft_updateCounter % interval != 0) {
            ci.cancel();
        }
    }
}
