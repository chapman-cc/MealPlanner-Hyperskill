package mealplanner.dao;

import mealplanner.entities.Ingredient;
import org.postgresql.core.SqlCommand;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IngredientDao implements Dao<Ingredient> {
    public static final String createTableQuery = """
            CREATE TABLE IF NOT EXISTS ingredients (
                id          SERIAL      PRIMARY KEY,
                ingredient  TEXT        NOT NULL,
                meal_id     INTEGER
            )""";
    public static final String insertQuery = "INSERT INTO ingredients (ingredient, meal_id) VALUES (?, ?)";
    public static final String selectQuery = "SELECT * FROM ingredients";
    public static final String selectByIdQuery = "SELECT * FROM ingredients WHERE id = ? ORDER BY id ASC";
    public static final String selectByMealIdQuery = "SELECT * FROM ingredients WHERE meal_id = ?";
    public static final String updateQuery = "UPDATE ingredients set ingredient = ?, meal_id = ? WHERE id = ?";
    public static final String deleteQuery = "DELETE FROM ingredients WHERE id = ?";
    private final Connection con;

    public IngredientDao(Connection connection) {
        this.con = connection;
    }

    public void createTable() {
        try (Statement stmt = con.createStatement();) {
            stmt.execute(createTableQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public int add(Ingredient ingredient) {
        try (PreparedStatement stmt = con.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, ingredient.getIngredient());
            stmt.setInt(2, ingredient.getMeal_id());
            stmt.executeUpdate();
            try (ResultSet set = stmt.getGeneratedKeys()) {
                if (set.next()) {
                    return set.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void addAll(Ingredient... ingredients) {
        try (PreparedStatement stmt = con.prepareStatement(insertQuery)) {
            for (Ingredient ingredient : ingredients) {
                stmt.setString(1, ingredient.getIngredient());
                stmt.setInt(2, ingredient.getMeal_id());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Ingredient get(int id) {
        try (
                PreparedStatement stmt = con.prepareStatement(selectByIdQuery);
                ResultSet found = stmt.executeQuery();
        ) {
            while (found.next()) {
                return new Ingredient(
                        found.getInt("id"),
                        found.getString("ingredient"),
                        found.getInt("meal_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Ingredient> getByMeal(int id) {
        List<Ingredient> ingredients = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(selectByMealIdQuery);) {
            stmt.setInt(1, id);
            try (ResultSet found = stmt.executeQuery()) {
                while (found.next()) {
                    Ingredient ingredient = new Ingredient(
                            found.getInt("id"),
                            found.getString("ingredient"),
                            found.getInt("meal_id")
                    );
                    ingredients.add(ingredient);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredients;  //To change body of created methods use File | Settings | File Templates.
    }

    @Override
    public List<Ingredient> getAll() {
        List<Ingredient> ingredients = new ArrayList<>();
        try (
                PreparedStatement stmt = con.prepareStatement(selectQuery);
                ResultSet found = stmt.executeQuery();
        ) {
            while (found.next()) {
                Ingredient ingredient = new Ingredient(
                        found.getInt("id"),
                        found.getString("ingredient"),
                        found.getInt("meal_id")
                );
                ingredients.add(ingredient);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return ingredients;
    }

    @Override
    public void update(Ingredient ingredient) {
        try (PreparedStatement stmt = con.prepareStatement(updateQuery)) {
            stmt.setString(1, ingredient.getIngredient());
            stmt.setInt(2, ingredient.getMeal_id());
            stmt.setInt(3, ingredient.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Ingredient ingredient) {
        try (PreparedStatement stmt = con.prepareStatement(deleteQuery);) {
            stmt.setInt(1, ingredient.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
