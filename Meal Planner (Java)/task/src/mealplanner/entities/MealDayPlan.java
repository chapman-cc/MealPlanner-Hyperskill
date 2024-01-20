package mealplanner.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MealDayPlan {
    public enum MealType {
        BREAKFAST, LUNCH, DINNER;
    }

    private final Map<MealType, Meal> plan;
    private final MealWeekPlan.Day day;

    public MealDayPlan(MealWeekPlan.Day day) {
        this.day = day;
        this.plan = new HashMap<>();
    }

    public void setMeal(MealType type, Meal meal) {
        plan.put(type, meal);
    }

    public Map<MealType, Meal> getPlan() {
        return plan;
    }

    public MealWeekPlan.Day getDay() {
        return day;
    }
    public List<Meal> getMeals() {
        return new ArrayList<>(plan.values());
    }

    public boolean isEmpty() {
        return plan.isEmpty();
    }

    public boolean isFull() {
        return plan.values().size() == 3;
    }

    public String getCapitalizedDay() {
        return day.name().substring(0, 1).toUpperCase() + day.name().substring(1).toLowerCase();
    }

    public String getFormattedPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append(getCapitalizedDay()).append("\n");
        for (MealType mealType : MealType.values()) {
            Meal meal = plan.get(mealType);
            sb.append("%s: %s%n".formatted(mealType.name(), meal.getMeal()));
        }
        return sb.toString();
    }
}
