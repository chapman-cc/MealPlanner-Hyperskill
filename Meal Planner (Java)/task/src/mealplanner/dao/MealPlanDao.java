package mealplanner.dao;

import mealplanner.entities.Meal;
import mealplanner.entities.MealDayPlan;
import mealplanner.entities.MealDayPlan.MealType;
import mealplanner.entities.MealWeekPlan;
import mealplanner.entities.MealWeekPlan.Day;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MealPlanDao  {

    private static MealPlanDao instance;
    private final MealDao mealDao;

    public static MealPlanDao getInstance(Connection connection) {
        if (instance == null) {
            instance = new MealPlanDao(connection);
        }
        return instance;
    }

    public static final String createTableQuery = """
            CREATE TABLE IF NOT EXISTS meal_plan (
                id          SERIAL  NOT NULL,
                meal_id     integer NOT NULL,
                meal_type   VARCHAR NOT NULL,
                day         VARCHAR NOT NULL,
                PRIMARY KEY (id)
            )""";
    public static final String insertQuery = "INSERT INTO meal_plan (meal_id, meal_type, day) VALUES (?, ?, ?)";
    public static final String selectQuery = "SELECT * FROM meal_plan";
    public static final String selectByIdQuery = "SELECT * FROM meal_plan WHERE id = ?";
    public static final String selectByMealIdQuery = "SELECT * FROM meal_plan WHERE meal_id = ?";
    public static final String selectByMealTypeQuery = "SELECT * FROM meal_plan WHERE meal_type = ?";
    public static final String selectByDayQuery = "SELECT * FROM meal_plan WHERE day = ?";
    public static final String selectByMealIdAndDayQuery = "SELECT * FROM meal_plan WHERE meal_id = ? AND day = ?";
    public static final String selectIngredientsAsShoppingList = """
            SELECT ingredient, count(ingredient) as count
            	FROM meal_plan as mp
            	INNER JOIN ingredients as i ON mp.meal_id = i.meal_id
            	GROUP BY ingredient
            	ORDER BY ingredient""";
    public static final String updateQuery = "UPDATE meal_plan set meal_id = ?, meal_type = ?, day = ? WHERE id = ?";
    public static final String deleteQuery = "DELETE FROM meal_plan WHERE id = ?";
    public static final String deleteAllQuery = "DELETE FROM meal_plan";
    private final Connection con;

    public MealPlanDao(Connection con) {
        this.con = con;
        this.mealDao = MealDao.getInstance(con);
    }

    public void createTable() {
        try (Statement stmt = con.createStatement()) {
            stmt.execute(createTableQuery);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void add(MealWeekPlan mealPlan) {
        deleteAll();
        try (PreparedStatement stmt = con.prepareStatement(insertQuery)) {
            for (MealDayPlan dayPlan : mealPlan.getDayPlansAsList()) {
                String day = dayPlan.getDay().name();
                for (Map.Entry<MealType, Meal> set : dayPlan.getPlan().entrySet()) {
                    String type = set.getKey().name();
                    Meal meal = set.getValue();
                    stmt.setInt(1, meal.getId());
                    stmt.setString(2, type);
                    stmt.setString(3, day);
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
    }

    public MealWeekPlan get() {
        MealWeekPlan mealPlan = new MealWeekPlan();
        try (Statement stmt = con.createStatement()) {
            ResultSet found = stmt.executeQuery(selectQuery);
            while (found.next()) {

                int mealId = found.getInt("meal_id");
                MealType type = MealType.valueOf(found.getString("meal_type"));
                Day day = Day.valueOf(found.getString("day"));
                Meal meal = mealDao.get(mealId);
                MealDayPlan dayPlan = mealPlan.getDayPlan(day);
                dayPlan.setMeal(type, meal);
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
        return mealPlan;
    }
    public List<String> getShoppingList(){
        List<String> lines = new ArrayList<>();
        try (Statement stmt = con.createStatement()) {
            ResultSet found = stmt.executeQuery(selectIngredientsAsShoppingList);
            while (found.next()) {
               String ingredient =found.getString("ingredient");
                int count = found.getInt("count");
                if (count > 1) {
                    lines.add("%s x%d%n".formatted(ingredient, count));
                } else {
                    lines.add("%s%n".formatted(ingredient));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
        return lines;
    }


    public void deleteAll() {
        try (Statement stmt = con.createStatement()) {
            stmt.execute(deleteAllQuery);
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
    }
}
