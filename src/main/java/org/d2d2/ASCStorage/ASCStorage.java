package org.d2d2.ASCStorage;

import org.bukkit.plugin.java.JavaPlugin;

public class ASCStorage extends JavaPlugin {
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        databaseManager = new DatabaseManager(this);
        getServer().getPluginManager().registerEvents(new EventListener(databaseManager), this);
    }

    @Override
    public void onDisable() {
        databaseManager.close();
    }
}
