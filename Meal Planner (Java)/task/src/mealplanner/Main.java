package mealplanner;

public class Main {
    public static void main(String[] args) {
        App app = new App();
        try {
            app.run();
        } finally {
            app.close();
        }
    }
}