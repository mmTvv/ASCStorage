package org.d2d2.ASCStorage;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DatabaseManager {
    private Connection connection;
    private final ASCStorage plugin;

    public DatabaseManager(ASCStorage plugin) {
        this.plugin = plugin;
        createDatabase();
    }

    private void createDatabase() {
        try {
            // Создание директории для базы данных
            File folder = new File(plugin.getDataFolder(), "ASCStorage");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // Путь к базе данных
            File databaseFile = new File(folder, "data.db");
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath());

            // Создание таблицы с дополнительным столбцом для данных
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS tile_entities (id INTEGER PRIMARY KEY, data TEXT, additional_data TEXT)");
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not connect to database: " + e.getMessage());
        }
    }

    // Метод для пакетной записи данных
    public void saveTileEntitiesBatch(List<TileEntityData> entities) {
        try {
            String sql = "INSERT INTO tile_entities(data, additional_data) VALUES(?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);

            for (TileEntityData entity : entities) {
                pstmt.setString(1, entity.getData());
                pstmt.setString(2, entity.getAdditionalData());
                pstmt.addBatch();
            }

            pstmt.executeBatch();
        } catch (SQLException e) {
            System.err.println("Could not save tile entities: " + e.getMessage());
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
