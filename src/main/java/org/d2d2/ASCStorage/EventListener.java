package org.d2d2.ASCStorage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.File;

public class DatabaseManager {
    private Connection connection;
    private final ASCStorage plugin;

    public DatabaseManager(ASCStorage plugin) {
        this.plugin = plugin;
        createDatabase();
    }

    private void createDatabase() {
        try {
            File folder = new File(plugin.getDataFolder(), "ASCStorage");
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File databaseFile = new File(folder, "data.db");
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath());

            // Создание таблицы для хранения сундуков и шалкеров
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS tile_entities (id INTEGER PRIMARY KEY, type TEXT, data TEXT)");
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not connect to database: " + e.getMessage());
        }
    }

    // Метод для пакетного сохранения данных в базу
    public void saveTileEntitiesBatch(List<TileEntityData> tileEntities) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO tile_entities(type, data) VALUES(?, ?)");
            for (TileEntityData entity : tileEntities) {
                pstmt.setString(1, entity.getType());
                pstmt.setString(2, entity.getData());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            System.err.println("Could not save tile entity: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Could not close database connection: " + e.getMessage());
        }
    }
}
