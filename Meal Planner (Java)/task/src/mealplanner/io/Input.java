package mealplanner.io;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Input {

    private final Scanner scanner;
    private Pattern defaultPattern;

    public Input() {
        this(Pattern.compile(".+"));
    }

    public Input(Pattern pattern) {
        this.scanner = new Scanner(System.in);
        this.defaultPattern = pattern;
    }

    public void setDefaultPattern(Pattern defaultPattern) {
        this.defaultPattern = defaultPattern;
    }

    public void close() {
        scanner.close();
    }


    public String read() {
        return read(string -> defaultPattern.matcher(string).matches(), "");
    }

    public String read(String regex, String errorMsg) {
        Pattern pattern = Pattern.compile(regex);
        Predicate<String> stringPredicate = string -> pattern.matcher(string).matches();
        return read(stringPredicate, errorMsg);
    }

    public String read(Pattern pattern, String errorMsg) {
        return read(string -> pattern.matcher(string).matches(), errorMsg);
    }

    public String read(Predicate<String> predicate, String errorMsg) {
        while (true) {
            try {
                String input = scanner.nextLine();
                if (input.isBlank() || !predicate.test(input)) {
                    throw new InputMismatchException(errorMsg);
                }
                return input;
            } catch (InputMismatchException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static Pattern AppMenuRegex = Pattern.compile("\\b(add|show|plan|exit)\\b");
    public static Predicate<String> verifyAppMenu = string -> AppMenuRegex.matcher(string).matches();
    public static Pattern MealTypeRegex = Pattern.compile("\\b(breakfast)|(lunch)|(dinner)\\b");
    public static Predicate<String> verifyMealType = string -> MealTypeRegex.matcher(string).matches();
    public static Pattern MealNameRegex = Pattern.compile("^[a-z\\s]+$");
    public static Predicate<String> verifyMealName = string -> MealNameRegex.matcher(string).matches();
    public static Pattern MealIngredientsRegex = Pattern.compile("\\b[a-z\\s]+\\b");
    public static Predicate<String> verifyMealIngredients = ingredients -> {
        return Arrays.stream(ingredients.split(","))
                .map(String::trim)
                .allMatch(ingredient -> MealIngredientsRegex.matcher(ingredient).matches());
    };
}
