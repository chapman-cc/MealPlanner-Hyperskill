package mealplanner;

import mealplanner.dao.IngredientDao;
import mealplanner.dao.MealDao;
import mealplanner.db.Db;
import mealplanner.entities.Ingredient;
import mealplanner.entities.Meal;
import mealplanner.io.Input;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class App {
    private final Input input;
    private final PrintStream out;
    private final MealDao mealsDao;
    private final IngredientDao ingredientDao;

    public App(Map<String, String> env) throws SQLException {
        Connection connection = Db.getConnection(env);
        this.input = new Input();
        this.out = System.out;
        this.mealsDao = new MealDao(connection);
        this.ingredientDao = new IngredientDao(connection);
        this.mealsDao.createTable();
        this.ingredientDao.createTable();
    }


    public void run() {
        try {
            while (true) {
                String menuPrompt = "What would you like to do (add, show, exit)? ";
                out.println(menuPrompt);
                String selectedMenu = input.read(Input.verifyAppMenu, menuPrompt);
                switch (selectedMenu) {
                    case "add" -> addMenuItem();
                    case "show" -> showMenuItems();
                    case "exit" -> {
                        out.println("Bye!");
                        return;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void addMenuItem() throws SQLException {
//        TODO: check if recipe of same name exist
        Meal meal = createMeal();
        int mealId = mealsDao.add(meal);
        meal.getIngredients().forEach(ingredient -> ingredient.setMealId(mealId));
        ingredientDao.addAll(meal.getIngredients().toArray(new Ingredient[0]));
    }

    private Meal createMeal() {
        Meal meal = new Meal();
        out.println("Which meal do you want to add (breakfast, lunch, dinner)? ");
        String menuType = input.read(Input.verifyMealType, "Wrong meal category! Choose from: breakfast, lunch, dinner.");
        meal.setCategory(menuType);
        out.println("Input the meal's name: ");
        String name = input.read(Input.verifyMealName, "Wrong format. Use letters only!");
        meal.setMeal(name);
        out.println("Input the ingredients: ");
        String ingredientsString = input.read(Input.verifyMealIngredients, "Wrong format. Use letters only!");
        List<Ingredient> list = Arrays.stream(ingredientsString
                        .split(","))
                        .map(String::trim)
                        .map(string -> new Ingredient(string, meal.getId()))
                        .toList();
        meal.setIngredients(list);
        return meal;
    }

    private void showMenuItems() throws SQLException {
        out.println("Which category do you want to print (breakfast, lunch, dinner)?");
        String mealType = input.read(Input.verifyMealType, "Wrong meal category! Choose from: breakfast, lunch, dinner.");
        List<Meal> meals = mealsDao.getByCategory(mealType);
        if (meals.isEmpty()) {
            out.println("No meals found.");
            return;
        }
        for (Meal meal : meals) {
            List<Ingredient> ingredients = ingredientDao.getByMeal(meal.getId());
            meal.setIngredients(ingredients);
        }

        out.printf("Category: %s%n", mealType);
        for (Meal meal : meals) {
            out.println(meal.createPrintRecord());
        }
    }

    public void close() {
        try {
            input.close();
            Db.closeConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
