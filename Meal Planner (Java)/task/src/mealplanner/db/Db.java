package mealplanner.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class Db {
    private static Connection connection = null;


    public static Connection getConnection() throws SQLException {
        return getConnection(System.getenv());
    }

    public static Connection getConnection(Map<String, String> env) throws SQLException {
        if (connection == null) {
            connection = createConnection(env);
            connection.setAutoCommit(true);
            if (!connection.isValid(5)) {
                throw new SQLException("Db not available");
            }
        }
        return connection;
    }

    private static Connection createConnection(Map<String, String> env) throws SQLException {
//        String url = "jdbc:postgresql://localhost:5432/%s".formatted(env.getOrDefault("POSTGRES_DB", "meals_db"));
//        String user = env.getOrDefault("POSTGRES_USER", "postgres");
//        String password = env.getOrDefault("POSTGRES_PASSWORD", "1111");
        String url = "jdbc:postgresql:meals_db";
        String user = "postgres";
        String password = "1111";
        return DriverManager.getConnection(url, user, password);
    }

    public static void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
            System.out.println("Connection closed");
        }
    }
}
