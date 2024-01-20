package mealplanner.entities;

public class Ingredient {
    private int id;
    private String ingredient;
    private int meal_id;

    public Ingredient(String name, int meal_id) {
        this(-1, name, meal_id);
    }

    public Ingredient(int id, String ingredient, int meal_id) {
        this.id = id;
        this.ingredient = ingredient;
        this.meal_id = meal_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public int getMeal_id() {
        return meal_id;
    }

    public void setMealId(int meal_id) {
        this.meal_id = meal_id;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + ingredient + '\'' +
                ", meal_id=" + meal_id +
                '}';
    }
}
