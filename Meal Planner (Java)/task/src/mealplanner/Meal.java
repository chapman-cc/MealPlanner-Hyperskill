package mealplanner;

import java.util.List;

public class Meal {
    private String type;
    private String name;
    private List<String> ingredients;

    public Meal() {
        this("", "", List.of());
    }

    public Meal(String type, String name, List<String> ingredients) {
        this.type = type;
        this.name = name;
        this.ingredients = ingredients;
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

    public String createPrintRecord() {
        StringBuilder sb = new StringBuilder();
        sb
                .append("Category: ").append(type).append("\n")
                .append("Name: ").append(name).append("\n")
                .append("Ingredients: ").append("\n");
        ingredients.forEach(ingredient -> sb.append(ingredient).append("\n"));
        return sb.toString();
    }
}
