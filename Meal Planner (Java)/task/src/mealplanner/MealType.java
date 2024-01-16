package mealplanner;

public enum MealType {
    BREAKFAST, LUNCH, DINNER;

    public String Capitalize() {
        String capital = this.name().substring(0, 1);
        String rest = this.name().substring(1);
        return capital + rest;
    }
}
