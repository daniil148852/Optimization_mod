package com.litecraft.mixin.client;

import com.litecraft.LiteCraftMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRendererMixin {

    @Unique
    private int litecraft_renderedEntities = 0;

    @Unique
    private long litecraft_lastResetFrame = 0;

    /**
     * Каллим сущности по дистанции и лимиту.
     * На T606 рендеринг каждой сущности = ощутимая нагрузка.
     */
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private <E extends Entity> void litecraft_cullEntity(
            E entity, double x, double y, double z,
            float yaw, float tickDelta,
            MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
            CallbackInfo ci
    ) {
        if (LiteCraftMod.config == null || !LiteCraftMod.config.cullingEntities) return;

        // Не каллим самого игрока
        MinecraftClient client = MinecraftClient.getInstance();
        if (entity == client.player) return;

        // Сброс счётчика каждый кадр
        long frameCount = client.world != null ? client.world.getTime() : 0;
        if (frameCount != litecraft_lastResetFrame) {
            litecraft_renderedEntities = 0;
            litecraft_lastResetFrame = frameCount;
        }

        // Отключаем рамки предметов
        if (LiteCraftMod.config.disableItemFrameRendering && entity instanceof ItemFrameEntity) {
            ci.cancel();
            return;
        }

        // Отключаем картины
        if (LiteCraftMod.config.disablePaintingRendering && entity instanceof PaintingEntity) {
            ci.cancel();
            return;
        }

        // Дистанционный каллинг
        if (client.player != null) {
            double distSq = entity.squaredDistanceTo(client.player);
            double maxDist = 48.0 * LiteCraftMod.config.entityRenderDistanceFactor;

            // Не-игроки каллятся раньше
            if (!(entity instanceof PlayerEntity) && distSq > maxDist * maxDist) {
                ci.cancel();
                return;
            }

            // В агрессивном режиме — ещё жёстче
            if (LiteCraftMod.aggressiveMode && !(entity instanceof PlayerEntity)) {
                maxDist *= 0.5;
                if (distSq > maxDist * maxDist) {
                    ci.cancel();
                    return;
                }
            }
        }

        // Лимит общего количества рендерящихся сущностей
        if (litecraft_renderedEntities >= LiteCraftMod.config.maxRenderedEntities) {
            // Но игроков всегда рендерим
            if (!(entity instanceof PlayerEntity)) {
                ci.cancel();
                return;
            }
        }

        litecraft_renderedEntities++;
    }
}
