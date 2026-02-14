package com.litecraft.config;

import com.litecraft.LiteCraftMod;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public class LiteCraftConfig {

    private static final Path CONFIG_PATH = Paths.get("config", "litecraft.properties");

    // Рендер
    public boolean dynamicRenderDistance = true;
    public int minRenderDistance = 2;
    public int maxRenderDistance = 6;

    // Частицы
    public boolean reduceParticles = true;
    public int maxParticleCount = 50;           // ваниль ~1000+
    public boolean disableBlockBreakParticles = true;

    // Сущности
    public boolean cullingEntities = true;
    public double entityRenderDistanceFactor = 0.5;  // 50% от ванильной
    public int maxRenderedEntities = 24;

    // Освещение
    public boolean simplifiedLighting = true;
    public int lightUpdateBudgetMs = 2;         // максимум мс на обновление света за тик

    // Анимации
    public boolean disableMapRendering = true;
    public boolean disableItemFrameRendering = false;
    public boolean disablePaintingRendering = false;
    public boolean reduceBiomeBlending = true;
    public int biomeBlendRadius = 0;            // ваниль = 2..7, мы ставим 0

    // Чанки
    public boolean deferChunkUpdates = true;
    public int maxChunkUpdatesPerFrame = 1;     // ваниль = 5+

    // Память
    public boolean aggressiveGC = false;
    public int gcIntervalSeconds = 120;

    public void load() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                save();
                return;
            }

            Properties props = new Properties();
            try (InputStream is = Files.newInputStream(CONFIG_PATH)) {
                props.load(is);
            }

            dynamicRenderDistance = getBool(props, "dynamicRenderDistance", dynamicRenderDistance);
            minRenderDistance = getInt(props, "minRenderDistance", minRenderDistance);
            maxRenderDistance = getInt(props, "maxRenderDistance", maxRenderDistance);
            reduceParticles = getBool(props, "reduceParticles", reduceParticles);
            maxParticleCount = getInt(props, "maxParticleCount", maxParticleCount);
            disableBlockBreakParticles = getBool(props, "disableBlockBreakParticles", disableBlockBreakParticles);
            cullingEntities = getBool(props, "cullingEntities", cullingEntities);
            entityRenderDistanceFactor = getDouble(props, "entityRenderDistanceFactor", entityRenderDistanceFactor);
            maxRenderedEntities = getInt(props, "maxRenderedEntities", maxRenderedEntities);
            simplifiedLighting = getBool(props, "simplifiedLighting", simplifiedLighting);
            lightUpdateBudgetMs = getInt(props, "lightUpdateBudgetMs", lightUpdateBudgetMs);
            disableMapRendering = getBool(props, "disableMapRendering", disableMapRendering);
            disableItemFrameRendering = getBool(props, "disableItemFrameRendering", disableItemFrameRendering);
            disablePaintingRendering = getBool(props, "disablePaintingRendering", disablePaintingRendering);
            reduceBiomeBlending = getBool(props, "reduceBiomeBlending", reduceBiomeBlending);
            biomeBlendRadius = getInt(props, "biomeBlendRadius", biomeBlendRadius);
            deferChunkUpdates = getBool(props, "deferChunkUpdates", deferChunkUpdates);
            maxChunkUpdatesPerFrame = getInt(props, "maxChunkUpdatesPerFrame", maxChunkUpdatesPerFrame);
            aggressiveGC = getBool(props, "aggressiveGC", aggressiveGC);
            gcIntervalSeconds = getInt(props, "gcIntervalSeconds", gcIntervalSeconds);

            LiteCraftMod.LOGGER.info("[LiteCraft] Config loaded.");
        } catch (Exception e) {
            LiteCraftMod.LOGGER.error("[LiteCraft] Failed to load config, using defaults.", e);
        }
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Properties props = new Properties();

            props.setProperty("dynamicRenderDistance", String.valueOf(dynamicRenderDistance));
            props.setProperty("minRenderDistance", String.valueOf(minRenderDistance));
            props.setProperty("maxRenderDistance", String.valueOf(maxRenderDistance));
            props.setProperty("reduceParticles", String.valueOf(reduceParticles));
            props.setProperty("maxParticleCount", String.valueOf(maxParticleCount));
            props.setProperty("disableBlockBreakParticles", String.valueOf(disableBlockBreakParticles));
            props.setProperty("cullingEntities", String.valueOf(cullingEntities));
            props.setProperty("entityRenderDistanceFactor", String.valueOf(entityRenderDistanceFactor));
            props.setProperty("maxRenderedEntities", String.valueOf(maxRenderedEntities));
            props.setProperty("simplifiedLighting", String.valueOf(simplifiedLighting));
            props.setProperty("lightUpdateBudgetMs", String.valueOf(lightUpdateBudgetMs));
            props.setProperty("disableMapRendering", String.valueOf(disableMapRendering));
            props.setProperty("disableItemFrameRendering", String.valueOf(disableItemFrameRendering));
            props.setProperty("disablePaintingRendering", String.valueOf(disablePaintingRendering));
            props.setProperty("reduceBiomeBlending", String.valueOf(reduceBiomeBlending));
            props.setProperty("biomeBlendRadius", String.valueOf(biomeBlendRadius));
            props.setProperty("deferChunkUpdates", String.valueOf(deferChunkUpdates));
            props.setProperty("maxChunkUpdatesPerFrame", String.valueOf(maxChunkUpdatesPerFrame));
            props.setProperty("aggressiveGC", String.valueOf(aggressiveGC));
            props.setProperty("gcIntervalSeconds", String.valueOf(gcIntervalSeconds));

            try (OutputStream os = Files.newOutputStream(CONFIG_PATH)) {
                props.store(os, "LiteCraft Optimizer Configuration");
            }

            LiteCraftMod.LOGGER.info("[LiteCraft] Config saved.");
        } catch (Exception e) {
            LiteCraftMod.LOGGER.error("[LiteCraft] Failed to save config.", e);
        }
    }

    private boolean getBool(Properties p, String key, boolean def) {
        return Boolean.parseBoolean(p.getProperty(key, String.valueOf(def)));
    }

    private int getInt(Properties p, String key, int def) {
        try { return Integer.parseInt(p.getProperty(key, String.valueOf(def))); }
        catch (Exception e) { return def; }
    }

    private double getDouble(Properties p, String key, double def) {
        try { return Double.parseDouble(p.getProperty(key, String.valueOf(def))); }
        catch (Exception e) { return def; }
    }
}
