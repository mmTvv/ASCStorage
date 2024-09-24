package org.d2d2.ASCStorage;

import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ASCStorage extends JavaPlugin {
    private DatabaseManager databaseManager;
    private final List<TileEntityData> tileEntitiesBuffer = new ArrayList<>();

    @Override
    public void onEnable() {
        databaseManager = new DatabaseManager(this);
        getServer().getPluginManager().registerEvents(new EventListener(databaseManager, tileEntitiesBuffer), this);

        // Используем RegionScheduler для выполнения задач каждые несколько секунд
        RegionScheduler scheduler = Bukkit.getRegionScheduler();
        scheduler.runAtFixedRate(
            this,
            getServer().getWorlds().get(0).getSpawnLocation(), // Используем spawnLocation для задачи
            (Consumer<ScheduledTask>) scheduledTask -> {
                if (!tileEntitiesBuffer.isEmpty()) {
                    databaseManager.saveTileEntitiesBatch(new ArrayList<>(tileEntitiesBuffer));
                    tileEntitiesBuffer.clear();
                }
            },
            200L, 1200L // Начальная задержка в 1 тик и повторение каждые 100 тиков (5 секунд)
        );
    }

    @Override
    public void onDisable() {
        databaseManager.close();
    }
}
