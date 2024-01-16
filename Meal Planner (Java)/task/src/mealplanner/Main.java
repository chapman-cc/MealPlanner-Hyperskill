package mealplanner;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Meal meal = new Meal();

        System.out.println("Which meal do you want to add (breakfast, lunch, dinner)? ");
        meal.setType(MealType.valueOf(scanner.nextLine().toUpperCase()));
        System.out.println("Input the meal's name: ");
        meal.setName(scanner.nextLine());
        System.out.println("Input the ingredients: ");
        meal.setIngredients(Arrays.stream(scanner.nextLine().split(",")).map(String::trim).toList());

        System.out.println(meal.createPrintRecord());
        System.out.println("The meal has been added!");
    }
}