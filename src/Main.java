import util.TextUI;

public class Main {
    public static void main(String[] args) {
        MainMenu menu = new MainMenu();
        TextUI ui = new TextUI();

        String name = ui.promptText("Please enter your name: ");
        menu.createUser(name);
        menu.runMJO();

    }
}
