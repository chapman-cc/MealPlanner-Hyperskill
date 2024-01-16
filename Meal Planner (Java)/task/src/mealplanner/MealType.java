package mealplanner;

@Deprecated
public enum MealType {
    BREAKFAST, LUNCH, DINNER;

    public String Capitalize() {
        char capital = Character.toUpperCase(this.name().charAt(0));
        String rest = this.name().substring(1).toLowerCase();
        return capital + rest;
    }
}
