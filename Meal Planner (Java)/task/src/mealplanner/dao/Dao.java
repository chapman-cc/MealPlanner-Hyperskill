package mealplanner.dao;

import java.util.List;

public interface Dao<T> {
    void add(T t);

    T get(int id);

    List<T> getAll();

    void update(T t);

    void delete(T t);
}
