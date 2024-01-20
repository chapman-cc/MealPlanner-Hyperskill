package mealplanner.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Meal {
    private int id;
    private String type;
    private String name;
    private List<String> ingredients;

    public Meal() {
        this.id = -1;
        this.type = "";
        this.name = "";
        this.ingredients = new ArrayList<>();
    }

    public Meal(int id , String type, String name) {
        this(type, name, new ArrayList<>());
    }

    public Meal(String type, String name, List<String> ingredients) {
        this.type = type;
        this.name = name;
        this.ingredients = ingredients;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
    public void addIngredient(String ingredient) {
        ingredients.add(ingredient);
    }

    public String createPrintRecord() {
        StringBuilder sb = new StringBuilder();
        sb
                .append("Category: ").append(type).append("\n")
                .append("Name: ").append(name).append("\n")
                .append("Ingredients: ").append("\n");
        ingredients.forEach(ingredient -> sb.append(ingredient).append("\n"));
        return sb.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Meal meal = (Meal) object;
        return Objects.equals(type, meal.type) && Objects.equals(name, meal.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name);
    }
}
