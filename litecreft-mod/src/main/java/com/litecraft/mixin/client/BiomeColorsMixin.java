package com.litecraft.mixin.client;

import com.litecraft.LiteCraftMod;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.world.BlockRenderView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.ColorResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BiomeColors.class)
public class BiomeColorsMixin {

    /**
     * Биомный блендинг — неожиданно дорогая операция (сэмплирует цвета в радиусе).
     * При radius=0 используется цвет только текущего биома — огромная экономия.
     */
    @Inject(method = "getColor", at = @At("HEAD"), cancellable = true,
            remap = false)
    private static void litecraft_fastBiomeColor(
            BlockRenderView world, BlockPos pos, ColorResolver resolver,
            CallbackInfoReturnable<Integer> cir
    ) {
        if (LiteCraftMod.config == null || !LiteCraftMod.config.reduceBiomeBlending) return;

        if (LiteCraftMod.config.biomeBlendRadius == 0) {
            // Прямой запрос цвета без блендинга
            try {
                double x = pos.getX();
                double z = pos.getZ();
                int color = world.getBiomeFabric(pos).value()
                        .getTemperature() > 0.5f ? 0x7CBD6B : 0x59C93C;
                // Упрощённый вариант — берём цвет напрямую из резолвера
                cir.setReturnValue(resolver.getColor(
                        world.getBiomeFabric(pos).value(), x, z));
            } catch (Exception e) {
                // Fallback — не ломаем игру
            }
        }
    }
}
