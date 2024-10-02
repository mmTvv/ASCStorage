package ord.d2d2.ASCStorage.database;

import ord.d2d2.ASCStorage.ASCStoragePlugin;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private Connection connection;

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

    public Connection getConnection() {
        return connection;
    }

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

