package org.d2d2.ASCStorage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {
    private Connection connection;

    public DatabaseManager(ASCStorage plugin) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:tile_entities.db");
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS tile_entities (id INTEGER PRIMARY KEY, data TEXT)");
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not connect to database: " + e.getMessage());
        }
    }

    public void saveTileEntity(String data) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO tile_entities(data) VALUES(?)");
            pstmt.setString(1, data);
            pstmt.executeUpdate();
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
