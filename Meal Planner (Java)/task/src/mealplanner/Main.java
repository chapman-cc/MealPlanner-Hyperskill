package mealplanner;

public class Main {
    public static void main(String[] args) {
        App app = null;
        try {
            app = new App(System.getenv());
            app.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (app != null) app.close();
        }
    }
}