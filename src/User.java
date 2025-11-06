import util.TextUI;

public class User {
    TextUI ui = new TextUI();
    private String name;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
