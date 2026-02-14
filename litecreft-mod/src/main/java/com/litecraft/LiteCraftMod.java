package com.litecraft;

import com.litecraft.config.LiteCraftConfig;
import com.litecraft.util.PerformanceMonitor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiteCraftMod implements ClientModInitializer {

    public static final String MOD_ID = "litecraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static LiteCraftConfig config;
    public static PerformanceMonitor performanceMonitor;

    // Динамическое управление качеством
    public static volatile int dynamicRenderDistance = 4;
    public static volatile boolean aggressiveMode = false;
    public static volatile int currentFps = 60;
    public static volatile boolean reduceAnimations = false;

    @Override
    public void onInitializeClient() {
        LOGGER.info("[LiteCraft] Initializing LiteCraft Optimizer v1.0.0");
        LOGGER.info("[LiteCraft] Designed for low-end devices (Unisoc T606 target)");

        config = new LiteCraftConfig();
        config.load();

        performanceMonitor = new PerformanceMonitor();

        // Динамическая адаптация каждый тик
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null) return;

            performanceMonitor.tick();
            currentFps = performanceMonitor.getAverageFps();

            // Динамическая подстройка под производительность
            if (currentFps < 15) {
                aggressiveMode = true;
                dynamicRenderDistance = Math.max(2, config.minRenderDistance);
                reduceAnimations = true;
            } else if (currentFps < 25) {
                aggressiveMode = true;
                dynamicRenderDistance = Math.max(3, config.minRenderDistance);
                reduceAnimations = true;
            } else if (currentFps < 40) {
                aggressiveMode = false;
                dynamicRenderDistance = Math.min(config.maxRenderDistance, 5);
                reduceAnimations = false;
            } else {
                aggressiveMode = false;
                dynamicRenderDistance = config.maxRenderDistance;
                reduceAnimations = false;
            }

            // Применяем динамическую дистанцию рендера
            if (config.dynamicRenderDistance && client.options != null) {
                int currentViewDist = client.options.getViewDistance().getValue();
                if (currentViewDist != dynamicRenderDistance) {
                    client.options.getViewDistance().setValue(dynamicRenderDistance);
                }
            }
        });

        LOGGER.info("[LiteCraft] Initialization complete. Happy gaming!");
    }
}
