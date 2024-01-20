package mealplanner.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Meal {
    private int id;
    private String category;
    private String meal;
    private List<Ingredient> ingredients;

    public Meal() {
        this.id = -1;
        this.category = "";
        this.meal = "";
        this.ingredients = new ArrayList<>();
    }

    public Meal(int id, String type, String name) {
        this(id, type, name, new ArrayList<>());
    }

    public Meal(int id, String type, String name, List<Ingredient> ingredients) {
        this.id = id;
        this.category = type;
        this.meal = name;
        this.ingredients = ingredients;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        this.ingredients.forEach(ingredient -> ingredient.setMealId(id));
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    public String createPrintRecord() {
        StringBuilder sb = new StringBuilder();
        sb
//                .append("Category: ").append(category).append("\n")
                .append("Name: ").append(meal).append("\n")
                .append("Ingredients: ").append("\n");
        ingredients.forEach(ingredient -> sb.append(ingredient.getIngredient()).append("\n"));
        return sb.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Meal meal = (Meal) object;
        return Objects.equals(category, meal.category) && Objects.equals(this.meal, meal.meal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, meal);
    }
}
