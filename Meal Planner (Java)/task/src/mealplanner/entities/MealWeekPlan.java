package mealplanner.entities;

import java.util.*;
import java.util.stream.Collectors;

public class MealWeekPlan {
    public enum Day {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;
    }

    private int id;

    private final Map<Day, MealDayPlan> plan;

    public MealWeekPlan() {
        this.plan = new HashMap<>(Arrays.stream(Day.values())
                .collect(Collectors.toMap(day -> day, MealDayPlan::new)));
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<Day, MealDayPlan> getPlan() {
        return plan;
    }

    public boolean isValid() {
        return plan.values().stream().allMatch(MealDayPlan::isFull);
    }

    public List<MealDayPlan> getDayPlansAsList() {
        List<MealDayPlan> list = new ArrayList<>(plan.values().size());
        for (Day day : Day.values()) {
            MealDayPlan plan = this.plan.get(day);
            list.add(plan);
        }
        return list;
    }

    public MealDayPlan getDayPlan(Day day) {
        return plan.get(day);
    }

    public void setDayPlan(MealDayPlan dayPlan) {
        plan.put(dayPlan.getDay(), dayPlan);
    }

    public void setDayPlan(Day day, MealDayPlan dayPlan) {
        plan.put(day, dayPlan);
    }


}
