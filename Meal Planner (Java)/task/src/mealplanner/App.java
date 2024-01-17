package mealplanner;

import mealplanner.entities.Meal;
import mealplanner.io.Input;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {
    private final Input input = new Input();
    private final PrintStream out = System.out;
    private final List<Meal> meals = new ArrayList<>();

    public void run() {
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
    }

    private void addMenuItem() {
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

        meals.add(meal);
        out.println("The meal has been added!");
    }

    private void showMenuItems() {
        if (meals.isEmpty()) {
            out.println("No meals saved. Add a meal first.");
        } else {
            meals.stream()
                    .map(Meal::createPrintRecord)
                    .forEach(out::println);
        }
    }

    public void close() {
        input.close();
    }
}
