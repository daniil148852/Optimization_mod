package com.litecraft.mixin.client;

import com.litecraft.LiteCraftMod;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow
    private static int currentFps;

    @Unique
    private long litecraft_lastGcTime = 0;

    @Unique
    private int litecraft_frameCount = 0;

    /**
     * Лёгкий GC при критической нехватке памяти + подача FPS мониторингу.
     */
    @Inject(method = "render", at = @At("TAIL"))
    private void litecraft_afterRender(boolean tick, CallbackInfo ci) {
        litecraft_frameCount++;

        // Подаём реальный FPS в мод
        LiteCraftMod.currentFps = currentFps;

        // Управление памятью
        if (LiteCraftMod.config != null && LiteCraftMod.config.aggressiveGC) {
            long now = System.currentTimeMillis();
            long interval = LiteCraftMod.config.gcIntervalSeconds * 1000L;

            if (now - litecraft_lastGcTime > interval) {
                Runtime rt = Runtime.getRuntime();
                long used = rt.totalMemory() - rt.freeMemory();
                long max = rt.maxMemory();

                // GC только если использование > 80%
                if ((double) used / max > 0.80) {
                    System.gc();
                    LiteCraftMod.LOGGER.info("[LiteCraft] Triggered GC. Memory: {}MB / {}MB",
                            used / 1024 / 1024, max / 1024 / 1024);
                }
                litecraft_lastGcTime = now;
            }
        }
    }
}
