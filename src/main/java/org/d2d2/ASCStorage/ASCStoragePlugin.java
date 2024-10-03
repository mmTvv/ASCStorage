package ord.d2d2.ASCStorage;

import ord.d2d2.ASCStorage.database.DatabaseManager;
import ord.d2d2.ASCStorage.listeners.ChunkLoadListener;
import ord.d2d2.ASCStorage.listeners.ShulkerReturnEvent;
import ord.d2d2.ASCStorage.listeners.DelUUIDonMove;
import org.bukkit.plugin.java.JavaPlugin;

public class ASCStoragePlugin extends JavaPlugin {

    private static ASCStoragePlugin instance;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        // Инициализация базы данных
        databaseManager = new DatabaseManager();
        databaseManager.setupDatabase();

        // Регистрация слушателей
        getServer().getPluginManager().registerEvents(new ChunkLoadListener(), this);
        getServer().getPluginManager().registerEvents(new ShulkerReturnEvent(), this);
        getServer().getPluginManager().registerEvents(new DelUUIDonMove(), this);

        getLogger().info("ASCStorage plugin enabled.");
    }

    @Override
    public void onDisable() {
        databaseManager.closeDatabase();
        getLogger().info("ASCStorage plugin disabled.");
    }

    public static ASCStoragePlugin getInstance() {
        return instance;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}

