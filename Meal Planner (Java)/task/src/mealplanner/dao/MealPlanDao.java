package mealplanner.dao;

import mealplanner.entities.Meal;
import mealplanner.entities.MealDayPlan;
import mealplanner.entities.MealDayPlan.MealType;
import mealplanner.entities.MealWeekPlan;
import mealplanner.entities.MealWeekPlan.Day;

import java.sql.*;
import java.util.List;
import java.util.Map;

public class MealPlanDao implements Dao<MealWeekPlan> {

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
                id SERIAL NOT NULL,
                meal_id integer NOT NULL,
                meal_type text NOT NULL,
                day text NOT NULL,
                PRIMARY KEY (id)
            )""";
    public static final String insertQuery = "INSERT INTO meal_plan (meal_id, meal_type, day) VALUES (?, ?, ?)";
    public static final String selectQuery = "SELECT * FROM meal_plan";
    public static final String selectByIdQuery = "SELECT * FROM meal_plan WHERE id = ?";
    public static final String selectByMealIdQuery = "SELECT * FROM meal_plan WHERE meal_id = ?";
    public static final String selectByMealTypeQuery = "SELECT * FROM meal_plan WHERE meal_type = ?";
    public static final String selectByDayQuery = "SELECT * FROM meal_plan WHERE day = ?";
    public static final String selectByMealIdAndDayQuery = "SELECT * FROM meal_plan WHERE meal_id = ? AND day = ?";
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

    @Override
    public void add(MealWeekPlan mealPlan) {
        deleteAll();
        try (PreparedStatement stmt = con.prepareStatement(insertQuery)) {
            for (MealDayPlan dayPlan : mealPlan.getDayPlans()) {
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

    @Override
    public MealWeekPlan get(int id) {
        MealWeekPlan mealPlan = new MealWeekPlan();
        try (Statement stmt = con.createStatement()) {
            ResultSet found = stmt.executeQuery(selectQuery);
            if (found.next()) {
                int mealId = found.getInt(2);
                MealType type = MealType.valueOf(found.getString(3));
                Day day = Day.valueOf(found.getString(4));
                Meal meal = mealDao.get(mealId);
                MealDayPlan dayPlan = mealPlan.getDayPlan(day);
                dayPlan.setMeal(type, meal);
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
        return mealPlan;
    }

    @Override
    public List<MealWeekPlan> getAll() {
        return List.of();
    }

    @Override
    public void update(MealWeekPlan mealPlan) {

    }

    public void deleteAll() {

    }

    @Override
    public void delete(MealWeekPlan mealPlan) {
        try (Statement stmt = con.createStatement()) {
            stmt.execute(deleteAllQuery);
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
    }
}
