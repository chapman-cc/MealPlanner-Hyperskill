package mealplanner.db;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    public static void main(String[] args) {
        String dir = "/home/chapman/IdeaProjects/Meal Planner (Java)/db";
        String url = "jdbc:sqlite:%s/db.sqlite".formatted(dir);
        String memoryUrl = "jdbc:sqlite::memory:";

        try (Connection con = makeSqLiteConnection(memoryUrl)){
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    public static Connection makeSqLiteConnection(String url) throws SQLException {
        try (Connection con = DriverManager.getConnection(url)) {
            if (con.isValid(10)) {
                System.out.println("sqlite connected");
                return con;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

//    public static Connection makeHyperSkillConnection(String url) {
//        SQLiteDataSource dataSource = new SQLiteDataSource();
//        dataSource.setUrl(url);
//        try (Connection con = dataSource.getConnection()) {
//            if (con.isValid(5)) {
//                System.out.println("Connection is valid");
//                return con;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace(System.out);
//        }
//
//        return null;
//    }
}
