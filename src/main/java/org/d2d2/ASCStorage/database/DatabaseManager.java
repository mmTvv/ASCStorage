package ord.d2d2.ASCStorage.database;

import ord.d2d2.ASCStorage.ASCStoragePlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private Connection connection;

    // Настройка базы данных
    public void setupDatabase() {
        try {
            String url = "jdbc:sqlite:" + ASCStoragePlugin.getInstance().getConfig().getString("database.path");
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS shulkers (uuid TEXT PRIMARY KEY, content TEXT);");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Получение соединения с базой данных
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = "jdbc:sqlite:" + ASCStoragePlugin.getInstance().getConfig().getString("database.path");
            connection = DriverManager.getConnection(url);
        }
        return connection;
    }

    // Закрытие соединения с базой данных
    public void closeDatabase() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
