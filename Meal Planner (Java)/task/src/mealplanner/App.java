package mealplanner;

import mealplanner.db.Db;
import mealplanner.db.Queries;
import mealplanner.entities.Meal;
import mealplanner.entities.MealsRepository;
import mealplanner.io.Input;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Map;

public class App {
    private final Input input;
    private final PrintStream out;

    private final MealsRepository mealsRepo;

    public App(Map<String, String> env) throws SQLException {
        this.input = new Input();
        this.out = System.out;
        Connection con = Db.getConnection(env);
        this.mealsRepo = new MealsRepository(con);
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
        Meal meal = createMeal();
        if (mealsRepo.addMeal(meal)) {
            out.println("The meal has been added!");
        }
    }

    private Meal createMeal() {
        Meal meal = new Meal();
        out.println("Which meal do you want to add (breakfast, lunch, dinner)? ");
        String menuType = input.read(Input.verifyMealType, "Wrong meal category! Choose from: breakfast, lunch, dinner.");
        meal.setType(menuType);
        out.println("Input the meal's name: ");
        String name = input.read(Input.verifyMealName, "Wrong format. Use letters only!");
        meal.setName(name);
        out.println("Input the ingredients: ");
        String ingredientsString = input.read(Input.verifyMealIngredients, "Wrong format. Use letters only!");
        meal.setIngredients(Arrays.stream(ingredientsString.split(",")).map(String::trim).toList());
        return meal;
    }

    private void showMenuItems() throws SQLException {
        for (Meal meal : mealsRepo.getMeals()) {
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
