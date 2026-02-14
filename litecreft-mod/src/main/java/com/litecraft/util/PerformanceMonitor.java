package com.litecraft.util;

public class PerformanceMonitor {

    private final long[] frameTimes = new long[60];
    private int frameIndex = 0;
    private long lastTickTime = System.nanoTime();
    private int tickCount = 0;

    // Для отслеживания памяти
    private long lastGcTime = 0;
    private final Runtime runtime = Runtime.getRuntime();

    public void tick() {
        long now = System.nanoTime();
        long delta = now - lastTickTime;
        lastTickTime = now;

        frameTimes[frameIndex % frameTimes.length] = delta;
        frameIndex++;
        tickCount++;
    }

    public int getAverageFps() {
        int count = Math.min(frameIndex, frameTimes.length);
        if (count == 0) return 60;

        long totalNanos = 0;
        for (int i = 0; i < count; i++) {
            totalNanos += frameTimes[i];
        }

        double avgNanos = (double) totalNanos / count;
        if (avgNanos <= 0) return 60;

        // Тик = 50мс, FPS оценивается из дельты тиков
        // Но мы измеряем тик, а не кадр. Приблизительно:
        return (int) Math.min(120, 1_000_000_000.0 / avgNanos);
    }

    public double getMemoryUsagePercent() {
        long max = runtime.maxMemory();
        long used = runtime.totalMemory() - runtime.freeMemory();
        return (double) used / max * 100.0;
    }

    public long getUsedMemoryMB() {
        return (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
    }

    public int getTickCount() {
        return tickCount;
    }
}
