package util;

import java.util.Objects;

public class Series extends Media {
    private TextUI ui = new TextUI();

    public Series(String title, int releaseDate, int endDate, double rating, String category, int duration) {
        super(title, releaseDate, endDate, rating, category, duration);
    }

    @Override
    public void play(User user) {   //Tilføjet user som argument, så mediet bliver tilføjet til brugeren.
        String choice = ui.promptText("Do you want to: \n" + "1) Play " + getTitle()+ "\n2) Add to watch later \n" +
                "3) Delete from watch later\n0) Go back to main menu");
        switch (choice) {
            case "1" -> {
                user.addWatchedMedia(this); //Tilføjer det valgte medie til brugerens already watched arraylist.
                user.removeSavedForLater(this); //Fjerner fra se senere.
                System.out.print(getTitle() + " is now playing... (" + getDuration() + " minutes)\n");
            }
            case "2" -> {
                if(user.getSavedForLater().contains(this)) {        //Checks if the user has already added the show.
                    ui.displayMsg(this.getTitle() + " is already in your saved for later list.");
                }else {
                    ui.promptText("Added " + getTitle() + " to watch later, press any key to return to main menu");
                    user.addSavedForLater(this);
                }
            }
            case "3" -> {
                if(user.getSavedForLater().contains(this)) {
                    ui.promptText(this.getTitle() + " was removed from your watch later list\nPress any button to return to the main menu");
                    user.removeSavedForLater(this);
                }else{
                    ui.displayMsg(this.getTitle() + " could not be removed because it was was never added to the list");
                    play(user);
                }
            }
            case "0" -> ui.displayMsg("Returning to main menu.");
            case null, default -> {
                ui.displayMsg("Invalid choice, try again");
                play(user);
            }
        }

    }
}
