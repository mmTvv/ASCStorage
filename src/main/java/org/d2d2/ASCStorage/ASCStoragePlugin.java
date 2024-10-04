package ord.d2d2.ASCStorage;

import ord.d2d2.ASCStorage.database.DatabaseManager;
import ord.d2d2.ASCStorage.listeners.ChunkLoadListener;
import ord.d2d2.ASCStorage.listeners.ShulkerReturnEvent;
import ord.d2d2.ASCStorage.listeners.DelUUIDonMove;
import ord.d2d2.ASCStorage.utils.ShulkerUtils;
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

        // Планирование авто-сохранения изменений в БД
        int interval = getConfig().getInt("database.auto-save-interval", 5) * 60 * 20; // Интервал в тиках (минуты -> тики)
        getServer().getGlobalRegionScheduler().runAtFixedRate(
            this, 
            taskContext -> ShulkerUtils.flushPendingChangesToDatabase(),
            interval, 
            interval
        ); // Запуск каждые interval

        getLogger().info("ASCStorage plugin enabled.");
    }

    @Override
    public void onDisable() {
        // Сохранение изменений перед отключением
        ShulkerUtils.flushPendingChangesToDatabase();
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
