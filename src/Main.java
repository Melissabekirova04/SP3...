import util.MainMenu;

public class Main {
    public static void main(String[] args) {
        try {
            MainMenu menu = new MainMenu();
            menu.startMenu();

        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());

        }
    }
}
