package util;

import java.util.ArrayList;
import java.util.Scanner;

public class TextUI {
    private static Scanner sc = new Scanner(System.in);

    // Viser en besked til brugeren
    public void displayMsg(String msg){
        System.out.println(msg);
    }

    // Spørger efter tekst
    public String promptText(String msg){
        displayMsg(msg);
        return sc.nextLine();
    }

    // Spørger efter tal
    public int promptNumeric(String msg){
        displayMsg(msg);
        String input = sc.nextLine();
        return Integer.parseInt(input);
    }

    // Viser en nummereret liste
    public void displayList(ArrayList<String> list){
        for (int i = 0; i < list.size(); i++) {
            System.out.println((i+1) + ". " + list.get(i));
        }
    }
}
