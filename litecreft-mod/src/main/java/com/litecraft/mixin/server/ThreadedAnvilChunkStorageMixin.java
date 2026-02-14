package com.litecraft.mixin.server;

import com.litecraft.LiteCraftMod;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ThreadedAnvilChunkStorage.class)
public class ThreadedAnvilChunkStorageMixin {

    /**
     * Урезаем отслеживание сущностей на расстоянии.
     * В одиночной игре на T606 — серверная часть тоже ест CPU.
     */
    @Inject(method = "getViewDistance", at = @At("HEAD"), cancellable = true,
            remap = true)
    private void litecraft_reduceTrackingDistance(CallbackInfoReturnable<Integer> cir) {
        if (LiteCraftMod.config != null && LiteCraftMod.aggressiveMode) {
            cir.setReturnValue(LiteCraftMod.dynamicRenderDistance);
        }
    }
}
