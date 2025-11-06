import util.FileIO;
import util.TextUI;

import java.util.ArrayList;

public class MainMenu {
    TextUI ui = new TextUI();
    FileIO io = new FileIO();
    User user = new User("user");
    private ArrayList<User>users = new ArrayList<>();


    public void createUser(String name){
        User user = new User(name);
        users.add(user);
    }

    public void runMJO(){
        ui.displayMsg("Welcome to MJO " + user.getName());
    }

}
