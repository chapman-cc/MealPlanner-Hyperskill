package mealplanner.db;

public class Queries {
    public static String mealSchema = """
            CREATE TABLE IF NOT EXISTS meals (
                category VARCHAR(255),
                meal VARCHAR(255),
                meal_id INTEGER
            )""";
    public static String ingredientSchema = """
            CREATE TABLE IF NOT EXISTS ingredients (
                ingredient VARCHAR(255),
                ingredient_id INTEGER,
                meal_id INTEGER
            )""";
    public static String mealQuery = "SELECT * FROM meals";
    public static String ingredientQuery = "SELECT * FROM ingredients WHERE meal_id = ?";

    /*
     * 1. category VARCHAR(255),
     * 2. meal VARCHAR(255),
     * 3. meal_id INTEGER
     * */
    public static String insertmealQuery = "INSERT INTO meals (category, meal, meal_id) VALUES (?, ?, ?)";


    /*
     * Insert order:
     * 1. ingredient VARCHAR
     * 2. ingredient_id INTEGER
     * 3. meal_id INTEGER
     * */
    public static String insertIngredientQuery = "INSERT INTO ingredients (ingredient, ingredient_id, meal_id) VALUES (?, ?, ?)";

}
