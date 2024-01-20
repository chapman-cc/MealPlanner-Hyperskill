package mealplanner.dao;

import mealplanner.entities.Ingredient;
import mealplanner.entities.Meal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MealDao implements Dao<Meal> {
    private static MealDao instance;

    public static MealDao getInstance(Connection connection) {
        if (instance == null) {
            instance = new MealDao(connection);
        }
        return instance;
    }

    public static final String createTableQuery = """
            CREATE TABLE IF NOT EXISTS meals (
                id          SERIAL      PRIMARY KEY,
                category    VARCHAR     NOT NULL,
                meal        VARCHAR     NOT NULL,
                meal_id     INTEGER
            )""";
    public static final String insertQuery = "INSERT INTO meals (category, meal) VALUES (?, ?)";
    public static final String selectQuery = "SELECT * FROM meals";
    public static final String selectByIdQuery = "SELECT * FROM meals WHERE id = ?";
    public static final String selectByCategoryQuery = "SELECT * FROM meals WHERE category = ?";
    public static final String updateQuery = "UPDATE meals set category = ?, meal = ? WHERE id = ?";
    public static final String deleteQuery = "DELETE FROM meals WHERE id = ?";
    private final Connection con;
    private final IngredientDao ingredientDao;

    public MealDao(Connection connection) {
        this.con = connection;
        this.ingredientDao = IngredientDao.getInstance(connection);
    }

    public void createTable() {
        try (Statement stmt = con.createStatement()) {
            stmt.execute(createTableQuery);
            ingredientDao.createTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void add(Meal meal) {
        try (PreparedStatement stmt = con.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, meal.getCategory());
            stmt.setString(2, meal.getMeal());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    meal.setId(keys.getInt(1));
                    ingredientDao.addAll(meal.getIngredients().toArray(new Ingredient[0]));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
    }

    @Override
    public Meal get(int id) {
        try (PreparedStatement stmt = con.prepareStatement(selectByIdQuery)) {
            stmt.setInt(1, id);
            try (ResultSet found = stmt.executeQuery()) {
                if (found.next()) {
                    int idRef = found.getInt("id");
                    String type = found.getString("category");
                    String name = found.getString("meal");
                    List<Ingredient> ingredients = ingredientDao.getByMeal(idRef);
                    return new Meal(idRef, type, name, ingredients);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }

        return null;
    }

    @Override
    public List<Meal> getAll() {
        List<Meal> meals = new ArrayList<>();
        try (
                PreparedStatement stmt = con.prepareStatement(selectQuery);
                ResultSet found = stmt.executeQuery()
        ) {
            while (found.next()) {
                int id = found.getInt("id");
                String type = found.getString("category");
                String name = found.getString("meal");
                List<Ingredient> ingredients = ingredientDao.getByMeal(id);
                Meal meal = new Meal(id, type, name, ingredients);
                meals.add(meal);
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }

        return meals;
    }

    public List<Meal> getByCategory(String category) {
        List<Meal> meals = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(selectByCategoryQuery)) {
            stmt.setString(1, category);
            try (ResultSet found = stmt.executeQuery()) {
                while (found.next()) {
                    int id = found.getInt("id");
                    String type = found.getString("category");
                    String name = found.getString("meal");
                    List<Ingredient> ingredients = ingredientDao.getByMeal(id);
                    Meal meal = new Meal(id, type, name, ingredients);
                    meals.add(meal);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }

        return meals;
    }

    @Override
    public void update(Meal meal) {
        try (PreparedStatement stmt = con.prepareStatement(updateQuery)) {
            stmt.setString(1, meal.getCategory());
            stmt.setString(2, meal.getMeal());
            stmt.setInt(3, meal.getId());
            stmt.executeUpdate();
            for (Ingredient ingredient : meal.getIngredients()) {
                ingredientDao.update(ingredient);
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }

    }

    @Override
    public void delete(Meal meal) {
        try (PreparedStatement stmt = con.prepareStatement(deleteQuery)) {
            stmt.setInt(1, meal.getId());
            stmt.executeUpdate();
            for (Ingredient ingredient : meal.getIngredients()) {
                ingredientDao.delete(ingredient);
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }

    }
}
