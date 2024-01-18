package mealplanner.db;

import mealplanner.entities.Meal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MealsRepository {
    private final List<Meal> meals;
    private final Connection con;

    public MealsRepository(Connection connection) throws SQLException {
        this.meals = new ArrayList<>();
        this.con = connection;
        setupTables();
        loadMeals();
    }

    public List<Meal> getMeals() {
        return meals;
    }

    private void setupTables() throws SQLException {
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(Queries.mealSchema);
            stmt.executeUpdate(Queries.ingredientSchema);
        }
    }

    private void loadMeals() throws SQLException {
        try (Statement stmt = con.createStatement()) {
            try (ResultSet mealsResult = stmt.executeQuery(Queries.mealQuery)) {
                while (mealsResult.next()) {
                    int id = mealsResult.getInt("meal_id");
                    String type = mealsResult.getString("category");
                    String name = mealsResult.getString("meal");
                    Meal meal = new Meal(id, type, name);
                    meals.add(meal);

                    try (PreparedStatement ingredientQueryStmt = con.prepareStatement(Queries.ingredientQuery)) {
                        ingredientQueryStmt.setInt(1, mealsResult.getInt("meal_id"));
                        try (ResultSet ingredientsResult = ingredientQueryStmt.executeQuery()) {
                            while (ingredientsResult.next()) {
                                String ingredient = ingredientsResult.getString("ingredient");
                                meal.addIngredient(ingredient);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean addMeal(Meal meal) throws SQLException {
        if (meals.contains(meal)) {
            return false;
        }

        meals.add(meal);
        int mealIdx = meals.size() - 1;
        try (PreparedStatement stmt = con.prepareStatement(Queries.insertmealQuery)) {
            stmt.setString(1, meal.getType());
            stmt.setString(2, meal.getName());
            stmt.setInt(3, mealIdx);
            stmt.executeUpdate();
        }
        try (PreparedStatement stmt = con.prepareStatement(Queries.insertIngredientQuery)) {
            List<String> mealIngredients = meal.getIngredients();
            for (int i = 0; i < mealIngredients.size(); i++) {
                String ingredient = mealIngredients.get(i);
                stmt.setString(1, ingredient);
                stmt.setInt(2, i + 1);
                stmt.setInt(3, mealIdx);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
        return true;
    }


}
