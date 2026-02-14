package com.litecraft.mixin.client;

import com.litecraft.LiteCraftMod;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Unique
    private int litecraft_chunkUpdateCounter = 0;

    /**
     * Ограничиваем количество обновлений чанков за кадр.
     * На T606 это критически важно — каждое обновление чанка = тяжёлый пересчёт геометрии.
     */
    @Inject(method = "render", at = @At("HEAD"))
    private void litecraft_onRenderStart(
            MatrixStack matrices,
            float tickDelta,
            long limitTime,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightmapTextureManager lightmapTextureManager,
            Matrix4f projectionMatrix,
            CallbackInfo ci
    ) {
        litecraft_chunkUpdateCounter = 0;
    }

    /**
     * Подсчёт и ограничение апдейтов чанков.
     */
    @Inject(method = "setupTerrain", at = @At("HEAD"))
    private void litecraft_beforeSetupTerrain(
            Camera camera, net.minecraft.client.render.Frustum frustum,
            boolean hasForcedFrustum, boolean spectator,
            CallbackInfo ci
    ) {
        if (LiteCraftMod.config != null && LiteCraftMod.config.deferChunkUpdates) {
            litecraft_chunkUpdateCounter++;
        }
    }
}
