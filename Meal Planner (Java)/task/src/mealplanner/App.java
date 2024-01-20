package mealplanner;

import mealplanner.dao.MealDao;
import mealplanner.dao.MealPlanDao;
import mealplanner.db.Db;
import mealplanner.entities.Ingredient;
import mealplanner.entities.Meal;
import mealplanner.entities.MealDayPlan;
import mealplanner.entities.MealWeekPlan;
import mealplanner.io.Input;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class App {
    public static enum Command {
        ADD, SHOW, PLAN, SAVE, EXIT;

        public static String formattedPrint() {
            return Arrays.stream(values()).map(Enum::name).map(String::toLowerCase).collect(Collectors.joining(", "));
        }
    }

    private final Input input;
    private final PrintStream out;
    private final MealDao mealsDao;
    private final MealPlanDao planDao;

    public App(Map<String, String> env) throws SQLException {
        Connection connection = Db.getConnection(env);
        this.input = new Input();
        this.out = System.out;
        this.mealsDao = new MealDao(connection);
        this.planDao = new MealPlanDao(connection);
        this.mealsDao.createTable();
        this.planDao.createTable();

//        List<Meal> meals = List.of(
////        generate Meals
//                new Meal(1, "breakfast", "eggs", Arrays.asList(new Ingredient("egg", 1), new Ingredient("milk", 1))),
//                new Meal(2, "breakfast", "bacon", Arrays.asList(new Ingredient("bacon", 1), new Ingredient("milk", 1))),
//                new Meal(3, "breakfast", "toast", Arrays.asList(new Ingredient("toast", 1), new Ingredient("milk", 1))),
//                new Meal(4, "breakfast", "coffee", Arrays.asList(new Ingredient("coffee", 1), new Ingredient("milk", 1))),
//                new Meal(5, "breakfast", "tea", Arrays.asList(new Ingredient("tea", 1), new Ingredient("milk", 1))),
////        generate lunches
//                new Meal(6, "lunch", "salad", Arrays.asList(new Ingredient("salad", 1), new Ingredient("milk", 1))),
//                new Meal(7, "lunch", "soup", Arrays.asList(new Ingredient("soup", 1), new Ingredient("milk", 1))),
//                new Meal(8, "lunch", "sandwich", Arrays.asList(new Ingredient("sandwich", 1), new Ingredient("milk", 1))),
//                new Meal(9, "lunch", "pizza", Arrays.asList(new Ingredient("pizza", 1), new Ingredient("milk", 1))),
//                new Meal(10, "lunch", "burger", Arrays.asList(new Ingredient("burger", 1), new Ingredient("milk", 1))),
//
////        generate Dinner
//                new Meal(11, "dinner", "steak", Arrays.asList(new Ingredient("steak", 1), new Ingredient("milk", 1))),
//                new Meal(12, "dinner", "pasta", Arrays.asList(new Ingredient("pasta", 1), new Ingredient("milk", 1))),
//                new Meal(13, "dinner", "chicken", Arrays.asList(new Ingredient("chicken", 1), new Ingredient("milk", 1))),
//                new Meal(14, "dinner", "fish", Arrays.asList(new Ingredient("fish", 1), new Ingredient("milk", 1))),
//                new Meal(15, "dinner", "beef", Arrays.asList(new Ingredient("beef", 1), new Ingredient("milk", 1)))
//        );
//        for (Meal meal : meals) {
//            int mealId = mealsDao.add(meal);
//            meal.getIngredients().forEach(ingredient -> ingredient.setMealId(mealId));
//            ingredientDao.addAll(meal.getIngredients().toArray(new Ingredient[0]));
//        }

    }


    public void run() {
        try {
            while (true) {
                String menuPrompt = "What would you like to do (%s)? ".formatted(Command.formattedPrint());
                out.println(menuPrompt);
                String selectedMenu = input.read(Input.verifyAppMenu, menuPrompt);

                switch (Command.valueOf(selectedMenu.toUpperCase())) {
                    case ADD -> addMenuItem();
                    case SHOW -> showMenuItems();
                    case PLAN -> planMenu();
                    case SAVE -> savePlan();
                    case EXIT -> {
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
        mealsDao.add(meal);
        out.println("The meal has been added!");
    }

    private Meal createMeal() {
        Meal meal = new Meal();
        String mealTypeString = Arrays.stream(MealDayPlan.MealType.values()).map(Enum::name).map(String::toLowerCase).reduce("", (partial, string) -> partial.isBlank() ? string : "%s, %s".formatted(partial, string));
        out.println("Which meal do you want to add (" + mealTypeString + ")? ");
        String menuType = input.read(Input.verifyMealType, "Wrong meal category! Choose from: " + mealTypeString + ".");
        meal.setCategory(menuType);
        out.println("Input the meal's name: ");
        String name = input.read(Input.verifyMealName, "Wrong format. Use letters only!");
        meal.setMeal(name);
        out.println("Input the ingredients: ");
        String ingredientsString = input.read(Input.verifyMealIngredients, "Wrong format. Use letters only!");
        List<Ingredient> list = Arrays.stream(ingredientsString.split(",")).map(String::trim).map(string -> new Ingredient(string, meal.getId())).toList();
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
        out.printf("Category: %s%n", mealType);
        for (Meal meal : meals) {
            out.println(meal.createPrintRecord());
        }
    }

    private void planMenu() {
        MealWeekPlan mealPlan = new MealWeekPlan();
        StringBuilder sb = new StringBuilder();
        for (MealWeekPlan.Day weekday : MealWeekPlan.Day.values()) {
            MealDayPlan dayPlan = mealPlan.getDayPlan(weekday);
            out.println(weekday.name().toLowerCase());

            for (MealDayPlan.MealType mealType : MealDayPlan.MealType.values()) {
                String mealTypeName = mealType.name().toLowerCase();
                List<Meal> meals = mealsDao.getByCategory(mealTypeName);
                List<String> mealNames = meals.stream().map(Meal::getMeal).sorted().toList();
                mealNames.forEach(name -> sb.append(name).append("\n"));
                out.println(sb);
                out.printf("Choose the %s for %s from the list above:%n", mealTypeName, dayPlan.getCapitalizedDay());
                String selectedMeal = input.read(mealNames::contains, "This meal doesn’t exist. Choose a meal from the list above.");
                meals.stream().filter(meal -> meal.getMeal().equals(selectedMeal)).findFirst().ifPresent(meal -> dayPlan.setMeal(mealType, meal));
                sb.setLength(0);
            }
            if (dayPlan.isFull()) {
                out.printf("Yeah! We planned the meals for %s.%n", dayPlan.getCapitalizedDay());
            }
        }

        for (MealDayPlan dayPlan : mealPlan.getDayPlansAsList()) {
            out.println(dayPlan.getFormattedPrint());
        }

        planDao.add(mealPlan);
    }

    public void savePlan() {
        List<Meal> meals = planDao.get().getPlan().values().stream().flatMap(dp -> dp.getPlan().values().stream()).toList();

        List<String> list = planDao.getShoppingList();
        if (list.isEmpty()) {
            out.println("Unable to save. Plan your meals first.");
            return;
        }
        out.println("Input a filename:");
        String fileName = input.read();

        try (FileWriter writer = new FileWriter(fileName, true)) {
            for (String line : list) {
                writer.write(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        out.println("Saved!");
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
