package com.litecraft.mixin.client;

import com.litecraft.LiteCraftMod;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {

    @Unique
    private int litecraft_particleCount = 0;

    @Unique
    private int litecraft_tickCounter = 0;

    /**
     * Лимитируем добавление частиц.
     * На T606 частицы — одна из главных причин лагов.
     */
    @Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;",
            at = @At("HEAD"), cancellable = true)
    private void litecraft_limitParticles(
            ParticleEffect parameters,
            double x, double y, double z,
            double velocityX, double velocityY, double velocityZ,
            CallbackInfoReturnable<Particle> cir
    ) {
        if (LiteCraftMod.config == null || !LiteCraftMod.config.reduceParticles) return;

        // В агрессивном режиме — отменяем 90% частиц
        if (LiteCraftMod.aggressiveMode) {
            litecraft_particleCount++;
            if (litecraft_particleCount % 10 != 0) {
                cir.setReturnValue(null);
                return;
            }
        }

        // Лимит общего количества
        if (litecraft_particleCount > LiteCraftMod.config.maxParticleCount) {
            cir.setReturnValue(null);
        }

        litecraft_particleCount++;
    }

    /**
     * Сброс счётчика каждый тик.
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void litecraft_resetParticleCount(CallbackInfo ci) {
        litecraft_tickCounter++;
        if (litecraft_tickCounter % 20 == 0) {
            litecraft_particleCount = 0;
        }
    }
}
